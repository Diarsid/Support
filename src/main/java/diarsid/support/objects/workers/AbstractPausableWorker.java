package diarsid.support.objects.workers;

import static diarsid.support.objects.workers.WorkerState.PAUSED;
import static diarsid.support.objects.workers.WorkerStateChange.CHANGE_DONE;
import static diarsid.support.objects.workers.WorkerStateChange.CHANGE_FAILED;
import static diarsid.support.objects.workers.WorkerStateChange.CHANGE_NOT_NEEDED;
import static diarsid.support.objects.workers.WorkerStateTransition.TO_PAUSED;

public abstract class AbstractPausableWorker
        extends AbstractWorker
        implements PausableWorker {

    public AbstractPausableWorker(String name) {
        super(name);
    }

    protected abstract boolean doPauseWork();

    protected final boolean isPausedOrTransitingToPaused() {
        return super.isInStateOrTransitingToState(PAUSED, TO_PAUSED);
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
    public final boolean isPaused() {
        return super.state().current().equals(PAUSED);
    }
}
