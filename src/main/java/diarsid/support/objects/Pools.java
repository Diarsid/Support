package diarsid.support.objects;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.slf4j.LoggerFactory;

import static java.lang.String.format;
import static java.util.Arrays.stream;


public class Pools {    
    
    private static final Pools SINGLETON;
    static final Supplier<? extends RuntimeException> POOL_NOT_SET_EXCEPTION;
    
    private final Map<Class, GuardedPool> poolsByPooledClasses;
    
    static {
        SINGLETON = new Pools();
        String poolNotSetExceptionMessage = format("%s is not set in %s instance!", 
                                            GuardedPool.class.getSimpleName(),
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
        
    public <T extends PooledReusable> GuardedPool<T> createPool(Class<T> type, Supplier<T> tSupplier) {
        synchronized ( poolsByPooledClasses ) {
            GuardedPool<T> existedGuardedPool = poolsByPooledClasses.get(type);
            if ( existedGuardedPool == null ) {
                GuardedPool<T> newTGuardedPool = new GuardedPool<>(tSupplier);
                poolsByPooledClasses.put(type, newTGuardedPool);
                LoggerFactory.getLogger(Pools.class).info(format("Pool for %s created.", type.getCanonicalName()));
                existedGuardedPool = newTGuardedPool;
            }
            return existedGuardedPool;
        } 
    }
    
    <T extends PooledReusable> GuardedPool<T> nullablePoolOf(Class<T> type) {
        return poolsByPooledClasses.get(type);
    }
    
    public <T extends PooledReusable> Optional<GuardedPool<T>> poolOf(Class<T> type) {
        return Optional.ofNullable(poolsByPooledClasses.get(type));
    }
    
    public <T extends PooledReusable> T takeFromPool(Class<T> type) {
        GuardedPool<T> guardedPool = poolsByPooledClasses.get(type);
        T pooled;
        if ( guardedPool == null ) {
            pooled = initializePoolAndGetInstanceOf(type);
        } else {
            pooled = guardedPool.give();
        }        
        return pooled;
    }
    
    public <T, P extends PooledReusable> T usePooledObjectUnsafe(
            Class<P> type, Function<P, T> pooledObjectOperation) {
        P pooled = takeFromPool(type);
        try {            
            return pooledObjectOperation.apply(pooled);
        } catch (Throwable t) {
            LoggerFactory.getLogger(Pools.class).error("Exception during pooled object use:", t);
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
            LoggerFactory.getLogger(Pools.class).error("Exception during pooled object use:", t);
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
        
        GuardedPool<T> guardedPool = poolsByPooledClasses.get(pooleable.getPooleableClass());
        guardedPool.takeBack(pooleable);
    }
    
    public <T extends PooledReusable> void giveBackAllToPoolAndClear(List<T> pooleables) {
        if ( pooleables == null || pooleables.isEmpty() ) {
            return;
        }
        
        GuardedPool<T> guardedPool = poolsByPooledClasses.get(pooleables.get(0).getPooleableClass());
        guardedPool.takeBackAll(pooleables);
    }
}
