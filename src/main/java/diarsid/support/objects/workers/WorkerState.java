package diarsid.support.objects.workers;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static java.util.Arrays.stream;
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toSet;

public enum WorkerState {

    CREATED,
    WORKING,
    PAUSED,
    DESTROYED;

    private static final Map<WorkerState, Set<WorkerState>> ALLOWED_STATE_TRANSITIONS;

    static {
        ALLOWED_STATE_TRANSITIONS = new HashMap<>();

        ALLOWED_STATE_TRANSITIONS.put(CREATED, allowTransitionsTo(WORKING, DESTROYED));
        ALLOWED_STATE_TRANSITIONS.put(WORKING, allowTransitionsTo(PAUSED, DESTROYED));
        ALLOWED_STATE_TRANSITIONS.put(PAUSED, allowTransitionsTo(WORKING, DESTROYED));
    }

    WorkerState() {
    }

    public boolean canBeChangedTo(WorkerState otherState) {
        Set<WorkerState> allowedTransitionsForThisState = ALLOWED_STATE_TRANSITIONS.get(this);
        if (isNull(allowedTransitionsForThisState)) {
            return false;
        } else {
            return allowedTransitionsForThisState.contains(otherState);
        }
    }

    public boolean canNotBeChangedTo(WorkerState other) {
        return ! this.canBeChangedTo(other);
    }

    public boolean doesAllowWork() {
        return this.equals(WORKING);
    }

    public boolean doesNotAllowWork() {
        return ! this.doesAllowWork();
    }

    public boolean notEquals(WorkerState other) {
        return ! this.equals(other);
    }

    private static Set<WorkerState> allowTransitionsTo(WorkerState... states) {
        return stream(states).collect(toSet());
    }
}
