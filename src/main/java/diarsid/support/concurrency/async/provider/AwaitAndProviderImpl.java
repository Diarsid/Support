/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package diarsid.support.concurrency.async.provider;

import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static java.util.Objects.nonNull;
import static java.util.concurrent.CompletableFuture.runAsync;

import static diarsid.support.concurrency.async.provider.AsyncProviderState.PROVIDING_COMPLETED;
import static diarsid.support.concurrency.async.provider.AsyncProviderState.PROVIDING_FAILED;
import static diarsid.support.concurrency.async.provider.AsyncProviderState.PROVIDING_RUNNING;
import static diarsid.support.concurrency.async.provider.AsyncProviderState.PROVIDING_STOPPED;
import static diarsid.support.concurrency.threads.ThreadsUtil.sleepSafely;

/**
 *
 * @author Diarsid
 */
class AwaitAndProviderImpl<T> implements AwaitAndGet<T> {
    
    /* NON NULL fields */
    private final Supplier<T> tUnsafeSupplier;
    private final RepeatedWaiting waiting;
    private final Object supplyMonitor;
    private final Object getMonitor;
    
    /* NULLABLE fields */
    private final Consumer<Exception> nullableSupplierExceptionConsumer;
    private final Consumer<Throwable> nullableUnexpectedThrowableConsumer;
    private final Runnable nullableActionOnAttemptsExhausted;
    private final Runnable nullableActionOnStopped;
    
    /* mutable state */
    private T t;
    private Exception lastThrownSupplierException;
    private boolean isStopped;
    private boolean isCompleted;
    private boolean isFailed;
    private boolean getMonitorReleased;
    private AsyncProviderState state;

    AwaitAndProviderImpl(
            Supplier<T> tSupplier,    
            RepeatedWaiting waiting, 
            Consumer<Exception> nullableSupplierExceptionConsumer, 
            Consumer<Throwable> nullableUnexpectedThrowableConsumer, 
            Runnable nullableActionOnAttemptsExhausted,
            Runnable nullableActionOnStopped) {
        this.tUnsafeSupplier = tSupplier;
        this.waiting = waiting;
        this.supplyMonitor = new Object();
        this.getMonitor = new Object();
        
        this.isStopped = false;
        this.isCompleted = false;
        this.isFailed = false;
        this.getMonitorReleased = false;
        
        this.nullableSupplierExceptionConsumer = nullableSupplierExceptionConsumer;
        this.nullableUnexpectedThrowableConsumer = nullableUnexpectedThrowableConsumer;
        this.nullableActionOnAttemptsExhausted = nullableActionOnAttemptsExhausted;
        this.nullableActionOnStopped = nullableActionOnStopped;
    }
    
    void asyncStart() {
        this.state = PROVIDING_RUNNING;
        runAsync(this::doAsyncWaitingWork);
    }
    
    private void doAsyncWaitingWork() {
        try {
            synchronized ( this.supplyMonitor ) {
                supplyingLoop: 
                while ( ! this.isStopped && ! this.isCompleted && ! this.isFailed ) {            
                    try {
                        try { 
                            this.t = this.tUnsafeSupplier.get();
                            this.isCompleted = true;
                            this.state = PROVIDING_COMPLETED;
                            break supplyingLoop;
                        }
                        catch (Exception e) {
                            this.tryConsumeSupplier(e);
                            if ( this.waiting.doesHaveAttempts() ) {
                                this.supplyMonitor.wait(this.waiting.millis());
                            }
                            else {
                                this.tryDoWhenAttemptsExhausted();
                                this.lastThrownSupplierException = e;
                                this.isFailed = true;
                                this.state = PROVIDING_FAILED;
                                break supplyingLoop;
                            }
                        }
                    }
                    catch (InterruptedException e) {
                        this.tryConsumeUnexpected(e);
                    }                    
                }
            }
        }
        finally {
            synchronized ( this.getMonitor ) {
                this.getMonitorReleased = true;
                this.getMonitor.notifyAll();
            } 
        }    
    }
    
