package diarsid.support.objects.workers;

import static diarsid.support.objects.workers.WorkerState.DESTROYED;
import static diarsid.support.objects.workers.WorkerStateChange.CHANGE_DONE;
import static diarsid.support.objects.workers.WorkerStateChange.CHANGE_FAILED;
import static diarsid.support.objects.workers.WorkerStateChange.CHANGE_NOT_NEEDED;

public abstract class AbstractDestroyableWorker
        extends AbstractWorker
        implements DestroyableWorker {

    public AbstractDestroyableWorker(String name) {
        super(name);
    }

    protected abstract boolean doDestroy();

    protected final boolean isDestroyedOrTransitingToDestroyed() {
        return super.stateTransition().isIn(WorkerStateTransition.TO_DESTROYED) || super.state().current().equals(DESTROYED);
    }

    @Override
    public final WorkerStateChange destroy() {
        if (super.state().current().equals(DESTROYED)) {
            return CHANGE_NOT_NEEDED;
        }

        super.stateTransition().beginTransitionTo(WorkerStateTransition.TO_DESTROYED);
        try {
            if (doDestroy()) {
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
