package diarsid.support.callbacks.groups;

import java.util.UUID;

import diarsid.support.callbacks.Listener;

public interface Listeners {

    boolean remove(UUID uuid);

    default boolean remove(Listener listener) {
        return remove(listener.uuid());
    }
}
