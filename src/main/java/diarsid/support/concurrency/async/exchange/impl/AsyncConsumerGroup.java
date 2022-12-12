package diarsid.support.concurrency.async.exchange.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import diarsid.support.concurrency.async.exchange.api.AsyncExchangePoint;
import diarsid.support.concurrency.stateful.workers.AbstractStatefulDestroyableWorker;

import static java.lang.String.format;
import static java.util.Objects.isNull;

import static diarsid.support.concurrency.async.exchange.api.AsyncExchangePoint.AsyncConsumer.ConcurrencyMode.PARALLEL;
import static diarsid.support.concurrency.async.exchange.api.AsyncExchangePoint.AsyncConsumer.ConcurrencyMode.SEQUENTIAL;
import static diarsid.support.concurrency.threads.ThreadsUtil.shutdownAndWait;

class AsyncConsumerGroup<T>
        extends AbstractStatefulDestroyableWorker
        implements AsyncExchangePoint.AsyncConsumer.Group<T> {

    static class AsyncConsumerHolder<T> {

        private final AsyncExchangePointImpl<T> exchangePoint;
        private final AsyncConsumerGroup<T> consumerGroup;
        private final AsyncExchangePoint.AsyncConsumer<T> consumer;
        private final ExecutorService asyncListenForInput;
        private final ExecutorService asyncConsumeInput;
        private final Future<?> asyncAwaitForInputLoop;

        final AtomicBoolean isWorking;

        final Lock input;
        final Condition inputAppear;
        volatile T inputData;
        boolean inputAppearSignaled;

        public AsyncConsumerHolder(
                AsyncExchangePointImpl<T> exchangePoint,
                AsyncConsumerGroup<T> consumerGroup,
                AsyncExchangePoint.AsyncConsumer<T> consumer) {
            this.exchangePoint = exchangePoint;
            this.consumerGroup = consumerGroup;
            this.consumer = consumer;
            this.isWorking = new AtomicBoolean(true);
            this.input = new ReentrantLock();
            this.inputAppear = this.input.newCondition();
            this.asyncListenForInput = this.exchangePoint.namedThreadSource.newNamedFixedThreadPool(format("%s[%s].Listener", AsyncExchangePoint.AsyncConsumer.class.getSimpleName(), consumer.name()), 1);

            String asyncConsumeName = format("%s[%s]", AsyncExchangePoint.AsyncConsumer.class.getSimpleName(), consumer.name());
            AsyncExchangePoint.AsyncConsumer.ConcurrencyMode concurrencyMode = this.consumer.concurrencyMode();
            if ( concurrencyMode.is(SEQUENTIAL) ) {
                this.asyncConsumeInput = this.exchangePoint.namedThreadSource.newNamedFixedThreadPool(asyncConsumeName, 1);
            }
            else if ( concurrencyMode.is(PARALLEL) ) {
                this.asyncConsumeInput = this.exchangePoint.namedThreadSource.newNamedCachedThreadPool(asyncConsumeName);
            }
            else {
                throw concurrencyMode.unsupported();
            }

            this.asyncAwaitForInputLoop = this.asyncListenForInput.submit(this::awaitForInputLoop);
        }

        private void awaitForInputLoop() {
            awaitForInputLoop: while ( this.isWorking.get() && this.consumerGroup.isOpen.get() ) {
                this.input.lock();
                try {
                    do {
                        this.inputAppear.await();
                    } while ( ! this.inputAppearSignaled );
                    this.inputAppearSignaled = false;

                    if ( ! (this.isWorking.get() && this.consumerGroup.isOpen.get()) ) {
                        break awaitForInputLoop;
                    }
                    T data = this.inputData;
                    this.asyncConsumeInput.submit(() -> this.consumeSafely(data));
                }
                catch (InterruptedException e) {
                    // ignore;
                }
                catch (Exception e) {
                    // TODO
                }
                finally {
                    this.input.unlock();
                }
            }

        }

        private void consumeSafely(T data) {
            try {
                this.consumer.accept(data);
            }
            catch (Throwable t) {
                // TODO
            }
        }

        void stop() {
            this.isWorking.set(false);
            this.asyncAwaitForInputLoop.cancel(true);
            shutdownAndWait(this.asyncListenForInput);
            shutdownAndWait(this.asyncConsumeInput);
        }
    }

    private final AtomicBoolean isOpen;
    private final AsyncExchangePointImpl<T> exchangePoint;
    private final Map<String, AsyncConsumerHolder<T>> consumerHoldersByNames;
    private final List<AsyncConsumerHolder<T>> consumerHoldersToAwake;
    private final ExecutorService async;
    private final Future<?> asyncAwaitOnQueueLoop;

    public AsyncConsumerGroup(AsyncExchangePointImpl<T> exchangePoint) {
        super(exchangePoint.name() + "." + AsyncExchangePoint.AsyncConsumer.Group.class.getSimpleName());
        this.isOpen = new AtomicBoolean(true);
        this.exchangePoint = exchangePoint;
        this.consumerHoldersByNames = new ConcurrentHashMap<>();
        this.consumerHoldersToAwake = new ArrayList<>();
        this.async = exchangePoint.namedThreadSource.newNamedFixedThreadPool(name(), 1);
        this.asyncAwaitOnQueueLoop = this.async.submit(this::awaitOnQueueLoop);
        this.startWork();
    }

    private void awaitOnQueueLoop() {
        while ( this.isOpen.get() ) {
            try {
                T t = this.exchangePoint.queue.take();
                if ( ! this.isOpen.get() ) {
                    break;
                }

                this.doSynchronizedVoidChange(() -> {
                    this.consumerHoldersToAwake.addAll(this.consumerHoldersByNames.values());
                });

                for ( var consumerHolder : this.consumerHoldersToAwake ) {
                    consumerHolder.input.lock();
                    try {
                        consumerHolder.inputData = t;
                        consumerHolder.inputAppearSignaled = true;
                        consumerHolder.inputAppear.signal();
                    }
                    finally {
                        consumerHolder.input.unlock();
                    }
                }
            }
            catch (Throwable t) {
                // TODO
            }
            finally {
                this.consumerHoldersToAwake.clear();
            }
        }
    }

    @Override
    protected boolean doSynchronizedStartWork() {
        return true;
    }

    @Override
    public boolean add(AsyncExchangePoint.AsyncConsumer<T> consumer) {
        return super.doSynchronizedReturnRead(() -> {
            if ( ! this.isOpen.get() ) {
                return false;
            }

            this.consumerHoldersByNames.computeIfAbsent(
                    consumer.name(),
                    (name) -> new AsyncConsumerHolder<>(this.exchangePoint, this, consumer));
            return true;
        });
    }

    @Override
    public boolean remove(String consumerName) {
        AsyncConsumerHolder<T> holder = super.doSynchronizedReturnRead(() -> {
            return this.consumerHoldersByNames.remove(consumerName);
        });

        if ( isNull(holder) ) {
            return false;
        }

        holder.stop();
        return true;
    }

    @Override
    protected boolean doSynchronizedDestroy() {
        this.isOpen.set(false);
        for ( var holder : this.consumerHoldersByNames.values() ) {
            holder.stop();
        }
        this.consumerHoldersByNames.clear();

        this.asyncAwaitOnQueueLoop.cancel(true);
        shutdownAndWait(this.async);

        return true;
    }
}
