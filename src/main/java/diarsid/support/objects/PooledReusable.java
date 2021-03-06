/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package diarsid.support.objects;

import java.util.UUID;

import diarsid.support.objects.references.Possible;

import static java.lang.String.format;
import static java.util.UUID.randomUUID;

import static diarsid.support.objects.Pools.POOL_NOT_SET_EXCEPTION;
import static diarsid.support.objects.references.References.simplePossibleButEmpty;

/**
 *
 * @author Diarsid
 */
public abstract class PooledReusable implements AutoCloseable, StatefulClearable, Initializable {
    
    private final UUID uuid;
    private final Object monitor;
    private final Possible<GuardedPool<PooledReusable>> pool;
    private boolean isInPool;
    
    protected PooledReusable() {
        // empty constructor for creating new objects in pool
        this.uuid = randomUUID();
        this.monitor = new Object();
        this.pool = simplePossibleButEmpty();
        this.isInPool = false;
    }
    
    protected abstract void clearForReuse();

    @Override
    public void init() {
        // to be overridden if needed
    }
    
    final void clearForReuseSynchronously() {
        synchronized ( this.monitor ) {
            this.clearForReuse();
        }
    }
    
    final Class getPooleableClass() {
        return this.getClass();
    }
    
    final void setPool(GuardedPool guardedPool) {
        this.pool.resetTo(guardedPool);
    }
    
    final void placedInPool() {        
        synchronized ( this.monitor ) {
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
        synchronized ( this.monitor ) {
            if ( ! this.isInPool ) {
                throw new IllegalStateException(format(
                        "Object %s@%s is already not in pool!", 
                        this.getClass().getCanonicalName(), 
                        this.hashCode()));
            }
            this.isInPool = false;
            this.init();
        }
    }

    @Override
    public final void close() {
        this.pool.orThrow(POOL_NOT_SET_EXCEPTION).takeBack(this);
    }
    
    @Override
    public final void clear() {
        this.clearForReuseSynchronously();
    }
    
    public final UUID identityUuid() {
        return this.uuid;
    }
    
    public final boolean isSame(PooledReusable other) {
        return this.uuid.equals(other.uuid);
    }
}
