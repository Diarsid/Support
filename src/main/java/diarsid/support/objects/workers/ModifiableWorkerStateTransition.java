package diarsid.support.objects.workers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.lang.String.format;
import static java.util.Objects.nonNull;

public class ModifiableWorkerStateTransition {

    private final String className;
    private final String objName;
    private final Logger log;
    private WorkerStateTransition stateTransition;

    public ModifiableWorkerStateTransition(String className, String objName) {
        this.className = className;
        this.objName = objName;
        this.log = LoggerFactory.getLogger(this.className);
    }

    public void beginTransitionTo(WorkerStateTransition other) {
        if (nonNull(stateTransition)) {
            String message = format(
                    "Worker %s is in state transition %s and it cannot be changed to %s",
                    objName, stateTransition.name(), other.name());
            log.error(message);
            throw new IllegalStateException(message);
        }
        stateTransition = other;
    }

    public void stopCurrentTransition() {
        stateTransition = null;
    }

    public boolean isIn(WorkerStateTransition transition) {
        WorkerStateTransition currentTransition = stateTransition;
        return nonNull(currentTransition) && currentTransition.equals(transition);
    }
}
