package diarsid.support.concurrency.signals;

import java.util.Objects;
import java.util.function.Consumer;

import static java.util.Objects.isNull;
import static java.util.UUID.randomUUID;

public class SignalSimpleListener implements Signal.Listener.OfData {

    private final String name;
    private final Runnable reaction;
    private final Consumer<Object> reactionWithData;

    public SignalSimpleListener(
            String name,
            Runnable reaction,
            Consumer<Object> reactionWithData) {
        this.name = name;
        this.reaction = reaction;
        this.reactionWithData = reactionWithData;
    }

    public SignalSimpleListener(
            String name,
            Runnable reaction) {
        this(name, reaction, null);
    }

    public SignalSimpleListener(
            String name,
            Consumer<Object> reactionWithData) {
        this(name, null, reactionWithData);
    }

    public SignalSimpleListener(
            Runnable reaction,
            Consumer<Object> reactionWithData) {
        this(randomUUID().toString(), reaction, reactionWithData);
    }

    public SignalSimpleListener(
            Runnable reaction) {
        this(randomUUID().toString(), reaction, null);
    }

    public SignalSimpleListener(
            Consumer<Object> reactionWithData) {
        this(randomUUID().toString(), null, reactionWithData);
    }

    @Override
    public void onEmitted() {
        if (isNull(this.reaction)) {
            this.reactionWithData.accept(null);
        } else {
            this.reaction.run();
        }
    }

    @Override
    public void onEmittedWith(Object data) {
        if (isNull(this.reactionWithData)) {
            this.reaction.run();
        } else {
            this.reactionWithData.accept(data);
        }
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SignalSimpleListener)) return false;
        SignalSimpleListener that = (SignalSimpleListener) o;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
