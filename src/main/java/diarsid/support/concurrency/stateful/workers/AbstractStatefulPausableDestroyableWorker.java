package diarsid.support.concurrency.stateful.workers;

import java.util.function.BiConsumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import diarsid.support.objects.workers.PausableDestroyableWorker;
import diarsid.support.objects.workers.WorkerStateChange;

import static java.lang.String.format;

import static diarsid.support.objects.workers.WorkerState.DESTROYED;
import static diarsid.support.objects.workers.WorkerState.PAUSED;
import static diarsid.support.objects.workers.WorkerState.WORKING;
import static diarsid.support.objects.workers.WorkerStateChange.CHANGE_DONE;
import static diarsid.support.objects.workers.WorkerStateChange.CHANGE_FAILED;
import static diarsid.support.objects.workers.WorkerStateChange.CHANGE_NOT_NEEDED;
import static diarsid.support.objects.workers.WorkerStateTransition.TO_DESTROYED;
import static diarsid.support.objects.workers.WorkerStateTransition.TO_PAUSED;

public abstract class AbstractStatefulPausableDestroyableWorker
        extends AbstractStatefulWorker
        implements PausableDestroyableWorker {

    private final Logger log = LoggerFactory.getLogger(this.getClass().getSimpleName());

    public AbstractStatefulPausableDestroyableWorker(String name) {
        super(name);
    }

    public AbstractStatefulPausableDestroyableWorker(String name, BiConsumer<String, Throwable> unexpectedExceptionsConsumer) {
        super(name, unexpectedExceptionsConsumer);
    }

    protected abstract boolean doSynchronizedPauseWork();

    protected abstract boolean doSynchronizedDestroy();

    protected final boolean isPausedOrTransitingToPaused() {
        return super.stateTransition().isIn(TO_PAUSED) || super.state().current().equals(PAUSED);
    }

    protected final boolean isDestroyedOrTransitingToDestroyed() {
        return super.stateTransition().isIn(TO_DESTROYED) || super.state().current().equals(DESTROYED);
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
    public final WorkerStateChange destroy() {
        return super.doSynchronizedReturnChange(this::destroySynchronously);
    }

    private WorkerStateChange destroySynchronously() {
        if (super.state().current().equals(DESTROYED)) {
            return CHANGE_NOT_NEEDED;
        }

        super.state().throwIfCannotChangeTo(DESTROYED);

        try {
            if (super.state().current().equals(WORKING)) {
                this.pauseWork();
            }
        } catch (Throwable t) {
            log.error(format("Failed to pause %s before destroy! Destroy will proceed normally", super.name()), t);
        }

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

    @Override
    public final boolean isPaused() {
        return super.state().current().equals(PAUSED);
    }
}
