package diarsid.support.objects.workers;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import static diarsid.support.objects.workers.WorkerState.WORKING;
import static diarsid.support.objects.workers.WorkerStateChange.CHANGE_DONE;
import static diarsid.support.objects.workers.WorkerStateChange.CHANGE_FAILED;
import static diarsid.support.objects.workers.WorkerStateChange.CHANGE_NOT_NEEDED;
import static diarsid.support.objects.workers.WorkerStateTransition.TO_WORKING;

public abstract class AbstractWorker implements Worker {

    private final String name;
    private final ModifiableWorkerState state;
    private final ModifiableWorkerStateTransition stateTransition;

    public AbstractWorker(String name) {
        String className = this.getClass().getSimpleName();
        this.name = name;
        this.state = new ModifiableWorkerState(className, name);
        this.stateTransition = new ModifiableWorkerStateTransition(className, name);
    }

    public final String name() {
        return this.name;
    }

    protected abstract boolean doStartWork();

    protected final ModifiableWorkerState state() {
        return this.state;
    }

    protected final ModifiableWorkerStateTransition stateTransition() {
        return this.stateTransition;
    }

    protected final boolean isInStateOrTransitingToState(WorkerState someState, WorkerStateTransition someTransition) {
        WorkerStateTransition transition = this.stateTransition.current();

        if ( this.state.current().equals(someState) ) {
            return isNull(transition) || transition.equals(someTransition);
        }
        else {
            return nonNull(transition) && transition.equals(someTransition);
        }
    }

    protected final boolean isWorkingOrTransitingToWorking() {
        return this.isInStateOrTransitingToState(WORKING, TO_WORKING);
    }

    @Override
    public final WorkerStateChange startWork() {
        if (this.state.current().equals(WORKING)) {
            return CHANGE_NOT_NEEDED;
        }

        this.stateTransition.beginTransitionTo(WorkerStateTransition.TO_WORKING);
        try {
            if (doStartWork()) {
                this.state.changeTo(WORKING);
                return CHANGE_DONE;
            } else {
                return CHANGE_FAILED;
            }
        } finally {
            this.stateTransition.stopCurrentTransition();
        }
    }

    @Override
    public final boolean isWorking() {
        return this.state.current().equals(WORKING);
    }

}
