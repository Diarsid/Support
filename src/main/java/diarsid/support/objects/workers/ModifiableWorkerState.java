package diarsid.support.objects.workers;

import diarsid.support.strings.MultilineMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.lang.String.format;
import static java.util.Objects.isNull;

public class ModifiableWorkerState {

    private final String className;
    private final String objName;
    private final Logger log;
    private WorkerState state;

    public ModifiableWorkerState(String className, String objName) {
        this.className = className;
        this.objName = objName;
        this.log = LoggerFactory.getLogger(this.className);
        this.state = WorkerState.CREATED;
        logStateChange(null, this.state);
    }

    private void logStateChange(WorkerState oldState, WorkerState newState) {
        MultilineMessage message = new MultilineMessage("[WORKER]", "    ");
        message.newLine().add(format("id    : %s.%s.@%s", className, objName, hashCode()));
        message.newLine().add(format("state : %s -> %s",
                isNull(oldState) ? "<init>" : oldState,
                newState));
        log.info(message.compose());
    }

    public void changeTo(WorkerState otherState) {
        throwIfCannotChangeTo(otherState);
        logStateChange(state, otherState);
        state = otherState;
    }

    public void throwIfCannotChangeTo(WorkerState otherState) {
        if (state.canNotBeChangedTo(otherState)) {
            String message = format(
                    "Worker %s is in state %s and cannot be changed to %s",
                    objName, state.name(), otherState.name());
            log.error(message);
            throw new IllegalArgumentException(message);
        }
    }

    public WorkerState current() {
        return state;
    }
}
