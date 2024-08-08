package diarsid.support.objects.references;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import diarsid.support.objects.references.impl.SimplePresent;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import static diarsid.support.objects.references.Reference.Type.VALUE;
import static diarsid.support.objects.references.Reference.ValuePresence.NON_NULL;
import static diarsid.support.objects.references.Reference.ValuePresence.NULLABLE;

public final class Lazy<T> implements Reference.Readable.NonNull<T> {

    private final Supplier<T> lazy;
    private final Lock lock = new ReentrantLock(true);
    private volatile T value;

    public Lazy(Supplier<T> lazy) {
        this.lazy = lazy;
    }

    public T get() {
        if ( nonNull(value) ) {
            return value;
        }
        else {
            lock.lock();
            try {
                if ( isNull(value) ) {
                    value = lazy.get();
                    if ( isNull(value) ) {
                        throw new NullPointerException(Lazy.class.getSimpleName() + " Supplier<T> is not expected to return null!");
                    }
                }
                return value;
            }
            finally {
                lock.unlock();
            }
        }
    }

    @Override
    public <R> NonNull<R> map(Function<T, R> mapper) {
        return new SimplePresent<>(mapper.apply(this.get()));
    }

    @Override
    public boolean equalsTo(T other) {
        if ( isNull(other) ) {
            return false;
        }

        return this.get().equals(other);
    }

    @Override
    public boolean notEqualsTo(T other) {
        return ! this.equalsTo(other);
    }

    @Override
    public boolean match(Predicate<T> predicate) {
        return predicate.test(this.get());
    }

    @Override
    public boolean notMatch(Predicate<T> predicate) {
        return ! predicate.test(this.get());
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
    public boolean notEqualsToOther(Readable<T> readable) {
        T other;
        if ( readable.valuePresence().is(NULLABLE) ) {
            other = readable.asNullable().orNull();
            if ( isNull(other) ) {
                return true;
            }

            return other.equals(this.get());
        }
        else {
            other = readable.get();
            return other.equals(this.get());
        }
    }
}
