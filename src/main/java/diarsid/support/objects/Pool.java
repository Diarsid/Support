/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package diarsid.support.objects;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Queue;
import java.util.function.Supplier;


/**
 *
 * @author Diarsid
 */
public final class Pool<T extends PooledReusable> {
    
    private final Object lock;
    private final Queue<T> queue;
    private final Supplier<T> tNewObjectSupplier;
    
    Pool(Supplier<T> newTSupplier) {
        this.lock = new Object();
        this.queue = new ArrayDeque<>();
        this.tNewObjectSupplier = newTSupplier;
    }
    
    Object lock() {
        return this.lock;
    }
    
    public T give() {
        synchronized ( this.lock ) {
            T t;
            if ( this.queue.peek() == null ) {
                t = this.tNewObjectSupplier.get();
                t.placedInPool();
            } else {
                t = this.queue.poll();
            }
            t.takenFromPool();
            return t;
        }
    }
    
    public void takeBack(T t) {
        t.clearForReuse();
        synchronized ( this.lock ) {
            t.placedInPool();
            this.queue.offer(t);
        }
    }
    
    public void takeBackAll(Collection<T> ts) {
        for (T t : ts) {
            t.clearForReuse();
        }
        synchronized ( this.lock ) {
            for (T t : ts) {
                t.placedInPool();
                this.queue.offer(t);
            }            
        }        
        ts.clear();
    }
}
