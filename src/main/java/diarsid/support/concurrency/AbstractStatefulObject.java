package diarsid.support.concurrency;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import static java.lang.String.format;
import static java.util.Objects.nonNull;

public abstract class AbstractStatefulObject {

    /* fair mode is important to give a higher precedency to quick-to-do write operations */
    private static final boolean FAIR_LOCK_MODE = true;

    private final BiConsumer<String, Throwable> unexpectedExceptionsConsumer;
    private final ReadWriteLock objectReadWriteLock;

    public AbstractStatefulObject() {
        this.unexpectedExceptionsConsumer = null;
        this.objectReadWriteLock = new ReentrantReadWriteLock(FAIR_LOCK_MODE);
    }

    public AbstractStatefulObject(BiConsumer<String, Throwable> unexpectedExceptionsConsumer) {
        this.unexpectedExceptionsConsumer = unexpectedExceptionsConsumer;
        this.objectReadWriteLock = new ReentrantReadWriteLock(FAIR_LOCK_MODE);
    }

    protected void doSynchronizedVoidRead(Runnable objectReadOperation) {
        try {
            objectReadWriteLock.readLock().lock();
            objectReadOperation.run();
        } catch (Throwable t) {
            if (nonNull(unexpectedExceptionsConsumer)) {
                String message = format("Unexpected exception during synchronous void read in %s",
                        this.getClass().getCanonicalName());
                unexpectedExceptionsConsumer.accept(message, t);
            }
            throw t;
        } finally {
            objectReadWriteLock.readLock().unlock();
        }
    }

    protected <T> T doSynchronizedReturnRead(Supplier<T> objectReadOperation) {
        try {
            objectReadWriteLock.readLock().lock();
            return objectReadOperation.get();
        } catch (Throwable t) {
            if (nonNull(unexpectedExceptionsConsumer)) {
                String message = format("Unexpected exception during synchronous return read in %s",
                        this.getClass().getCanonicalName());
                unexpectedExceptionsConsumer.accept(message, t);
            }
            throw t;
        } finally {
            objectReadWriteLock.readLock().unlock();
        }
    }

    protected void doSynchronizedVoidChange(Runnable objectChangeVoidOperation) {
        try {
            objectReadWriteLock.writeLock().lock();
            objectChangeVoidOperation.run();
        } catch (Throwable t) {
            if (nonNull(unexpectedExceptionsConsumer)) {
                String message = format("Unexpected exception during synchronous void change in %s",
                        this.getClass().getCanonicalName());
                unexpectedExceptionsConsumer.accept(message, t);
            }
            throw t;
        } finally {
            objectReadWriteLock.writeLock().unlock();
        }
    }

    protected <T> T doSynchronizedReturnChange(Supplier<T> objectChangeReturnOperation) {
        try {
            objectReadWriteLock.writeLock().lock();
            return objectChangeReturnOperation.get();
        } catch (Throwable t) {
            if (nonNull(unexpectedExceptionsConsumer)) {
                String message = format("Unexpected exception during synchronous return change in %s",
                        this.getClass().getCanonicalName());
                unexpectedExceptionsConsumer.accept(message, t);
            }
            throw t;
        } finally {
            objectReadWriteLock.writeLock().unlock();
        }
    }

}
