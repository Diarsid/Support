package diarsid.support.objects.groups;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static java.util.UUID.randomUUID;

public class Runnables {

    private final Map<UUID, Runnable> runnablesByUuid;
    private final Lock readLock;
    private final Lock writeLock;

    public Runnables() {
        this.runnablesByUuid = new HashMap<>();
        ReadWriteLock readWriteLock = new ReentrantReadWriteLock(true);
        this.readLock = readWriteLock.readLock();
        this.writeLock = readWriteLock.writeLock();
    }

    public Running add(Runnable Runnable) {
        UUID uuid = randomUUID();
        this.writeLock.lock();
        try {
            this.runnablesByUuid.put(uuid, Runnable);
        }
        finally {
            this.writeLock.unlock();
        }
        return new Running(uuid, this);
    }

    public void run() {
        this.readLock.lock();
        List<Runnable> copy;

        try {
            copy = new ArrayList<>(this.runnablesByUuid.values());
        }
        finally {
            this.readLock.unlock();
        }

        for (Runnable runnable : copy) {
            try {
                runnable.run();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    Runnable remove(UUID uuid) {
        Runnable runnable;
        this.writeLock.lock();
        try {
            runnable = this.runnablesByUuid.remove(uuid);
        }
        finally {
            this.writeLock.unlock();
        }
        return runnable;
    }
}
