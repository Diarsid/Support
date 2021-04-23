package diarsid.support.concurrency.threads;

import java.util.concurrent.ExecutorService;

import static java.util.concurrent.TimeUnit.SECONDS;

public class ThreadsUtil {

    private ThreadsUtil() {}

    public static void shutdownAndWait(ExecutorService threads) {
        threads.shutdown();

        do {
            try {
                threads.awaitTermination(10, SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while (! threads.isTerminated() );
    }

    public static void sleepSafely(int ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) {}
    }
}
