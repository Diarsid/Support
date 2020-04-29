package diarsid.support.objects.references.real;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

import static java.lang.String.format;
import static java.util.Objects.isNull;

import static diarsid.support.objects.references.Reference.ValuePresence.PRESENT;

class RealPresent<T> implements Present<T> {

    private final String name;
    protected T t;

    RealPresent(T t, String name) {
        this.name = name;
        checkRequirements(t);
        this.t = t;
    }

    private final void checkRequirements(T offeredT) {
        if ( isNull(offeredT) ) {
            throw new NullPointerException(format("%s<%s> '%s' cannot be null!",
                    Present.class.getSimpleName(),
                    t.getClass().getSimpleName(),
                    this.name));
        }
    }

    protected T internalSet(T offeredT) {
        checkRequirements(offeredT);
        T oldT = this.t;
        this.t = offeredT;
        return oldT;
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
        return PRESENT;
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
        if (!(o instanceof RealPresent)) return false;
        RealPresent<?> that = (RealPresent<?>) o;
        return name.equals(that.name) &&
                t.equals(that.t);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, t);
    }
}
