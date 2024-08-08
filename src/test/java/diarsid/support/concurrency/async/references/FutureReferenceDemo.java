package diarsid.support.concurrency.async.references;

import static diarsid.support.tests.concurrency.CurrentThread.async;
import static diarsid.support.tests.concurrency.CurrentThread.awaitForAll;
import static diarsid.support.tests.concurrency.CurrentThread.blocking;

public class FutureReferenceDemo {

    public static void main(String[] args) {
        FutureReference<String> ref = new ReadWriteFutureReference<>();

        async()
                .action(() -> {
                    System.out.println("async 1: " + ref.read().get());
                });

        async()
                .sleep(1000)
                .afterSleepDo(() -> {
                    System.out.println("setting...");
                    ref.write().set("aaa");
                    System.out.println("set!");
                });

        async()
                .action(() -> {
                    System.out.println("async 2: " + ref.read().get());
                });

        blocking()
                .sleep(1100)
                .afterSleepNothing();

        async()
                .action(() -> {
                    System.out.println("async 3: " + ref.read().get());
                });

        awaitForAll();
    }
}
