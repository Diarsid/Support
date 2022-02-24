package diarsid.support.objects;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Queue;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class GuardedPool<T extends PooledReusable> {
    
    private final Object monitor;
    private final Queue<T> queue;
    private final Supplier<T> tNewObjectSupplier;
    private final Consumer<T> tInitializer;
    private final boolean hasInitializer;
    
    public GuardedPool(Supplier<T> newTSupplier) {
        this.monitor = new Object();
        this.queue = new ArrayDeque<>();
        this.tNewObjectSupplier = newTSupplier;
        this.tInitializer = null;
        this.hasInitializer = false;
    }

    public GuardedPool(Supplier<T> newTSupplier, Consumer<T> tInitializer) {
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
                t.setPool(this);
                t.placedInPool();
            } else {
                t = this.queue.poll();
            }            
        }
        t.takenFromPool();
        if ( this.hasInitializer ) {
            this.tInitializer.accept(t);
        }
        return t;
    }
    
    public void takeBack(T t) {
        t.clearForReuseSynchronously();
        t.placedInPool();
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
