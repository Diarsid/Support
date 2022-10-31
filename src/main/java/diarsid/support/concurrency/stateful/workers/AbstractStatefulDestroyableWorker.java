package diarsid.support.concurrency.stateful.workers;

import java.util.function.BiConsumer;

import diarsid.support.objects.workers.Worker;
import diarsid.support.objects.workers.WorkerStateChange;

import static diarsid.support.objects.workers.WorkerState.DESTROYED;
import static diarsid.support.objects.workers.WorkerStateChange.CHANGE_DONE;
import static diarsid.support.objects.workers.WorkerStateChange.CHANGE_FAILED;
import static diarsid.support.objects.workers.WorkerStateChange.CHANGE_NOT_NEEDED;
import static diarsid.support.objects.workers.WorkerStateTransition.TO_DESTROYED;

public abstract class AbstractStatefulDestroyableWorker
        extends AbstractStatefulWorker
        implements Worker.Destroyable {

    public AbstractStatefulDestroyableWorker() {
        super();
    }

    public AbstractStatefulDestroyableWorker(BiConsumer<String, Throwable> unexpectedExceptionsConsumer) {
        super(unexpectedExceptionsConsumer);
    }

    public AbstractStatefulDestroyableWorker(String name) {
        super(name);
    }

    public AbstractStatefulDestroyableWorker(String name, BiConsumer<String, Throwable> unexpectedExceptionsConsumer) {
        super(name, unexpectedExceptionsConsumer);
    }

    protected abstract boolean doSynchronizedDestroy();

    protected final boolean isDestroyedOrTransitingToDestroyed() {
        return super.isInStateOrTransitingToState(DESTROYED, TO_DESTROYED);
    }

    @Override
    public final WorkerStateChange destroy() {
        return super.doSynchronizedReturnChange(this::destroySynchronously);
    }

    private WorkerStateChange destroySynchronously() {
        if (super.state().current().equals(DESTROYED)) {
            return CHANGE_NOT_NEEDED;
        }

        super.state().throwIfCannotChangeTo(DESTROYED);

        super.stateTransition().beginTransitionTo(TO_DESTROYED);
        try {
            if (doSynchronizedDestroy()) {
                super.state().changeTo(DESTROYED);
                return CHANGE_DONE;
            } else {
                return CHANGE_FAILED;
            }
        } finally {
            super.stateTransition().stopCurrentTransition();
        }
    }

    @Override
    public final boolean isDestroyed() {
        return super.state().current().equals(DESTROYED);
    }
}
