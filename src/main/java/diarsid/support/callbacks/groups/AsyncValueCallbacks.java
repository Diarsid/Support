package diarsid.support.callbacks.groups;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;

import diarsid.support.callbacks.ValueCallback;

public class AsyncValueCallbacks<T> extends ValueCallbacks<T> {

    private final Executor async;

    public AsyncValueCallbacks(Executor executor) {
        super();
        this.async = executor;
    }

    @Override
    public void call(T t) {
        List<ValueCallback<T>> copy = super.createCallbacksListCopySynchronously();

        synchronized ( super.callLock ) {
            for ( ValueCallback<T> callback : copy ) {
                this.async.execute(() -> {
                    try {
                        callback.call(t);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                    catch (Throwable th) {
                        th.printStackTrace();
                    }
                });
            }
        }
    }

    public void callAndAwait(T t) {
        List<ValueCallback<T>> copy = super.createCallbacksListCopySynchronously();

        synchronized ( super.callLock ) {
            CountDownLatch block = new CountDownLatch(copy.size());

            for ( ValueCallback<T> callback : copy ) {
                this.async.execute(() -> {
                    try {
                        callback.call(t);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                    catch (Throwable th) {
                        th.printStackTrace();
                    }
                    block.countDown();
                });
            }

            try {
                block.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
