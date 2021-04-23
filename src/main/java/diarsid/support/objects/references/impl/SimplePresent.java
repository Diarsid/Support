package diarsid.support.objects.references.impl;

import java.util.function.Function;
import java.util.function.Predicate;

import diarsid.support.objects.references.Present;

import static java.util.Objects.requireNonNull;

import static diarsid.support.objects.references.Reference.Type.VALUE;
import static diarsid.support.objects.references.Reference.ValuePresence.PRESENT;

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
        return PRESENT;
    }

    @Override
    public Type type() {
        return VALUE;
    }
}
