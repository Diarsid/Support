package diarsid.support.callbacks.groups;

import java.util.UUID;

import diarsid.support.callbacks.Callback;

public class ActiveCallback<C extends Callback> {

    private final UUID uuid;
    private final Callbacks<C> callbacks;

    ActiveCallback(UUID uuid, Callbacks<C> callbacks) {
        this.uuid = uuid;
        this.callbacks = callbacks;
    }

    public C cancel() {
        return this.callbacks.remove(this.uuid);
    }
}
