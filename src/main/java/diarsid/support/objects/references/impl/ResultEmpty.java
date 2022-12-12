package diarsid.support.objects.references.impl;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import diarsid.support.objects.references.Result;

import static diarsid.support.objects.references.Reference.Type.VALUE;
import static diarsid.support.objects.references.Reference.ValuePresence.NULLABLE;

public class ResultEmpty<T> implements Result<T>, Result.Reason {

    protected final Object reason;

    public ResultEmpty(Object reason) {
        this.reason = reason;
    }

    @Override
    public boolean equalsTo(T otherT) {
        return false;
    }

    @Override
    public boolean notEqualsTo(T otherT) {
        return true;
    }

    @Override
    public boolean match(Predicate<T> predicate) {
        return predicate.test(null);
    }

    @Override
    public boolean notMatch(Predicate<T> predicate) {
        return ! predicate.test(null);
    }

    @Override
    public ValuePresence valuePresence() {
        return NULLABLE;
    }

    @Override
    public Type type() {
        return VALUE;
    }

    @Override
    public boolean isNotPresent() {
        return true;
    }

    @Override
    public boolean isPresent() {
        return false;
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public boolean isNotEmpty() {
        return false;
    }

    @Override
    public void ifNotPresent(Runnable runnable) {
        runnable.run();
    }

    @Override
    public void ifPresent(Consumer<T> consumer) {
        // nothing
    }

    @Override
    public <R> R mapValueOrThrow(Function<T, R> mapper) {
        throw new NullPointerException();
    }

    @Override
    public <R> R mapValueOr(Function<T, R> mapper, R other) {
        return other;
    }

    @Override
    public <R> R mapValueOrNull(Function<T, R> mapper) {
        return null;
    }

    @Override
    public Optional<T> optional() {
        return Optional.empty();
    }

    @Override
    public T or(T otherT) {
        return otherT;
    }

    @Override
    public T orOther(Readable<T> readableT) {
        if ( readableT instanceof Readable.Nullable ) {
            return ((Readable.Nullable<T>) readableT).or(null);
        }
        else {
            return ((Readable.NonNull<T>) readableT).get();
        }
    }

    @Override
    public Nullable<T> orDefault(T defaultT) {
        return new AbstractReadableNullable<>(defaultT);
    }

    @Override
    public <R> Nullable<R> map(Function<T, R> mapper) {
        return new AbstractReadableNullable<>();
    }

    @Override
    public T orThrow() {
        throw new NullPointerException();
    }

    @Override
    public T orNull() {
        return null;
    }

    @Override
    public T orThrow(Supplier<? extends RuntimeException> exceptionCreator) {
        throw exceptionCreator.get();
    }

    @Override
    public boolean notEqualsToOther(Readable<T> readableT) {
        return true;
    }

    @Override
    public Reason reason() {
        return this;
    }

    @Override
    public Object subject() {
        return this.reason;
    }
}
