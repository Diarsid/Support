package diarsid.support.objects.groups;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;

import static java.util.UUID.randomUUID;

public class Consumers<T> {

    private final Map<UUID, Consumer<T>> consumersByUuid;
    private final Lock readLock;
    private final Lock writeLock;

    public Consumers() {
        this.consumersByUuid = new HashMap<>();
        ReadWriteLock readWriteLock = new ReentrantReadWriteLock(true);
        this.readLock = readWriteLock.readLock();
        this.writeLock = readWriteLock.writeLock();
    }

    public Consuming<T> add(Consumer<T> consumer) {
        UUID uuid = randomUUID();
        this.writeLock.lock();
        try {
            this.consumersByUuid.put(uuid, consumer);
        }
        finally {
            this.writeLock.unlock();
        }
        return new Consuming<>(uuid, this);
    }

    public void accept(T t) {
        this.readLock.lock();
        List<Consumer<T>> copy;

        try {
            copy = new ArrayList<>(this.consumersByUuid.values());
        }
        finally {
            this.readLock.unlock();
        }

        for (Consumer<T> consumer : copy) {
            try {
                consumer.accept(t);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    Consumer<T> remove(UUID uuid) {
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
}
