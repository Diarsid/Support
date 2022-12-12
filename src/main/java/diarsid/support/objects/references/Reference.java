package diarsid.support.objects.references;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import diarsid.support.objects.CommonEnum;

public interface Reference<T> extends Supplier<T> {

    public @interface ReturnsNullable {}

    public @interface ReturnsNonNull {}

    enum ValuePresence implements CommonEnum<ValuePresence> {
        NULLABLE,
        NON_NULL
    }

    enum Type implements CommonEnum<Type> {
        VALUE,
        PROPERTY
    }

    boolean equalsTo(T otherT);

    boolean notEqualsTo(T otherT);

    boolean match(Predicate<T> predicate);

    boolean notMatch(Predicate<T> predicate);

    Reference.ValuePresence valuePresence();

    Reference.Type type();

    default Readable.Nullable<T> asNullable() {
        if ( this instanceof Readable.Nullable ) {
            return (Readable.Nullable<T>) this;
        }

        throw new IllegalStateException();
    }

    default Readable.NonNull<T> asNonNull() {
        if ( this instanceof Readable.NonNull ) {
            return (Readable.NonNull<T>) this;
        }

        throw new IllegalStateException();
    }

    interface Readable<T> extends Reference<T> {

        interface Nullable<T> extends Readable<T> {

            boolean isNotPresent();

            boolean isPresent();

            boolean isEmpty();

            boolean isNotEmpty();

            void ifNotPresent(Runnable runnable);

            void ifPresent(Consumer<T> consumer);

            @ReturnsNonNull
            <R> R mapValueOrThrow(Function<T, R> mapper);

            @ReturnsNonNull
            <R> R mapValueOr(Function<T, R> mapper, R other);

            @ReturnsNullable
            <R> R mapValueOrNull(Function<T, R> mapper);

            Optional<T> optional();

            @ReturnsNonNull
            T or(T otherT);

            @ReturnsNullable
            T orOther(Readable<T> readableT);

            Reference.Readable.Nullable<T> orDefault(T defaultT);

            <R> Reference.Readable.Nullable<R> map(Function<T, R> mapper);

            @ReturnsNonNull
            T orThrow();

            @ReturnsNullable
            T orNull();

            @ReturnsNonNull
            T orThrow(Supplier<? extends RuntimeException> exceptionCreator);

            @ReturnsNonNull
            @Override
            default T get() {
                return this.orThrow();
            }

        }

        interface NonNull<T> extends Readable<T> {

            T get();

            <R> NonNull<R> map(Function<T, R> mapper);

        }

        boolean notEqualsToOther(Readable<T> readableT);
    }

    interface Writable<T> extends Readable<T> {

        interface Nullable<T> extends Readable.Nullable<T>, Writable<T> {

            @ReturnsNullable
            T ifNotPresentResetTo(T newT);

            @ReturnsNullable
            T ifPresentResetTo(T newT);

            default T resetOrMerge(T newT, BiFunction<T, T, T> oldAndNewMerging) {
                if ( this.isPresent() ) {
                    T oldT = this.orThrow();
                    T merged = oldAndNewMerging.apply(oldT, newT);
                    this.resetTo(merged);
                    return oldT;
                }
                else {
                    return this.resetTo(newT);
                }
            }

            @ReturnsNullable
            T nullify();

            @ReturnsNonNull
            T extractOrThrow();

            @ReturnsNullable
            T extractOrNull();

            T extractOr(T t);

            @ReturnsNullable
            T resetTo(Optional<T> optionalT);

            @ReturnsNullable
            T resetTo(Readable.Nullable<T> nullableT);

            @ReturnsNullable
            T resetTo(Readable.NonNull<T> nonNullT);

            T modifyNullable(Function<T, T> oldNullableToNew);

            T modifyIfPresent(Function<T, T> oldNullableToNew);

            T modifyIfPresent(Consumer<T> mutateOldTWhenPresent);

        }

        interface NonNull<T> extends Readable.NonNull<T>, Writable<T> {

            T modify(Function<T, T> oldToNew);

        }

        @ReturnsNullable
        T resetTo(T newT);

        @ReturnsNullable
        default T resetTo(Supplier<T> supplierT) {
            return this.resetTo(supplierT.get());
        }

        default Writable.Nullable<T> asNullable() {
            if ( this instanceof Readable.Nullable ) {
                return (Writable.Nullable<T>) this;
            }

            throw new IllegalStateException();
        }

        default Writable.NonNull<T> asNonNull() {
            if ( this instanceof Readable.NonNull ) {
                return (Writable.NonNull<T>) this;
            }

            throw new IllegalStateException();
        }

    }
}
