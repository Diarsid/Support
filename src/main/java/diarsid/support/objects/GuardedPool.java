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
public final class GuardedPool<T extends PooledReusable> {
    
    private final Object monitor;
    private final Queue<T> queue;
    private final Supplier<T> tNewObjectSupplier;
    
    GuardedPool(Supplier<T> newTSupplier) {
        this.monitor = new Object();
        this.queue = new ArrayDeque<>();
        this.tNewObjectSupplier = newTSupplier;
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
