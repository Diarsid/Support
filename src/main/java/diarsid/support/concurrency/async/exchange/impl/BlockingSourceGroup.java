package diarsid.support.concurrency.async.exchange.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

import diarsid.support.concurrency.async.exchange.api.AsyncExchangePoint;
import diarsid.support.concurrency.stateful.workers.AbstractStatefulDestroyableWorker;

import static java.lang.String.format;
import static java.util.Objects.isNull;

import static diarsid.support.concurrency.threads.ThreadsUtil.shutdownAndWait;

public class BlockingSourceGroup<T>
        extends AbstractStatefulDestroyableWorker
        implements AsyncExchangePoint.BlockingSource.Group<T> {

    static class BlockingSourceHolder<T> {

        private final AtomicBoolean isWorking;
        private final AsyncExchangePointImpl<T> exchangePoint;
        private final AsyncExchangePoint.BlockingSource<T> source;
        private final ExecutorService async;
        private final Future<?> asyncAwaitAndTakeLoop;

        public BlockingSourceHolder(
                AsyncExchangePointImpl<T> exchangePoint,
                AsyncExchangePoint.BlockingSource<T> source) {
            this.isWorking = new AtomicBoolean(true);
            this.exchangePoint = exchangePoint;
            this.source = source;
            this.async = exchangePoint.namedThreadSource.newNamedFixedThreadPool(format("%s[%s]", AsyncExchangePoint.BlockingSource.class.getSimpleName(), source.name()), 1);
            this.asyncAwaitAndTakeLoop = this.async.submit(this::awaitAndTakeFromSourceToExchangePoint);
        }

        private void awaitAndTakeFromSourceToExchangePoint() {
            while ( this.isWorking.get() ) {
                try {
                    T t = this.source.awaitAndTake();
                    if ( ! this.isWorking.get() ) {
                        break;
                    }
                    this.exchangePoint.put(t);
                }
                catch (Throwable t) {
                    // TODO
                }
            }
        }

        void stop() {
            this.isWorking.set(false);
            this.asyncAwaitAndTakeLoop.cancel(true);
            shutdownAndWait(this.async);
        }
    }

    private final AtomicBoolean isOpen;
    private final AsyncExchangePointImpl<T> exchangePoint;
    private final Map<String, BlockingSourceHolder<T>> sourceHoldersByNames;

    public BlockingSourceGroup(AsyncExchangePointImpl<T> exchangePoint) {
        super(exchangePoint.name() + "." + AsyncExchangePoint.BlockingSource.Group.class.getSimpleName());
        this.isOpen = new AtomicBoolean(true);
        this.exchangePoint = exchangePoint;
        this.sourceHoldersByNames = new ConcurrentHashMap<>();
    }

    @Override
    protected boolean doSynchronizedStartWork() {
        return true;
    }

    @Override
    public boolean add(AsyncExchangePoint.BlockingSource<T> source) {
        return super.doSynchronizedReturnRead(() -> {
            if ( ! this.isOpen.get() ) {
                return false;
            }

            this.sourceHoldersByNames.computeIfAbsent(
                    source.name(),
                    (name) -> new BlockingSourceHolder<>(this.exchangePoint, source));
            return true;
        });
    }

    @Override
    public boolean remove(String sourceName) {
        BlockingSourceHolder<T> holder = super.doSynchronizedReturnRead(() -> {
            return this.sourceHoldersByNames.remove(sourceName);
        });

        if ( isNull(holder) ) {
            return false;
        }

        holder.stop();
        return true;
    }

    @Override
    protected boolean doSynchronizedDestroy() {
        this.isOpen.set(false);
        for ( var holder : this.sourceHoldersByNames.values() ) {
            holder.stop();
        }
        this.sourceHoldersByNames.clear();

        return true;
    }
}
