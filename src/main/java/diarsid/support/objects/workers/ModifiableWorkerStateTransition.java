package diarsid.support.objects.workers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.lang.String.format;
import static java.util.Objects.nonNull;

public class ModifiableWorkerStateTransition {

    private final String className;
    private final String objName;
    private final Logger log;
    private WorkerStateTransition current;

    public ModifiableWorkerStateTransition(String className, String objName) {
        this.className = className;
        this.objName = objName;
        this.log = LoggerFactory.getLogger(this.className);
    }

    public void beginTransitionTo(WorkerStateTransition other) {
        if (nonNull(current)) {
            String message = format(
                    "Worker %s is in state transition %s and it cannot be changed to %s",
                    objName, current.name(), other.name());
            log.error(message);
            throw new IllegalStateException(message);
        }
        current = other;
    }

    public void stopCurrentTransition() {
        current = null;
    }

    public WorkerStateTransition current() {
        return current;
    }

}
