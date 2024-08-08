package diarsid.support.concurrency.async.references;

public interface FutureReference<T> {

    interface Read<T> {

        T get() throws InterruptedException;
    }

    interface Write<T> {

        boolean set(T t);
    }

    Read<T> read();

    Write<T> write();
}
