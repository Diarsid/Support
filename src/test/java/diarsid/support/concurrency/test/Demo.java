package diarsid.support.concurrency.test;

import static diarsid.support.concurrency.test.CurrentThread.async;
import static diarsid.support.concurrency.test.CurrentThread.awaitFor;
import static diarsid.support.concurrency.test.CurrentThread.awaitForAll;
import static diarsid.support.concurrency.test.CurrentThread.blocking;

public class Demo {


    public static void main(String[] args) {

//        blocking()
//                .sleep(3000)
//                .afterSleepNothing();
//        System.out.println(" 3 sec sleep");
//
//        async()
//                .sleep(3000)
//                .afterSleepDo(() -> {
//                    System.out.println("async 3 sec after sleep");
//                });

//        blocking()
//                .loopExact(10)
//                .eachTimeDo((i) -> System.out.println("loop: " + i));

//        blocking()
//                .sleep(3000)
//                .afterSleepDo(() -> {
//                    System.out.println("blocking 3 sec after sleep");
//                    async()
//                            .loopEndless()
//                            .eachTimeSleep(100)
//                            .eachTimeDo((i) -> {
//                                System.out.println("endless async loop: " + i);
//                            });
//                    System.out.println("DEBUG");
//                });


//        blocking()
//                .sleep(1000)
//                .afterSleepAgain()
//                .blocking()
//                .sleep(1000)
//                .afterSleepNothing();

        Steps steps = new Steps();
        steps.step(1);

        async("named")
                .action(() -> {
                    blocking()
                            .sleep(1000)
                            .afterSleepNothing();
                    steps.step(4);
                });

        steps.step(2);

        async()
                .action(() -> {
                    awaitFor("named");
                    steps.step(6);
                    blocking()
                            .sleep(1000)
                            .afterSleepNothing();
                    steps.step(5);
                });

        steps.step(3);

        awaitForAll();

        steps.step(7);

        steps.mustBeConsistent();


//        blocking()
//                .sleep(10_000)
//                .afterSleepDo(() -> System.out.println("end 10 sec"));
    }
}
