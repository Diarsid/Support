package diarsid.support.objects.workers;

import static diarsid.support.objects.workers.WorkerState.WORKING;
import static diarsid.support.objects.workers.WorkerStateChange.CHANGE_DONE;
import static diarsid.support.objects.workers.WorkerStateChange.CHANGE_FAILED;
import static diarsid.support.objects.workers.WorkerStateChange.CHANGE_NOT_NEEDED;

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

    protected final boolean isWorkingOrTransitingToWorking() {
        return this.stateTransition.isIn(WorkerStateTransition.TO_WORKING) || this.state.current().equals(WORKING);
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
