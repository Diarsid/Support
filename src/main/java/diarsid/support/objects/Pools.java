/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package diarsid.support.objects;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.lang.String.format;
import static java.util.Arrays.stream;

import static diarsid.support.log.Logging.logFor;


/**
 *
 * @author Diarsid
 */
public class Pools {    
    
    private static final Pools SINGLETON;
    static final Supplier<? extends RuntimeException> POOL_NOT_SET_EXCEPTION;
    
    private final Map<Class, Pool> poolsByPooledClasses;
    
    static {
        SINGLETON = new Pools();
        String poolNotSetExceptionMessage = format("%s is not set in %s instance!", 
                                            Pool.class.getSimpleName(), 
                                            PooledReusable.class.getSimpleName());
        POOL_NOT_SET_EXCEPTION = () -> {
            return new PoolException(poolNotSetExceptionMessage);
        };
    }
    
    public Pools() {
        this.poolsByPooledClasses = new HashMap<>();
    }
    
    public static Pools pools() {
        return SINGLETON;
    }
        
    public <T extends PooledReusable> Pool<T> createPool(Class<T> type, Supplier<T> tSupplier) {
        synchronized ( poolsByPooledClasses ) {
            Pool<T> existedPool = poolsByPooledClasses.get(type);
            if ( existedPool == null ) {
                Pool<T> newTPool = new Pool<>(tSupplier);
                poolsByPooledClasses.put(type, newTPool);
                logFor(Pools.class).info(format("Pool for %s created.", type.getCanonicalName()));
                existedPool = newTPool;
            }
            return existedPool;
        } 
    }
    
    <T extends PooledReusable> Pool<T> nullablePoolOf(Class<T> type) {
        return poolsByPooledClasses.get(type);
    }
    
    public <T extends PooledReusable> Optional<Pool<T>> poolOf(Class<T> type) {
        return Optional.ofNullable(poolsByPooledClasses.get(type));
    }
    
    public <T extends PooledReusable> T takeFromPool(Class<T> type) {
        Pool<T> pool = poolsByPooledClasses.get(type);
        T pooled;
        if ( pool == null ) {
            pooled = initializePoolAndGetInstanceOf(type);
        } else {
            pooled = pool.give();
        }        
        return pooled;
    }
    
    public <T, P extends PooledReusable> T usePooledObjectUnsafe(
            Class<P> type, Function<P, T> pooledObjectOperation) {
        P pooled = takeFromPool(type);
        try {            
            return pooledObjectOperation.apply(pooled);
        } catch (Throwable t) {
            logFor(Pools.class).error("Exception during pooled object use:", t);
            throw t;
        } finally {
            giveBackToPool(pooled);
        }
    }
    
    public <P extends PooledReusable> void usePooledObject(
            Class<P> type, Consumer<P> pooledObjectOperation) {
        P pooled = takeFromPool(type);
        try {            
            pooledObjectOperation.accept(pooled);
        } catch (Throwable t) {
            logFor(Pools.class).error("Exception during pooled object use:", t);
        } finally {
            giveBackToPool(pooled);
        }
    }
    
    private <T extends PooledReusable> T initializePoolAndGetInstanceOf(Class<T> type) {
        Constructor noArgsConstructor = stream(type.getDeclaredConstructors())
                .filter(constructor -> constructor.getParameterCount() == 0)
                .peek(constructor -> constructor.setAccessible(true))
                .findFirst()
                .orElseThrow(() -> {
                    return new PoolException("Cannot find pooled no-args constructor!");
                });
        
        try {
            return (T) noArgsConstructor.newInstance();
        } catch (Exception e) {
            throw new PoolException(
                    "Cannot initialize instance and Pool for class " + type.getCanonicalName());
        }
    }
    
    public <T extends PooledReusable> void giveBackToPool(T pooleable) {
        if ( pooleable == null ) {
            return;
        }
        
        Pool<T> pool = poolsByPooledClasses.get(pooleable.getPooleableClass());
        pool.takeBack(pooleable);
    }
    
    public <T extends PooledReusable> void giveBackAllToPoolAndClear(List<T> pooleables) {
        if ( pooleables == null || pooleables.isEmpty() ) {
            return;
        }
        
        Pool<T> pool = poolsByPooledClasses.get(pooleables.get(0).getPooleableClass());
        pool.takeBackAll(pooleables);
    }
}
