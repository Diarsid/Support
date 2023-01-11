package diarsid.support.objects.references.impl;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import diarsid.support.objects.references.Reference;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import static diarsid.support.objects.references.Reference.Type.VALUE;
import static diarsid.support.objects.references.Reference.ValuePresence.NULLABLE;

class AbstractReadableNullable<T> implements Reference.Readable.Nullable<T> {

    protected volatile T t;

    AbstractReadableNullable() {
        this.t = null;
    }

    AbstractReadableNullable(T t) {
        this.t = t;
    }

    protected T internalSet(T newT) {
        T oldT = this.t;
        this.t = newT;
        return oldT;
    }

    @Override
    public Reference.Readable.Nullable<T> orDefault(T defaultT) {
        if ( isNull(this.t) ) {
            this.internalSet(defaultT);
        }
        return this;
    }

    @Override
    public <R> Reference.Readable.Nullable<R> map(Function<T, R> mapper) {
        if ( this.isPresent() ) {
            return new AbstractReadableNullable<>(mapper.apply(this.t));
        } else {
            return new AbstractReadableNullable<>();
        }
    }

    protected void checkValueNotNull() {
        if ( isNull(this.t) ) {
            throw new NullPointerException();
        }
    }

    @Override
    public boolean match(Predicate<T> predicate) {
        return this.isPresent() && predicate.test(this.t);
    }

    @Override
    public boolean notMatch(Predicate<T> predicate) {
        return this.isNotPresent() || ! predicate.test(this.t);
    }

    @Override
    public boolean isPresent() {
        return nonNull(this.t);
    }

    @Override
    public boolean isEmpty() {
        return isNull(this.t);
    }

    @Override
    public boolean isNotEmpty() {
        return nonNull(this.t);
    }

    @Override
    public boolean isNotPresent() {
        return isNull(this.t);
    }

    @Override
    public void ifPresent(Consumer<T> consumer) {
        if ( this.isPresent() ) {
            consumer.accept(this.t);
        }
    }

    @Override
    public void ifNotPresent(Runnable runnable) {
        if ( this.isNotPresent() ) {
            runnable.run();
        }
    }

//    @Override
//    public <R> SimplePossible<R> map(Function<T, R> mapper) {
//        if ( this.isPresent() ) {
//            return new SimplePossible<>(mapper.apply(this.t));
//        } else {
//            return new SimplePossible<>();
//        }
//    }

    @Override
    public <R> R mapValueOrThrow(Function<T, R> mapper) {
        if ( this.isPresent() ) {
            return mapper.apply(this.t);
        }
        else {
            throw new NullPointerException();
        }
    }

    @Override
    public <R> R mapValueOr(Function<T, R> mapper, R other) {
        if ( this.isPresent() ) {
            return mapper.apply(this.t);
        }
        else {
            return other;
        }
    }

    @Override
    public <R> R mapValueOrNull(Function<T, R> mapper) {
        return mapper.apply(this.t);
    }

    @Override
    public T orThrow() {
        this.checkValueNotNull();
        return this.t;
    }

    @Override
    public T orNull() {
        return this.t;
    }

//    @Override
//    public T extractOrThrow() {
//        this.checkValueNotNull();
//        return this.nullify();
//    }
//
//    @Override
//    public T extractOrNull() {
//        return this.nullify();
//    }
//
//    @Override
//    public T extractOr(T other) {
//        if ( isNull(this.t) ) {
//            return other;
//        }
//        else {
//            return this.nullify();
//        }
//    }

    @Override
    public T orThrow(Supplier<? extends RuntimeException> exceptionCreator) {
        if ( isNull(this.t) ) {
            throw exceptionCreator.get();
        } else {
            return this.t;
        }
    }

    @Override
    public T or(T otherT) {
        return isNull(this.t) ? otherT : t;
    }

    @Override
    public T orOther(Readable<T> readableT) {
        if ( readableT.valuePresence().is(NULLABLE) ) {
            return readableT.asNullable().orNull();
        }
        else {
            return readableT.asNonNull().get();
        }
    }

//    @Override
//    public T orOther(Possible<T> otherPossibleT) {
//        return isNull(this.t) ? otherPossibleT.orThrow() : t;
//    }

    @Override
    public Optional<T> optional() {
        return Optional.ofNullable(this.t);
    }
//
//    @Override
//    public T resetTo(T newT) {
//        if ( this.isPresent() && this.equalsTo(newT) ) {
//            return this.t;
//        }
//
//        if ( this.isNotPresent() && isNull(newT) ) {
//            return this.t;
//        }
//
//        T oldT = this.t;
//        this.internalSet(newT);
//        return oldT;
//    }
//
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
    public ValuePresence valuePresence() {
        return NULLABLE;
    }

    @Override
    public Type type() {
        return VALUE;
    }

//    @Override
//    public T resetTo(Optional<T> optionalT) {
//        return this.resetTo(optionalT.orElse(null));
//    }
//
//    @Override
//    public T resetTo(Possible<T> possibleT) {
//        return this.resetTo(possibleT.or(null));
//    }
//
//    @Override
//    public T ifPresentResetTo(T newT) {
//        if ( this.isPresent() ) {
//            return this.resetTo(newT);
//        } else {
//            return this.t;
//        }
//    }
//
//    @Override
//    public T ifNotPresentResetTo(T newT) {
//        if ( this.isNotPresent() ) {
//            return this.resetTo(newT);
//        } else {
//            return this.t;
//        }
//    }
//
//    @Override
//    public T nullify() {
//        T oldT = this.t;
//        this.internalSet(null);
//        return oldT;
//    }

    @Override
    public boolean equals(Object o) {
        return
                nonNull(this.t) &&
                        nonNull(o) &&
                        o instanceof SimplePossible &&
                        this.t.equals(((SimplePossible) o).t);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 37 * hash + Objects.hashCode(this.t);
        return hash;
    }

    @Override
    public boolean equalsTo(T otherT) {
        return nonNull(this.t) && this.t.equals(otherT);
    }

    @Override
    public boolean notEqualsTo(T otherT) {
        return nonNull(this.t) && ! this.t.equals(otherT);
    }

    @Override
    public String toString() {
        return "SimplePossible{" + t + '}';
    }

    @Override
    public boolean notEqualsToOther(Readable<T> readableT) {
        if ( this.isEmpty() ) {
            return true;
        }

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
