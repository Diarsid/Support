package diarsid.support.concurrency.async.exchange.api;

import java.util.function.Consumer;

import diarsid.support.concurrency.ConcurrencyMode;

import static diarsid.support.concurrency.ConcurrencyMode.PARALLEL;

public class SimpleAsyncConsumer<T> implements AsyncExchangePoint.AsyncConsumer<T> {

    private final String name;
    private final Consumer<T> consumer;
    private final ConcurrencyMode concurrencyMode;

    public SimpleAsyncConsumer(String name, Consumer<T> consumer) {
        this.name = name;
        this.consumer = consumer;
        this.concurrencyMode = PARALLEL;
    }

    public SimpleAsyncConsumer(String name, Consumer<T> consumer, ConcurrencyMode concurrencyMode) {
        this.name = name;
        this.consumer = consumer;
        this.concurrencyMode = concurrencyMode;
    }

    @Override
    public void accept(T t) {
        this.consumer.accept(t);
    }

    @Override
    public ConcurrencyMode concurrencyMode() {
        return this.concurrencyMode;
    }

    @Override
    public String name() {
        return this.name;
    }
}
