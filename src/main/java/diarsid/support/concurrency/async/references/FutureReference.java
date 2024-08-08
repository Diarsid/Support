package diarsid.support.concurrency.async.references;

import diarsid.support.exceptions.InvalidLogicException;

public interface FutureReference<T> {

    interface Read<T> {

        T get() throws InterruptedException;
    }

    interface Write<T> {

        boolean set(T t) throws InvalidLogicException;
    }

    Read<T> read();

    Write<T> write();
}
