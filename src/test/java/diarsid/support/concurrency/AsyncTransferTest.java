package diarsid.support.concurrency;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.junit.Test;

import static java.lang.Thread.sleep;
import static java.util.concurrent.CompletableFuture.runAsync;

public class AsyncTransferTest {

    @Test
    public void test() throws Exception {
        BlockingQueue<String> queue = new ArrayBlockingQueue<>(10);

        AsyncTransfer<String> p = new AsyncTransfer<>(
                "test",
                () -> {
                    var s = queue.take();
                    System.out.println("taken");
                    return s;
                },
                (s) -> {
                    System.out.println("taken: " + s);
                },
                false
        );

        p.startWork();

        runAsync(() -> {
            for (int i = 0; i < 4; i++) {
                try {
                    sleep(1000);
                    queue.put("string " + i);
                    System.out.println("put!");
                } catch (InterruptedException e) {
                    System.out.println("!!!");
                }
            }
        });

        sleep(7_000);

        System.out.println("pausing");
        p.pauseWork();
        sleep(3000);
    }
}
