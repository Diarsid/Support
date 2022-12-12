package diarsid.support.objects.references.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;

import diarsid.support.objects.references.Listenable;
import diarsid.support.objects.references.Listening;
import diarsid.support.objects.references.Present;
import diarsid.support.objects.references.PresentProperty;

import static java.lang.String.format;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.UUID.randomUUID;

import static diarsid.support.objects.references.Reference.Type.PROPERTY;
import static diarsid.support.objects.references.Reference.ValuePresence.NON_NULL;

public class RealPresentProperty<T> implements PresentProperty<T>, RealBindable<T>, ListenableRemovable<T>  {

    private final String name;
    protected T t;
    private final HashMap<RealListening<T>, BiConsumer<T, T>> listeners;
    private final Map<Listenable<T>, Listening<T>> bindings;

    public RealPresentProperty(T t, String name) {
        this.name = name;
        checkRequirements(t);
        this.t = t;
        this.listeners = new HashMap<>();
        this.bindings = new HashMap<>();
    }

    protected T internalSet(T newT) {
        checkRequirements(newT);
        T oldT = this.t;
        this.t = newT;

        if ( ! this.listeners.isEmpty() ) {
            this.listeners
                    .entrySet()
                    .forEach(pair -> {
                        if ( pair.getKey().isListening() ) {
                            try {
                                pair.getValue().accept(oldT, newT);
                            }
                            catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
        }
        return oldT;
    }

    @Override
    public Listening<T> listen(BiConsumer<T, T> listener) {
        RealListening<T> listening = new RealListening<>(this);
        this.listeners.put(listening, listener);
        return listening;
    }

    @Override
    public void clearListeners() {
        this.listeners.keySet().forEach(RealListening::cancelInternally);
    }

    @Override
    public boolean remove(Listening<T> listening) {
        if (listening instanceof RealListening) {
            BiConsumer<T, T> removed = this.listeners.remove(listening);
            return nonNull(removed);
        }
        else {
            return false;
        }
    }

    private final void checkRequirements(T offeredT) {
        if ( isNull(offeredT) ) {
            throw new NullPointerException(format("%s<%s> '%s' cannot be null!",
                    Present.class.getSimpleName(),
                    t.getClass().getSimpleName(),
                    this.name));
        }
    }

    @Override
    public T get() {
        return this.t;
    }

    @Override
    public <R> Readable.NonNull<R> map(Function<T, R> mapper) {
        return new RealPresentProperty<>(mapper.apply(this.t), this.name + ".mapped:" + randomUUID());
    }

    @Override
    public boolean equalsTo(T otherT) {
        return this.t.equals(otherT);
    }

    @Override
    public boolean notEqualsTo(T otherT) {
        return ! this.t.equals(otherT);
    }

    @Override
    public boolean match(Predicate<T> predicate) {
        return predicate.test(this.t);
    }

    @Override
    public boolean notMatch(Predicate<T> predicate) {
        return ! predicate.test(this.t);
    }

    @Override
    public T resetTo(T newT) {
        if ( this.equalsTo(newT) ) {
            return this.t;
        }

        return this.internalSet(newT);
    }

    @Override
    public T modify(Function<T, T> oldToNew) {
        T oldT = this.t;
        T newT = oldToNew.apply(oldT);

        if ( this.equalsTo(newT) ) {
            return this.t;
        }

        this.internalSet(newT);
        return oldT;
    }

    @Override
    public ValuePresence valuePresence() {
        return NON_NULL;
    }

    @Override
    public Type type() {
        return PROPERTY;
    }

    @Override
    public Map<Listenable<T>, Listening<T>> bindings() {
        return this.bindings;
    }

    @Override
    public String toString() {
        return "RealPresent{" +
                "name='" + name + '\'' +
                ", t=" + t +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RealPresentProperty)) return false;
        RealPresentProperty<?> that = (RealPresentProperty<?>) o;
        return name.equals(that.name) &&
                t.equals(that.t);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, t);
    }

    @Override
    public boolean notEqualsToOther(Readable<T> readableT) {
        if ( readableT instanceof Readable.NonNull ) {
            return Objects.equals(this.t, readableT.get());
        }
        else if ( readableT instanceof Readable.Nullable ) {
            return Objects.equals(this.t, ((Readable.Nullable<T>) readableT).or(null) );
        }
        else {
            return false;
        }
    }
}
