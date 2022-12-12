package diarsid.support.concurrency.signals.impl;

import java.util.HashMap;
import java.util.Map;

import diarsid.support.concurrency.signals.Signal;
import diarsid.support.concurrency.signals.Signals;
import diarsid.support.concurrency.stateful.workers.AbstractStatefulDestroyableWorker;
import diarsid.support.concurrency.threads.NamedThreadSource;

import static java.util.Objects.nonNull;

public class SignalsRuntimeImpl extends AbstractStatefulDestroyableWorker implements Signals.Runtime {

    private final Map<String, SignalImpl> signalsByNames;
    private final NamedThreadSource namedThreadSource;

    public SignalsRuntimeImpl() {
        this.signalsByNames = new HashMap<>();
        this.namedThreadSource = new NamedThreadSource(Signals.class.getSimpleName());
        this.startWork();
    }

    @Override
    public Signal signal(String name) {
        return super.doSynchronizedReturnChange(() -> {
            return this.signalsByNames.computeIfAbsent(name, this::newNamedSignal);
        });
    }

    boolean remove(String name) {
        return super.doSynchronizedReturnChange(() -> {
            SignalImpl signal = this.signalsByNames.get(name);
            return nonNull(signal);
        });
    }

    private SignalImpl newNamedSignal(String name) {
        SignalImpl signal = new SignalImpl(
                name,
                this.namedThreadSource.newNamedCachedThreadPool("Event[" +name + "].listener"),
                () -> this.remove(name));
        return signal;
    }

    @Override
    protected boolean doSynchronizedStartWork() {
        return true;
    }

    @Override
    protected boolean doSynchronizedDestroy() {
        for ( SignalImpl signal : this.signalsByNames.values() ) {
            signal.cancel();
        }
        this.namedThreadSource.close();
        return true;
    }

    @Override
    public void close() {
        super.destroy();
    }
}
