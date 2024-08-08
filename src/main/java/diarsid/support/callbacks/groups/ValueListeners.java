package diarsid.support.callbacks.groups;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;

import diarsid.support.callbacks.ValueListener;

import static java.util.Objects.nonNull;
import static java.util.UUID.randomUUID;

public class ValueListeners<T> implements Listeners {

    private final Map<UUID, ValueListener<T>> listenersByUuid;
    private final Lock getListeners;
    private final Lock changeListeners;
    private final Lock accept;

    public ValueListeners() {
        this.listenersByUuid = new HashMap<>();
        ReadWriteLock readWriteLock = new ReentrantReadWriteLock(true);
        this.getListeners = readWriteLock.readLock();
        this.changeListeners = readWriteLock.writeLock();
        this.accept = new ReentrantLock(true);
    }

    public final void add(ValueListener<T> listener) {
        UUID uuid = listener.uuid();
        this.changeListeners.lock();
        try {
            this.listenersByUuid.put(uuid, listener);
        }
        finally {
            this.changeListeners.unlock();
        }
    }

    public final ValueListener<T> add(Consumer<T> consumer) {
        UUID uuid = randomUUID();
        var listener = new ValueListenerImpl<>(uuid, consumer);
        this.changeListeners.lock();
        try {
            this.listenersByUuid.put(uuid, listener);
        }
        finally {
            this.changeListeners.unlock();
        }
        return listener;
    }

    public void accept(T t) {
        this.accept.lock();
        try {
            this.getListeners.lock();
            try {
                for ( ValueListener<T> listener : this.listenersByUuid.values() ) {
                    try {
                        listener.accept(t);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            finally {
                this.getListeners.unlock();
            }
        }
        finally {
            this.accept.unlock();
        }
    }

    @Override
    public final boolean remove(UUID uuid) {
        ValueListener<T> listener;
        this.changeListeners.lock();
        try {
            listener = this.listenersByUuid.remove(uuid);
        }
        finally {
            this.changeListeners.unlock();
        }
        return nonNull(listener);
    }
}
