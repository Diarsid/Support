/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package diarsid.support.objects;

import java.util.UUID;
import java.util.function.Supplier;

import static java.lang.String.format;
import static java.util.UUID.randomUUID;

import static diarsid.support.objects.Pools.createPool;
import static diarsid.support.objects.Pools.giveBackToPool;
import static diarsid.support.objects.Pools.nullablePoolOf;

/**
 *
 * @author Diarsid
 */
public abstract class PooledReusable implements AutoCloseable, StatefulClearable {
    
    private final UUID uuid;
    private final Object poolLock;
    private boolean isInPool;
    
    protected PooledReusable() {
        // empty constructor for creating new objects in pool
        this.uuid = randomUUID();
        this.poolLock = nullablePoolOf(this.getClass()).lock();
        this.isInPool = false;
    }
    
    protected static <T extends PooledReusable> void createPoolFor(
            Class<T> type, Supplier<T> tSupplier) {
        createPool(type, tSupplier);
    }
    
    protected abstract void clearForReuse();
    
    final Class getPooleableClass() {
        return this.getClass();
    }
    
    final void placedInPool() {
        synchronized ( this.poolLock ) {
            if ( this.isInPool ) {
                throw new IllegalStateException(format(
                        "Object %s@%s is already in pool!", 
                        this.getClass().getCanonicalName(), 
                        this.hashCode()));
            }
            this.isInPool = true;
        }
    }
    
    final void takenFromPool() {
        synchronized ( this.poolLock ) {
            if ( ! this.isInPool ) {
                throw new IllegalStateException(format(
                        "Object %s@%s is already not in pool!", 
                        this.getClass().getCanonicalName(), 
                        this.hashCode()));
            }
            this.isInPool = false;
        }
    }

    @Override
    public void close() {
        giveBackToPool(this);
    }
    
    @Override
    public void clear() {
        this.clearForReuse();
    }
    
    public final UUID identityUuid() {
        return this.uuid;
    }
    
    public final boolean isSame(PooledReusable other) {
        return this.uuid.equals(other.uuid);
    }
}
