package diarsid.support.objects.groups;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

import static java.util.UUID.randomUUID;

public class Consumers<T> {

    private final Map<UUID, Consumer<T>> consumersByUuid;

    public Consumers() {
        this.consumersByUuid = new HashMap<>();
    }

    public Consuming<T> add(Consumer<T> consumer) {
        UUID uuid = randomUUID();
        this.consumersByUuid.put(uuid, consumer);
        return new Consuming<>(uuid, this);
    }

    public void accept(T t) {
        for (Consumer<T> consumer : this.consumersByUuid.values()) {
            try {
                consumer.accept(t);
            }
            catch (Exception e) {

            }
        }
    }

    Consumer<T> remove(UUID uuid) {
        Consumer<T> consumer;
        consumer = this.consumersByUuid.remove(uuid);
        return consumer;
    }
}
