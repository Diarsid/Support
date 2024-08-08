package diarsid.support.concurrency.async.references;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static java.util.Objects.nonNull;

public class ReadWriteFutureReference<T> implements
        FutureReference<T>,
        FutureReference.Read<T>,
        FutureReference.Write<T> {

    private T t;
    private final Lock lock;
    private final Condition condition;
    private boolean conditionNotSignalled;

    public ReadWriteFutureReference() {
        this.lock = new ReentrantLock();
        this.condition = this.lock.newCondition();
        this.conditionNotSignalled = true;
    }

    @Override
    public T get() throws InterruptedException {
        if ( nonNull(this.t) ) {
            return this.t;
        }

        this.lock.lock();
        try {
            if ( nonNull(this.t) ) {
                return t;
            }

            do {
                this.condition.await();
            } while ( this.conditionNotSignalled );

            return this.t;
        }
        finally {
            this.lock.unlock();
        }
    }

    @Override
    public boolean set(T t) {
        if ( nonNull(this.t) ) {
            throw new IllegalStateException("is already set!");
        }

        this.lock.lock();
        try {
            if ( nonNull(this.t) ) {
                throw new IllegalStateException("is already set!");
            }

            this.t = t;
            this.conditionNotSignalled = false;
            this.condition.signalAll();
            return true;
        }
        finally {
            this.lock.unlock();
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
}
