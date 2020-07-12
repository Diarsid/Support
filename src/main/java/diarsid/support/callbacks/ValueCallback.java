package diarsid.support.callbacks;

import java.util.function.Consumer;

public interface ValueCallback<T> extends Consumer<T>, Callback {

    void call(T t);

    @Override
    default void accept(T t) {
        this.call(t);
    }
}
