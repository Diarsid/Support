package diarsid.support.objects.groups;

import java.util.UUID;
import java.util.function.Consumer;

public class Consuming<T>  {

    private final UUID uuid;
    private final Consumers<T> consumers;

    Consuming(UUID uuid, Consumers<T> consumers) {
        this.uuid = uuid;
        this.consumers = consumers;
    }

    public Consumer<T> cancel() {
        return this.consumers.remove(this.uuid);
    }
}
