package diarsid.support.objects;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Queue;
import java.util.function.Supplier;

public class SimplePool<T> {

    private final Object monitor;
    private final Queue<T> queue;
    private final Supplier<T> tNewObjectSupplier;

    public SimplePool(Supplier<T> newTSupplier) {
        this.monitor = new Object();
        this.queue = new ArrayDeque<>();
        this.tNewObjectSupplier = newTSupplier;
    }

    public T give() {
        T t;
        synchronized ( this.monitor ) {
            if ( this.queue.isEmpty() ) {
                t = this.tNewObjectSupplier.get();
            } else {
                t = this.queue.poll();
            }
        }
        return t;
    }

    public void takeBack(T t) {
        if ( t instanceof Closeable) {
            try {
                ((Closeable) t).close();
            }
            catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }
        else if ( t instanceof StatefulClearable ) {
            ((StatefulClearable) t).clear();
        }

        synchronized ( this.monitor ) {
            this.queue.offer(t);
        }
    }

    public void takeBackAll(Collection<T> ts) {
        for (T t : ts) {
            this.takeBack(t);
        }
        ts.clear();
    }
}
