package diarsid.support.objects.references.impl;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

import diarsid.support.objects.references.Present;

import static java.util.Objects.requireNonNull;

import static diarsid.support.objects.references.Reference.Type.VALUE;
import static diarsid.support.objects.references.Reference.ValuePresence.NON_NULL;

public class SimplePresent<T> implements Present<T> {

    private T t;

    public SimplePresent(T t) {
        requireNonNull(t);
        this.t = t;
    }

    @Override
    public T get() {
        return this.t;
    }

    @Override
    public <R> Readable.NonNull<R> map(Function<T, R> mapper) {
        return new SimplePresent<>(mapper.apply(this.t));
    }

    @Override
    public boolean equalsTo(T otherT) {
        return this.t.equals(otherT);
    }

    @Override
    public boolean notEqualsTo(T otherT) {
        return ! this.equalsTo(otherT);
    }

    @Override
    public boolean match(Predicate<T> predicate) {
        requireNonNull(predicate);
        return predicate.test(t);
    }

    @Override
    public boolean notMatch(Predicate<T> predicate) {
        requireNonNull(predicate);
        return ! predicate.test(t);
    }

    @Override
    public T resetTo(T newT) {
        requireNonNull(newT);
        T oldT = this.t;
        this.t = newT;
        return oldT;
    }

    @Override
    public T modify(Function<T, T> oldToNew) {
        requireNonNull(oldToNew);
        T oldT = this.t;
        T newT = oldToNew.apply(oldT);
        requireNonNull(newT);
        this.t = newT;
        return oldT;
    }

    @Override
    public String toString() {
        return "SimplePresent{" + this.t + '}';
    }

    @Override
    public ValuePresence valuePresence() {
        return NON_NULL;
    }

    @Override
    public Type type() {
        return VALUE;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SimplePresent)) return false;
        SimplePresent<?> that = (SimplePresent<?>) o;
        return t.equals(that.t);
    }

    @Override
    public int hashCode() {
        return Objects.hash(t);
    }
}
