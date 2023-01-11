package diarsid.support.objects.references.impl;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import diarsid.support.objects.references.Possible;

import static java.util.Objects.isNull;

public class SimplePossible<T> extends AbstractReadableNullable<T> implements Possible<T> {

    protected volatile T t;

    public SimplePossible() {
        super();
    }

    public SimplePossible(T t) {
        super(t);
    }
    
    @Override
    public <R> Possible<R> map(Function<T, R> mapper) {
        if ( this.isPresent() ) {
            return new SimplePossible<>(mapper.apply(this.t));
        } else {
            return new SimplePossible<>();
        }
    }
    
    @Override
    public T extractOrThrow() {
        this.checkValueNotNull();
        return this.nullify();
    }

    @Override
    public T extractOrNull() {
        return this.nullify();
    }

    @Override
    public T extractOr(T other) {
        if ( isNull(this.t) ) {
            return other;
        }
        else {
            return this.nullify();
        }
    }

    @Override
    public T orOther(Possible<T> otherPossibleT) {
        return isNull(this.t) ? otherPossibleT.orThrow() : this.t;
    }
    
    @Override
    public T resetTo(T newT) {
        if ( this.isPresent() && this.equalsTo(newT) ) {
            return this.t;
        }

        if ( this.isNotPresent() && isNull(newT) ) {
            return this.t;
        }

        T oldT = this.t;
        this.internalSet(newT);
        return oldT;
    }

//    @Override
//    public T modify(Function<T, T> oldToNew) {
//        T oldT = this.t;
//        T newT = oldToNew.apply(oldT);
//
//        if ( this.isPresent() && this.equalsTo(newT) ) {
//            return this.t;
//        }
//
//        if ( this.isNotPresent() && isNull(newT) ) {
//            return this.t;
//        }
//
//        this.internalSet(newT);
//        return oldT;
//    }

    @Override
    public T resetTo(Optional<T> optionalT) {
        return this.resetTo(optionalT.orElse(null));
    }

    @Override
    public T resetTo(Readable.Nullable<T> nullableT) {
        return this.resetTo(nullableT.or(null));
    }

    @Override
    public T resetTo(Readable.NonNull<T> nonNullT) {
        return this.resetTo(nonNullT.get());
    }

    @Override
    public T modifyNullable(Function<T, T> oldNullableToNew) {
        return this.resetTo(oldNullableToNew.apply(this.t));
    }

    @Override
    public T modifyIfPresent(Function<T, T> oldNullableToNew) {
        if ( this.isPresent() ) {
            return this.resetTo(oldNullableToNew.apply(this.t));
        }
        return this.t;
    }

    @Override
    public T modifyIfPresent(Consumer<T> mutateOldTWhenPresent) {
        if ( this.isPresent() ) {
            mutateOldTWhenPresent.accept(this.t);
        }
        return this.t;
    }

    @Override
    public T resetTo(Possible<T> possibleT) {
        return this.resetTo(possibleT.or(null));
    }
    
    @Override
    public T ifPresentResetTo(T newT) {
        if ( this.isPresent() ) {
            return this.resetTo(newT);
        } else {
            return null;
        }
    }
    
    @Override
    public T ifNotPresentResetTo(T newT) {
        if ( this.isNotPresent() ) {
            return this.resetTo(newT);
        } else {
            return null;
        }
    }
    
    @Override
    public T nullify() {
        T oldT = this.t;
        this.internalSet(null);
        return oldT;
    }

    @Override
    public boolean notEquals(Possible<T> possibleT) {
        return ! this.equals(possibleT);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SimplePossible)) return false;
        if (!super.equals(o)) return false;
        SimplePossible<?> that = (SimplePossible<?>) o;
        return Objects.equals(t, that.t);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), t);
    }
}
