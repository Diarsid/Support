package diarsid.support.functional;

public interface ThrowingFunction<T, R> {

    R applyThrowing(T t) throws Throwable;

}
