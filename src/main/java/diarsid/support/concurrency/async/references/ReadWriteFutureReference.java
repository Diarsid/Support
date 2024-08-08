package diarsid.support.concurrency.async.references;

import java.util.Objects;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import diarsid.support.exceptions.InvalidLogicException;

import static java.lang.String.format;
import static java.util.Objects.nonNull;

public class ReadWriteFutureReference<T> implements
        FutureReference<T>,
        FutureReference.Read<T>,
        FutureReference.Write<T> {

    private final Class<T> refType;
    private final Lock refAccess;
    private final Condition refWritten;

    private boolean refNotWritten;
    private T ref;

    public ReadWriteFutureReference(Class<T> type) {
        this.refType = type;
        this.refAccess = new ReentrantLock();
        this.refWritten = this.refAccess.newCondition();
        this.refNotWritten = true;
    }

    @Override
    public T get() throws InterruptedException {
        if ( nonNull(this.ref) ) {
            return this.ref;
        }

        this.refAccess.lock();
        try {
            if ( nonNull(this.ref) ) {
                return ref;
            }

            do {
                this.refWritten.await();
            } while ( this.refNotWritten );

            return this.ref;
        }
        finally {
            this.refAccess.unlock();
        }
    }

    @Override
    public boolean set(T t) {
        if ( nonNull(this.ref) ) {
            throw new InvalidLogicException(format("%s<%s> is already set!",
                    FutureReference.class.getSimpleName(),
                    this.refType.getSimpleName()));
        }

        this.refAccess.lock();
        try {
            if ( nonNull(this.ref) ) {
                throw new InvalidLogicException(format("%s<%s> is already set!",
                        FutureReference.class.getSimpleName(),
                        this.refType.getSimpleName()));
            }

            this.ref = t;
            this.refNotWritten = false;
            this.refWritten.signalAll();
            return true;
        }
        finally {
            this.refAccess.unlock();
        }
    }

    @Override
    public Read<T> read() {
        return this;
    }

    @Override
    public Write<T> write() {
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ReadWriteFutureReference)) return false;
        ReadWriteFutureReference<?> that = (ReadWriteFutureReference<?>) o;
        return refNotWritten == that.refNotWritten &&
                refType.equals(that.refType) &&
                Objects.equals(ref, that.ref);
    }

    @Override
    public int hashCode() {
        return Objects.hash(refType, refNotWritten, ref);
    }
}
