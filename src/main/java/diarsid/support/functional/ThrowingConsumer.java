package diarsid.support.functional;

public interface ThrowingConsumer<T> {

    void acceptThrowing(T t) throws Throwable;
}
