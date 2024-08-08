package diarsid.support.concurrency.async.exchange.impl;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import diarsid.support.concurrency.async.exchange.api.AsyncExchangePoint;
import diarsid.support.concurrency.stateful.workers.AbstractStatefulDestroyableWorker;
import diarsid.support.concurrency.threads.NamedThreadSource;

import static java.lang.String.format;

public class AsyncExchangePointImpl<T> extends AbstractStatefulDestroyableWorker implements AsyncExchangePoint<T> {

    private final AsyncConsumerGroup<T> consumerGroup;
    private final BlockingSourceGroup<T> sourceGroup;
    final NamedThreadSource namedThreadSource;
    final BlockingQueue<T> queue;

    public AsyncExchangePointImpl(String name) {
        this(name, new ArrayBlockingQueue<>(10));
    }

    public AsyncExchangePointImpl(String name, BlockingQueue<T> queue) {
        super(name);
        this.queue = queue;
        this.namedThreadSource = new NamedThreadSource(format("%s[%s]", AsyncExchangePoint.class.getSimpleName(), name));
        this.consumerGroup = new AsyncConsumerGroup<>(this);
        this.sourceGroup = new BlockingSourceGroup<>(this);
    }

    @Override
    protected boolean doSynchronizedStartWork() {
        this.consumerGroup.startWork();
        this.sourceGroup.startWork();
        return true;
    }

    @Override
    protected boolean doSynchronizedDestroy() {
        this.sourceGroup.destroy();
        this.consumerGroup.destroy();
        this.namedThreadSource.close();
        return true;
    }

    @Override
    public BlockingSource.Group<T> sources() {
        return this.sourceGroup;
    }

    @Override
    public AsyncConsumer.Group<T> consumers() {
        return this.consumerGroup;
    }

    @Override
    public void put(T t) {
        try {
            this.queue.put(t);
        }
        catch (InterruptedException e) {
            // TODO
        }
    }
}
