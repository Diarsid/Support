package diarsid.support.callbacks;

import java.util.function.BiConsumer;

public interface BiValueCallback<T1, T2> extends BiConsumer<T1, T2>, Callback {

    void call(T1 t1, T2 t2);

    @Override
    default void accept(T1 t1, T2 t2) {
        this.call(t1, t2);
    }
}
