package diarsid.support.concurrency.async.provider;

import java.util.function.Supplier;

public interface AwaitAndGet<T> {
    
    public static <T> AsyncProviderBuilder<T> from(Supplier<T> tSupplier) {
        return new AsyncProviderBuilder<>(tSupplier);
    }

    Awaited<T> awaitAndGet();

    Awaited<T> stop();
    
    AsyncProviderState state();
    
}
