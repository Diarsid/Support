package diarsid.support.concurrency.threads;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class NamedThreadFactory implements ThreadFactory {

    private final ThreadsNaming threadsNaming;
    private final ThreadFactory threadsWrappedFactory;

    public NamedThreadFactory(ThreadsNaming threadsNaming) {
        this.threadsNaming = threadsNaming;
        this.threadsWrappedFactory = Executors.defaultThreadFactory();
    }

    public NamedThreadFactory(ThreadsNaming threadsNaming, ThreadFactory threadsWrappedFactory) {
        this.threadsNaming = threadsNaming;
        this.threadsWrappedFactory = threadsWrappedFactory;
    }

    @Override
    public Thread newThread(Runnable runnable) {
        Thread thread = threadsWrappedFactory.newThread(runnable);
        thread.setName(threadsNaming.nextName());
        return thread;
    }
}
