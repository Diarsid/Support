package diarsid.support.callbacks.groups;

import java.util.UUID;

import diarsid.support.callbacks.Callback;

public interface Callbacks<C extends Callback> {

    ActiveCallback<C> add(C callback);

    C remove(UUID uuid);
}
