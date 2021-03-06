package diarsid.support.objects;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Queue;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class SimplePool<T> {

    private final Object monitor;
    private final Queue<T> queue;
    private final Supplier<T> tNewObjectSupplier;
    private final Consumer<T> tInitializer;
    private final boolean hasInitializer;

    public SimplePool(Supplier<T> newTSupplier) {
        this.monitor = new Object();
        this.queue = new ArrayDeque<>();
        this.tNewObjectSupplier = newTSupplier;
        this.tInitializer = null;
        this.hasInitializer = false;
    }

    public SimplePool(Supplier<T> newTSupplier, Consumer<T> tInitializer) {
        this.monitor = new Object();
        this.queue = new ArrayDeque<>();
        this.tNewObjectSupplier = newTSupplier;
        this.tInitializer = tInitializer;
        this.hasInitializer = true;
    }

    public T give() {
        T t;
        synchronized ( this.monitor ) {
            if ( this.queue.isEmpty() ) {
                t = this.tNewObjectSupplier.get();
            } else {
                t = this.queue.poll();
            }

            if ( t instanceof Initializable ) {
                ((Initializable) t).init();
            }

            if ( this.hasInitializer ) {
                this.tInitializer.accept(t);
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