    @Override
    public Awaited<T> awaitAndGet() {
        synchronized ( this.getMonitor ) {
            if ( ! this.getMonitorReleased ) {
                try {
                    this.getMonitor.wait();
                }
                catch (InterruptedException e) {
                    this.tryConsumeUnexpected(e);
                }
            }              
        }
        synchronized ( this.supplyMonitor ) {
            if ( nonNull(this.lastThrownSupplierException) ) {
                throw new RuntimeException(this.lastThrownSupplierException);
            }
            return new Awaited<>(this.t, this.state.awaitedResult());
        }
    }
    
    @Override
    public Awaited<T> stop() {
        synchronized ( this.supplyMonitor ) {
            this.isStopped = ! this.isCompleted && ! this.isFailed ;
            if ( this.isStopped ) {
                this.state = PROVIDING_STOPPED;
                this.tryDoWhenStopped();
            }
            this.supplyMonitor.notifyAll();
        }
        synchronized ( this.getMonitor ) {
            this.getMonitorReleased = true;
            this.getMonitor.notifyAll();
        }  
        synchronized ( this.supplyMonitor ) {
            return new Awaited<>(this.t, this.state.awaitedResult());
        }
    }
    
    @Override
    public AsyncProviderState state() {
        return this.state;
    }

    private void tryDoWhenAttemptsExhausted() {
        if ( nonNull(this.nullableActionOnAttemptsExhausted) ) {
            try {
                this.nullableActionOnAttemptsExhausted.run();
            }
            catch (Throwable e) {
                this.tryConsumeUnexpected(e);
            }
        }
    }
    
    private void tryDoWhenStopped() {
        if ( nonNull(this.nullableActionOnStopped) ) {
            try {
                this.nullableActionOnStopped.run();
            }
            catch (Throwable e) {
                this.tryConsumeUnexpected(e);
            }
        }
    }

    private void tryConsumeSupplier(Exception e) {
        if ( nonNull(this.nullableSupplierExceptionConsumer) ) {
            try {
                this.nullableSupplierExceptionConsumer.accept(e);
            }
            catch (Throwable e1) {
                this.tryConsumeUnexpected(e1);
            }
        }
    }

    private void tryConsumeUnexpected(Throwable e) {
        if ( nonNull(this.nullableUnexpectedThrowableConsumer) ) {
            try {
                this.nullableUnexpectedThrowableConsumer.accept(e);
            }
            catch (Throwable e1) {
                // ignore
            }
        }
    }
    
    static class TestUnsafeSupplier<T> implements Supplier<T> {
        
        private final T t;
        private int fails;

        public TestUnsafeSupplier(T t, int fails) {
            this.t = t;
            this.fails = fails;
        }

        @Override
        public T get() {
            sleepSafely(200);
            if ( this.fails == 0 ) {
                return this.t;
            }
            else {
                this.fails--;
                throw new RuntimeException("fail!");
            }
        }
        
    }
    
    public static void main(String[] args) throws Exception {
        TestUnsafeSupplier<String> unsafeSupplier = new TestUnsafeSupplier<>("value", 2);
        AwaitAndGet<String> value = AwaitAndGet.from(unsafeSupplier)
                .withMillisToWaitAfterFail(500)
                .withMaxAttempts(3)
                .withCallbackOnSupplierFail((e) -> System.out.println("fail"))
                .withCallbackOnUnexpectedFail((t) -> t.printStackTrace())
                .withCallbackOnStopped(() -> System.out.println("stopped"))
                .begin();

        CountDownLatch barier = new CountDownLatch(2);

        runAsync(() -> {
            try {
                Awaited<String> awaitedString = value.awaitAndGet();
                System.out.println("getting thread: " + awaitedString);
                barier.countDown();
            }
            catch (Exception e) {
                e.printStackTrace();
            }            
        });
        
        runAsync(() -> {
            sleepSafely(6020);
            Awaited<String> awaitedString = value.stop();
            System.out.println("stopping thread: " + awaitedString);
            barier.countDown();
        });

        barier.await();
        System.out.println("stop");
    }
}
