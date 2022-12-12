package diarsid.support.concurrency.async.exchange.api;

public class SimpleBlockingSource<T> implements AsyncExchangePoint.BlockingSource<T> {

    private final String name;
    private final UnsafeSupplier<T> unsafeSupplier;

    public SimpleBlockingSource(String name, UnsafeSupplier<T> unsafeSupplier) {
        this.name = name;
        this.unsafeSupplier = unsafeSupplier;
    }

    @Override
    public T awaitAndTake() throws Exception {
        return this.unsafeSupplier.get();
    }

    @Override
    public String name() {
        return this.name;
    }
}
