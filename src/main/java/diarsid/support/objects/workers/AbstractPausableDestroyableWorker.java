package diarsid.support.objects.workers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.lang.String.format;

import static diarsid.support.objects.workers.WorkerState.DESTROYED;
import static diarsid.support.objects.workers.WorkerState.PAUSED;
import static diarsid.support.objects.workers.WorkerState.WORKING;
import static diarsid.support.objects.workers.WorkerStateChange.CHANGE_DONE;
import static diarsid.support.objects.workers.WorkerStateChange.CHANGE_FAILED;
import static diarsid.support.objects.workers.WorkerStateChange.CHANGE_NOT_NEEDED;

public abstract class AbstractPausableDestroyableWorker
        extends AbstractWorker
        implements PausableDestroyableWorker {

    private final Logger log = LoggerFactory.getLogger(this.getClass().getSimpleName());

    public AbstractPausableDestroyableWorker(String name) {
        super(name);
    }

    protected abstract boolean doPauseWork();

    protected abstract boolean doDestroy();

    protected final boolean isPausedOrTransitingToPaused() {
        return super.stateTransition().isIn(WorkerStateTransition.TO_PAUSED) || super.state().current().equals(PAUSED);
    }

    protected final boolean isDestroyedOrTransitingToDestroyed() {
        return super.stateTransition().isIn(WorkerStateTransition.TO_DESTROYED) || super.state().current().equals(DESTROYED);
    }

    @Override
    public final WorkerStateChange pauseWork() {
        if (super.state().current().equals(PAUSED)) {
            return CHANGE_NOT_NEEDED;
        }

        super.stateTransition().beginTransitionTo(WorkerStateTransition.TO_PAUSED);
        try {
            if (doPauseWork()) {
                super.state().changeTo(PAUSED);
                return CHANGE_DONE;
            } else {
                return CHANGE_FAILED;
            }
        } finally {
            super.stateTransition().stopCurrentTransition();
        }
    }

    @Override
    public final WorkerStateChange destroy() {
        if (super.state().current().equals(DESTROYED)) {
            return CHANGE_NOT_NEEDED;
        }

        try {
            if (super.state().current().equals(WORKING)) {
                this.pauseWork();
            }
        } catch (Throwable t) {
            log.error(format("Failed to pause %s before destroy! Destroy will proceed normally", super.name()), t);
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

    @Override
    public final boolean isPaused() {
        return super.state().current().equals(PAUSED);
    }
}
