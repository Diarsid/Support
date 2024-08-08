package diarsid.support.callbacks.groups;

import java.util.UUID;

import diarsid.support.callbacks.Listener;

public abstract class AbstractListener implements Listener {

    protected final UUID uuid;

    AbstractListener(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public UUID uuid() {
        return uuid;
    }
}
