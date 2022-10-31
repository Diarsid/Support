package diarsid.support.concurrency.threads;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;

import static java.lang.Thread.currentThread;
import static java.util.Collections.synchronizedList;
import static java.util.concurrent.Executors.newCachedThreadPool;
import static java.util.concurrent.Executors.newFixedThreadPool;

import static diarsid.support.concurrency.threads.ThreadsUtil.shutdownAndWait;

public class NamedThreadSource {

    private final String namePrefix;
    private final ExecutorService asyncDefault;
    private final List<ExecutorService> executors;

    public NamedThreadSource(String namePrefix) {
        this.namePrefix = namePrefix;
        this.executors = synchronizedList(new ArrayList<>());
        this.asyncDefault = newNamedCachedThreadPool(namePrefix + ".async.default");
    }

    public void close() {
        shutdownAndWait(asyncDefault);
        executors.forEach(ThreadsUtil::shutdownAndWait);
    }

    public ExecutorService newNamedFixedThreadPool(String name, int size) {
        ExecutorService executor = newFixedThreadPool(size, namedIncrementThreadFactory(name));
        this.executors.add(executor);
        return executor;
    }

    public ExecutorService newNamedCachedThreadPool(String name) {
        ExecutorService executor = newCachedThreadPool(namedIncrementThreadFactory(name));
        this.executors.add(executor);
        return executor;
    }

    public ScheduledExecutorService newNamedScheduledExecutorService(String name, int size) {
        ScheduledExecutorService executor = new ScheduledThreadPoolExecutor(size, namedIncrementThreadFactory(name));
        this.executors.add(executor);
        return executor;
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
