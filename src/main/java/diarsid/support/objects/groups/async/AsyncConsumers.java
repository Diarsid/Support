package diarsid.support.objects.groups.async;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;

import static java.util.UUID.randomUUID;

public class AsyncConsumers<T> {

    private final Executor async;
    private final Map<UUID, Consumer<T>> consumersByUuid;
    private final Lock readLock;
    private final Lock writeLock;

    public AsyncConsumers() {
        this.async = Executors.newCachedThreadPool();
        this.consumersByUuid = new HashMap<>();

        ReadWriteLock readWriteLock = new ReentrantReadWriteLock(true);
        this.readLock = readWriteLock.readLock();
        this.writeLock = readWriteLock.writeLock();
    }

    public AsyncConsumers(Executor async) {
        this.async = async;
        this.consumersByUuid = new HashMap<>();

        ReadWriteLock readWriteLock = new ReentrantReadWriteLock(true);
        this.readLock = readWriteLock.readLock();
        this.writeLock = readWriteLock.writeLock();
    }

    public AsyncConsumers(Executor async, ReadWriteLock readWriteLock) {
        this.async = async;
        this.consumersByUuid = new HashMap<>();

        this.readLock = readWriteLock.readLock();
        this.writeLock = readWriteLock.writeLock();
    }

    public UUID add(Consumer<T> consumer) {
        UUID uuid = randomUUID();

        this.writeLock.lock();
        try {
            this.consumersByUuid.put(uuid, consumer);
        }
        finally {
            this.writeLock.unlock();
        }

        return uuid;
    }

    public Consumer<T> remove(UUID uuid) {
        Consumer<T> consumer;

        this.writeLock.lock();
        try {
            consumer = this.consumersByUuid.remove(uuid);
        }
        finally {
            this.writeLock.unlock();
        }

        return consumer;
    }

    public void asyncAdd(Consumer<T> consumer, Consumer<UUID> callback) {
        this.async.execute(() -> {
            UUID uuid = randomUUID();

            this.writeLock.lock();
            try {
                this.consumersByUuid.put(uuid, consumer);
            }
            finally {
                this.writeLock.unlock();
            }

            callback.accept(uuid);
        });
    }

    public void asyncRemove(UUID uuid, Consumer<Consumer<T>> callback) {
        this.async.execute(() -> {
            Consumer<T> consumer;

            this.writeLock.lock();
            try {
                consumer = this.consumersByUuid.remove(uuid);
            }
            finally {
                this.writeLock.unlock();
            }

            callback.accept(consumer);
        });
    }

    public void accept(T t) {
        this.async.execute(() -> {
            this.readLock.lock();
            try {
                for (Consumer<T> consumer : this.consumersByUuid.values()) {
                    consumer.accept(t);
                }
            }
            finally {
                this.readLock.unlock();
            }
        });
    }
}
