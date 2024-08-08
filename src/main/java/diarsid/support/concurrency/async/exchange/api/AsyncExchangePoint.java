package diarsid.support.concurrency.async.exchange.api;

import java.util.concurrent.BlockingQueue;
import java.util.function.Consumer;

import diarsid.support.concurrency.ConcurrencyMode;
import diarsid.support.concurrency.async.exchange.impl.AsyncExchangePointImpl;
import diarsid.support.model.Named;
import diarsid.support.objects.workers.Worker;

public interface AsyncExchangePoint<T> extends Named, Worker.Destroyable {

    public static <T> AsyncExchangePoint<T> newInstance(String name) {
        return new AsyncExchangePointImpl<>(name);
    }

    interface BlockingSource<T> extends Named {

        T awaitAndTake() throws Exception;

        interface Group<T> {

            boolean add(BlockingSource<T> source);

            default void add(String name, BlockingQueue<T> sourceQueue) {
                this.add(new SimpleBlockingSource<>(name, sourceQueue::take));
            }

            boolean remove(String sourceName);
        }
    }

    interface AsyncConsumer<T> extends Named {

        void accept(T t) throws Exception;

        ConcurrencyMode concurrencyMode();

        interface Group<T> {

            boolean add(AsyncConsumer<T> consumer);

            default void add(String name, Consumer<T> consumer) {
                this.add(new SimpleAsyncConsumer<>(name, consumer));
            }

            boolean remove(String consumerName);
        }
    }

    BlockingSource.Group<T> sources();

    AsyncConsumer.Group<T> consumers();

    void put(T t);
}
