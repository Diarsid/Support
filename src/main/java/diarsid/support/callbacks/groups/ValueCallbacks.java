package diarsid.support.callbacks.groups;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import diarsid.support.callbacks.ValueCallback;

import static java.util.UUID.randomUUID;

public class ValueCallbacks<T> implements Callbacks<ValueCallback<T>> {

    private final Map<UUID, ValueCallback<T>> callbacksByUuid;
    private final Lock readLock;
    private final Lock writeLock;
    protected final Object callLock;

    public ValueCallbacks() {
        this.callbacksByUuid = new HashMap<>();
        ReadWriteLock readWriteLock = new ReentrantReadWriteLock(true);
        this.readLock = readWriteLock.readLock();
        this.writeLock = readWriteLock.writeLock();
        this.callLock = new Object();
    }

    @Override
    public final ActiveCallback<ValueCallback<T>> add(ValueCallback<T> callback) {
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

    public void call(T t) {
        List<ValueCallback<T>> copy = this.createCallbacksListCopySynchronously();

        synchronized ( this.callLock ) {
            for (ValueCallback<T> callback : copy) {
                try {
                    callback.call(t);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    protected final List<ValueCallback<T>> createCallbacksListCopySynchronously() {
        this.readLock.lock();
        List<ValueCallback<T>> copy;

        try {
            copy = new ArrayList<>(this.callbacksByUuid.values());
        }
        finally {
            this.readLock.unlock();
        }
        return copy;
    }

    @Override
    public final ValueCallback<T> remove(UUID uuid) {
        ValueCallback<T> callback;
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
