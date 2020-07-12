package diarsid.support.callbacks.groups;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import diarsid.support.callbacks.BiValueCallback;

import static java.util.UUID.randomUUID;

public class BiValueCallbacks<T1, T2> implements Callbacks<BiValueCallback<T1, T2>> {

    private final Map<UUID, BiValueCallback<T1, T2>> callbacksByUuid;
    private final Lock readLock;
    private final Lock writeLock;
    protected final Object callLock;

    public BiValueCallbacks() {
        this.callbacksByUuid = new HashMap<>();
        ReadWriteLock readWriteLock = new ReentrantReadWriteLock(true);
        this.readLock = readWriteLock.readLock();
        this.writeLock = readWriteLock.writeLock();
        this.callLock = new Object();
    }

    @Override
    public final ActiveCallback<BiValueCallback<T1, T2>> add(BiValueCallback<T1, T2> callback) {
        UUID uuid = randomUUID();
        this.writeLock.lock();
        try {
            this.callbacksByUuid.put(uuid, callback);
        }
        finally {
            this.writeLock.unlock();
        }
        return new ActiveCallback<>(uuid, this);
    }

    public void call(T1 t1, T2 t2) {
        List<BiValueCallback<T1, T2>> copy = this.createCallbacksListCopySynchronously();

        synchronized ( this.callLock ) {
            for (BiValueCallback<T1, T2> callback : copy) {
                try {
                    callback.call(t1, t2);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private List<BiValueCallback<T1, T2>> createCallbacksListCopySynchronously() {
        this.readLock.lock();
        List<BiValueCallback<T1, T2>> copy;

        try {
            copy = new ArrayList<>(this.callbacksByUuid.values());
        }
        finally {
            this.readLock.unlock();
        }
        return copy;
    }

    @Override
    public final BiValueCallback<T1, T2> remove(UUID uuid) {
        BiValueCallback<T1, T2> callback;
        this.writeLock.lock();
        try {
            callback = this.callbacksByUuid.remove(uuid);
        }
        finally {
            this.writeLock.unlock();
        }
        return callback;
    }
}
