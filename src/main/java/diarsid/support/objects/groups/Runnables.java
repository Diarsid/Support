package diarsid.support.objects.groups;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static java.util.UUID.randomUUID;

public class Runnables {

    private final Map<UUID, Runnable> runnablesByUuid;

    public Runnables() {
        this.runnablesByUuid = new HashMap<>();
    }

    public Running add(Runnable Runnable) {
        UUID uuid = randomUUID();
        this.runnablesByUuid.put(uuid, Runnable);
        return new Running(uuid, this);
    }

    public void run() {
        for (Runnable runnable : this.runnablesByUuid.values()) {
            try {
                runnable.run();
            }
            catch (Exception e) {

            }
        }
    }

    Runnable remove(UUID uuid) {
        Runnable runnable;
        runnable = this.runnablesByUuid.remove(uuid);
        return runnable;
    }
}
