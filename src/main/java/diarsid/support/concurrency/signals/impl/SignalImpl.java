package diarsid.support.concurrency.signals.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import diarsid.support.concurrency.signals.Signal;
import diarsid.support.concurrency.signals.Signals;
import diarsid.support.model.Named;
import diarsid.support.objects.references.Possible;
import diarsid.support.objects.references.References;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import static diarsid.support.concurrency.threads.ThreadsUtil.shutdownAndWait;

class SignalImpl implements Named, Signal {

    private static final Logger log = LoggerFactory.getLogger(Signal.class);

    private static class ListenerHolder implements Named {

        private static final Logger log = LoggerFactory.getLogger(ListenerHolder.class);

        private final SignalImpl signal;
        private final Listener listener;
        private final Listener.OfData dataListener;
        private final boolean isDataListener;

        final Thread reactionThread;
        final AtomicBoolean isListening;
        final Lock lock;
        final Condition condition;
        final Possible<Object> emittedData;
        boolean conditionSignalled;

        public ListenerHolder(SignalImpl signal, Listener listener) {
            this.signal = signal;
            this.listener = listener;
            this.isDataListener = listener instanceof Listener.OfData;
            if ( this.isDataListener ) {
                this.dataListener = (Listener.OfData) this.listener;
            }
            else {
                this.dataListener = null;
            }

            this.isListening = new AtomicBoolean(true);
            this.reactionThread = new Thread(this::awaitInLoopAndRunListener);

            this.lock = new ReentrantLock();
            this.condition = this.lock.newCondition();

            this.emittedData = References.simplePossibleButEmpty();

            this.reactionThread.setName(Signals.class.getSimpleName() + "." + Signal.class.getSimpleName() + "[" + this.signal.name + "].listener[" + this.listener.name() + "].awaiter");
            this.reactionThread.start();

            this.lock.lock();
            try {
                this.condition.await();
            }
            catch (InterruptedException e) {
                // TODO
            }
            finally {
                this.lock.unlock();
            }
        }

        private void awaitInLoopAndRunListener() {
            this.lock.lock();
            try {
                this.condition.signal();
            }
            finally {
                this.lock.unlock();
            }

            listeningLoop: while ( this.isListening.get() ) {
                this.lock.lock();
                try {
                    do {
                        this.condition.await();
                        if ( ! this.isListening.get() ) {
                            break listeningLoop;
                        }
                    }
                    while ( ! this.conditionSignalled );
                    this.conditionSignalled = false;

                    if ( ! this.isListening.get() ) {
                        break listeningLoop;
                    }

                    Object data = this.emittedData.extractOrNull();
                    this.signal.async.execute(() -> this.invokeSafely(data));
                }
                catch (InterruptedException e) {
                    // ignore
                }
                finally {
                    this.lock.unlock();
                }
            }
        }

        private void invokeSafely(Object data) {
            try {
                if ( this.isDataListener && nonNull(data) ) {
                    this.dataListener.onEmittedWith(data);
                }
                else {
                    this.listener.onEmitted();
                }
            }
            catch (Throwable t) {
                // TODO
            }
        }

        @Override
        public String name() {
            return this.listener.name();
        }
    }

    private final String name;
    private final Runnable onClose;
    private final AtomicBoolean isWorking;
    private final Map<String, ListenerHolder> listenersByNames;
    private final Lock emitLock;
    private final Condition emitCondition;
    private final Lock listenersLock;
    private final Thread emitListenerThread;
    private final List<ListenerHolder> listenersToSignal;
    private final ExecutorService async;
    private final Possible<Object> emittedData;

    private boolean emitConditionSignaled;

