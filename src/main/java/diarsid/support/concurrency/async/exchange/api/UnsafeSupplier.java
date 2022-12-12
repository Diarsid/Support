package diarsid.support.concurrency.async.exchange.api;

@FunctionalInterface
public interface UnsafeSupplier<T> {

    T get() throws Exception;
}
