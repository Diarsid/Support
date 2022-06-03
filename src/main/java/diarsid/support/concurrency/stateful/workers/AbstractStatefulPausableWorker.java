package diarsid.support.concurrency.stateful.workers;

import java.util.function.BiConsumer;

import diarsid.support.objects.workers.PausableWorker;
import diarsid.support.objects.workers.WorkerStateChange;

import static diarsid.support.objects.workers.WorkerState.PAUSED;
import static diarsid.support.objects.workers.WorkerStateChange.CHANGE_DONE;
import static diarsid.support.objects.workers.WorkerStateChange.CHANGE_FAILED;
import static diarsid.support.objects.workers.WorkerStateChange.CHANGE_NOT_NEEDED;
import static diarsid.support.objects.workers.WorkerStateTransition.TO_PAUSED;

public abstract class AbstractStatefulPausableWorker
        extends AbstractStatefulWorker
        implements PausableWorker {

    public AbstractStatefulPausableWorker() {
        super();
    }

    public AbstractStatefulPausableWorker(BiConsumer<String, Throwable> unexpectedExceptionsConsumer) {
        super(unexpectedExceptionsConsumer);
    }

    public AbstractStatefulPausableWorker(String name) {
        super(name);
    }

    public AbstractStatefulPausableWorker(String name, BiConsumer<String, Throwable> unexpectedExceptionsConsumer) {
        super(name, unexpectedExceptionsConsumer);
    }

    protected abstract boolean doSynchronizedPauseWork();

    protected final boolean isPausedOrTransitingToPaused() {
        return super.isInStateOrTransitingToState(PAUSED, TO_PAUSED);
    }

    @Override
    public final WorkerStateChange pauseWork() {
        return super.doSynchronizedReturnChange(this::pauseWorkSynchronously);
    }

    private WorkerStateChange pauseWorkSynchronously() {
        if (super.state().current().equals(PAUSED)) {
            return CHANGE_NOT_NEEDED;
        }

        super.state().throwIfCannotChangeTo(PAUSED);

        super.stateTransition().beginTransitionTo(TO_PAUSED);
        try {
            if (doSynchronizedPauseWork()) {
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