    SignalImpl(String name, ExecutorService async, Runnable onClose) {
        this.name = name;
        this.onClose = onClose;
        this.isWorking = new AtomicBoolean(true);
        this.listenersByNames = new HashMap<>();
        this.emitLock = new ReentrantLock();
        this.emitCondition = this.emitLock.newCondition();
        this.listenersLock = new ReentrantLock();
        this.emitListenerThread = new Thread(this::listenForEmit);
        this.emitListenerThread.setName(Signals.class.getSimpleName() + ".Event[" + this.name + "].waiter");
        this.listenersToSignal = new ArrayList<>();
        this.emitConditionSignaled = false;
        this.async = async;
        this.emittedData = References.simplePossibleButEmpty();

        this.emitListenerThread.start();
        log.info("'{}' created!", this.name);
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public void cancel() {
        this.isWorking.set(false);
        this.onClose.run();

        this.emitLock.lock();
        try {
            this.emitConditionSignaled = true;
            this.emitCondition.signal();
        }
        finally {
            this.emitLock.unlock();
        }

        try {
            this.emitListenerThread.join();
        }
        catch (InterruptedException e) {
            // TODO
        }

        this.listenersLock.lock();
        try {
            for ( ListenerHolder listenerHolder : this.listenersByNames.values() ) {
                listenerHolder.isListening.set(false);
                listenerHolder.lock.lock();
                try {
                    listenerHolder.conditionSignalled = true;
                    listenerHolder.condition.signal();
                }
                finally {
                    listenerHolder.lock.unlock();
                }
            }
        }
        finally {
            this.listenersLock.unlock();
        }

        shutdownAndWait(this.async);
    }

    private void listenForEmit() {
        waitForEmitLoop: while ( this.isWorking.get() ) {
            this.emitLock.lock();
            try {
                do {
                    this.emitCondition.await();
                    System.out.println("Listen for fire thread dies " + this.name + " awakened");
                    if ( ! this.isWorking.get() ) {
                        System.out.println("Listen for fire thread dies " + this.name + " break[1]");
                        break waitForEmitLoop;
                    }
                }
                while ( ! this.emitConditionSignaled );
                this.emitConditionSignaled = false;

                if ( ! this.isWorking.get() ) {
                    System.out.println("Listen for fire thread dies " + this.name + " break[2]");
                    break waitForEmitLoop;
                }

                this.emitted();
            }
            catch (InterruptedException e) {
                // ignore
            }
            finally {
                this.emitLock.unlock();
            }
        }
        System.out.println("Listen for fire thread dies " + this.name);
    }

    private void emitted() {
        this.listenersLock.lock();
        try {
            this.listenersToSignal.addAll(this.listenersByNames.values());
        }
        finally {
            this.listenersLock.unlock();
        }

        for ( ListenerHolder listenerHolder : this.listenersToSignal ) {
            listenerHolder.lock.lock();
            try {
                listenerHolder.conditionSignalled = true;
                listenerHolder.emittedData.resetTo(this.emittedData);
                listenerHolder.condition.signal();
            }
            finally {
                listenerHolder.lock.unlock();
            }
        }

        this.listenersToSignal.clear();
    }

    @Override
    public String listen(Listener listener) {
        if ( ! this.isWorking.get() ) {
            throw new IllegalStateException("Is not working!");
        }

        String name = listener.name();

        this.listenersLock.lock();
        if ( ! this.isWorking.get() ) {
            throw new IllegalStateException("Is not working!");
        }
        try {
            ListenerHolder listenerHolder = this.listenersByNames.get(name);

            if ( nonNull(listenerHolder) ) {
                throw new IllegalArgumentException("This listener is already registered!");
            }

            this.listenersByNames.put(name, new ListenerHolder(this, listener));
            log.info("{} '{}' is registered for {} '{}'",
                    Listener.class.getSimpleName(), name, Signal.class.getSimpleName(), this.name);

            return name;
        }
        finally {
            this.listenersLock.unlock();
        }
    }

    @Override
    public boolean dontListen(String name) {
        this.listenersLock.lock();
        ListenerHolder listenerHolder;
        try {
            listenerHolder = this.listenersByNames.remove(name);
        }
        finally {
            this.listenersLock.unlock();
        }


        if ( isNull(listenerHolder) ) {
            return false;
        }

        listenerHolder.isListening.set(false);
        listenerHolder.lock.lock();
        listenerHolder.conditionSignalled = true;
        try {
            listenerHolder.condition.signal();
        }
        finally {
            listenerHolder.lock.unlock();
        }

        try {
            listenerHolder.reactionThread.join();

        }
        catch (InterruptedException e) {
            // TODO
        }

        return true;
    }

    @Override
    public void emitWith(Object data) {
        if ( !this.isWorking.get() ) {
            return;
        }

        this.emitLock.lock();
        if ( !this.isWorking.get() ) {
            return;
        }
        try {
            System.out.println("firing... " + this.name);
            this.emitConditionSignaled = true;
            this.emittedData.resetTo(data);
            this.emitCondition.signal();
            System.out.println("fired " + this.name);
        }
        finally {
            this.emitLock.unlock();
        }
    }

    @Override
    public void emit() {
        this.emitWith(null);
    }
}
