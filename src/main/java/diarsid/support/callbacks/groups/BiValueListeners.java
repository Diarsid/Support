package diarsid.support.callbacks.groups;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import diarsid.support.callbacks.BiValueListener;

import static java.util.Objects.nonNull;
import static java.util.UUID.randomUUID;

public class BiValueListeners<T1, T2> implements Listeners {

    private final Map<UUID, BiValueListener<T1, T2>> callbacksByUuid;
    private final Lock readLock;
    private final Lock writeLock;
    protected final Object callLock;

    public BiValueListeners() {
        this.callbacksByUuid = new HashMap<>();
        ReadWriteLock readWriteLock = new ReentrantReadWriteLock(true);
        this.readLock = readWriteLock.readLock();
        this.writeLock = readWriteLock.writeLock();
        this.callLock = new Object();
    }

    public final void add(BiValueListener<T1, T2> listener) {
        UUID uuid = randomUUID();
        this.writeLock.lock();
        try {
            this.callbacksByUuid.put(uuid, listener);
        }
        finally {
            this.writeLock.unlock();
        }
    }

    public void accept(T1 t1, T2 t2) {
        List<BiValueListener<T1, T2>> copy = this.createCallbacksListCopySynchronously();

        synchronized ( this.callLock ) {
            for (BiValueListener<T1, T2> callback : copy) {
                try {
                    callback.accept(t1, t2);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private List<BiValueListener<T1, T2>> createCallbacksListCopySynchronously() {
        this.readLock.lock();
        List<BiValueListener<T1, T2>> copy;

        try {
            copy = new ArrayList<>(this.callbacksByUuid.values());
        }
        finally {
            this.readLock.unlock();
        }
        return copy;
    }

    @Override
    public final boolean remove(UUID uuid) {
        BiValueListener<T1, T2> listener;
        this.writeLock.lock();
        try {
            listener = this.callbacksByUuid.remove(uuid);
        }
        finally {
            this.writeLock.unlock();
        }
        return nonNull(listener);
    }
}
