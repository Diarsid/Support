package diarsid.support.concurrency.signals;

import java.io.Closeable;
import java.util.function.Consumer;

import diarsid.support.concurrency.ConcurrencyMode;
import diarsid.support.model.Named;

import static diarsid.support.concurrency.ConcurrencyMode.DEFAULT;

public interface Signal extends Closeable {

    String name();

    String listen(Listener listener);

    default String listen(Runnable reaction) {
        var listener = new SignalSimpleListener(reaction);
        return this.listen(listener);
    }

    default String listen(Consumer<Object> reactionOnData) {
        var listener = new SignalSimpleListener(reactionOnData);
        return this.listen(listener);
    }

    default String listen(Runnable reaction, Consumer<Object> reactionOnData) {
        var listener = new SignalSimpleListener(reaction, reactionOnData);
        return this.listen(listener);
    }

    boolean dontListen(String name);

    void emit();

    void emitWith(Object data);

    void cancel();

    @Override
    default void close() {
        this.cancel();
    }

    interface Listener extends Named {

        interface OfData extends Listener {

            void onEmittedWith(Object data);
        }

        void onEmitted();

        default ConcurrencyMode concurrencyMode() {
            return DEFAULT;
        }
    }

}
