package diarsid.support.callbacks.groups;

import java.util.UUID;
import java.util.function.Consumer;

import diarsid.support.callbacks.ValueListener;

public class ValueListenerImpl<T> extends AbstractListener implements ValueListener<T> {

    private final Consumer<T> consumer;

    public ValueListenerImpl(UUID uuid, Consumer<T> consumer) {
        super(uuid);
        this.consumer = consumer;
    }

    @Override
    public void accept(T t) {
        this.consumer.accept(t);
    }
}
