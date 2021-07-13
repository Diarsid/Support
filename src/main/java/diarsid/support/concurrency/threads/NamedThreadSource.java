package diarsid.support.concurrency.threads;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;

import static java.lang.Thread.currentThread;
import static java.util.concurrent.Executors.newCachedThreadPool;
import static java.util.concurrent.Executors.newFixedThreadPool;

import static diarsid.support.concurrency.threads.ThreadsUtil.shutdownAndWait;

public class NamedThreadSource {

    private final String namePrefix;
    private final ExecutorService asyncDefault;

    public NamedThreadSource(String namePrefix) {
        this.namePrefix = namePrefix;
        this.asyncDefault = newNamedCachedThreadPool(namePrefix + ".async.default");
    }

    public void closeThreads() {
        shutdownAndWait(asyncDefault);
    }

    public ExecutorService newNamedFixedThreadPool(String name, int size) {
        return newFixedThreadPool(size, namedIncrementThreadFactory(name));
    }

    public ExecutorService newNamedCachedThreadPool(String name) {
        return newCachedThreadPool(namedIncrementThreadFactory(name));
    }

    public ScheduledExecutorService newNamedScheduledExecutorService(String name, int size) {
        return new ScheduledThreadPoolExecutor(size, namedIncrementThreadFactory(name));
    }

    public NamedThreadFactory namedThreadFactory(String name) {
        return new IncrementNamedThreadFactory(namePrefix + "." + name + ".%s");
    }

    public void runNamedAsync(String name, Runnable action) {
        asyncDefault.execute(() -> {
            String originalName = currentThread().getName();
            String actionName = originalName + "." + name;
            currentThread().setName(actionName);
            try {
                action.run();
            } finally {
                currentThread().setName(originalName);
            }
        });
    }

    private ThreadFactory namedIncrementThreadFactory(String name) {
        return new IncrementNamedThreadFactory(namePrefix + "." + name + ".%s");
    }
}
