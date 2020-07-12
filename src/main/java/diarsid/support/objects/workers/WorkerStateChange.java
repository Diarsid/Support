package diarsid.support.objects.workers;

public enum WorkerStateChange {

    CHANGE_DONE(
            /* change attempt =   */ true,
            /* change performed = */ true,
            /* in desired state = */ true),
    CHANGE_FAILED(
            /* change attempt =   */ true,
            /* change performed = */ false,
            /* in desired state = */ false),
    CHANGE_REJECTED(
            /* change attempt =   */ false,
            /* change performed = */ false,
            /* in desired state = */ false),
    CHANGE_NOT_NEEDED(
            /* change attempt =   */ false,
            /* change performed = */ false,
            /* in desired state = */ true);

    boolean isChangeAttemptDone;
    boolean isChangePerformed;
    boolean isInDesiredState;

    WorkerStateChange(boolean isChangeAttemptDone, boolean isChangePerformed, boolean isInDesiredState) {
        this.isChangeAttemptDone = isChangeAttemptDone;
        this.isChangePerformed = isChangePerformed;
        this.isInDesiredState = isInDesiredState;
    }

    public boolean isChangeAttemptDone() {
        return isChangeAttemptDone;
    }

    public boolean isChangePerformed() {
        return this.isChangePerformed;
    }

    public boolean isInDesiredState() {
        return this.isInDesiredState;
    }
}
