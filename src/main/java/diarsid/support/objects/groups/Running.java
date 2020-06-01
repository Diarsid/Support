package diarsid.support.objects.groups;

import java.util.UUID;

public class Running {

    private final UUID uuid;
    private final Runnables runables;

    Running(UUID uuid, Runnables runables) {
        this.uuid = uuid;
        this.runables = runables;
    }

    public Runnable cancel() {
        return this.runables.remove(this.uuid);
    }
}
