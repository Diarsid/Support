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
    
    private static final Map<Class, Pool> POOLS_BY_POOLED_CLASS;
    
    static {
        POOLS_BY_POOLED_CLASS = new HashMap<>();
    }
    
    private Pools() { }
        
    static <T extends PooledReusable> void createPool(Class<T> type, Supplier<T> tSupplier) {
        synchronized ( POOLS_BY_POOLED_CLASS ) {
            Pool<T> existedPool = POOLS_BY_POOLED_CLASS.get(type);
            if ( existedPool == null ) {
                Pool<T> newTPool = new Pool<>(tSupplier);
                POOLS_BY_POOLED_CLASS.put(type, newTPool);
                logFor(Pools.class).info(format("Pool for %s created.", type.getCanonicalName()));
            }       
        } 
    }
    
    static <T extends PooledReusable> Pool<T> nullablePoolOf(Class<T> type) {
        return POOLS_BY_POOLED_CLASS.get(type);
    }
    
    public static <T extends PooledReusable> Optional<Pool<T>> poolOf(Class<T> type) {
        return Optional.ofNullable(POOLS_BY_POOLED_CLASS.get(type));
    }
    
    public static <T extends PooledReusable> T takeFromPool(Class<T> type) {
        Pool<T> pool = POOLS_BY_POOLED_CLASS.get(type);
        T pooled;
        if ( pool == null ) {
            pooled = initializePoolAndGetInstanceOf(type);
        } else {
            pooled = pool.give();
        }        
        return pooled;
    }
    
    public static <T, P extends PooledReusable> T usePooledObjectUnsafe(
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
    
    public static <P extends PooledReusable> void usePooledObject(
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
    
    private static <T extends PooledReusable> T initializePoolAndGetInstanceOf(Class<T> type) {
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
    
    public static <T extends PooledReusable> void giveBackToPool(T pooleable) {
        if ( pooleable == null ) {
            return;
        }
        
        Pool<T> pool = POOLS_BY_POOLED_CLASS.get(pooleable.getPooleableClass());
        pool.takeBack(pooleable);
    }
    
    public static <T extends PooledReusable> void giveBackAllToPoolAndClear(List<T> pooleables) {
        if ( pooleables == null || pooleables.isEmpty() ) {
            return;
        }
        
        Pool<T> pool = POOLS_BY_POOLED_CLASS.get(pooleables.get(0).getPooleableClass());
        pool.takeBackAll(pooleables);
    }
}
