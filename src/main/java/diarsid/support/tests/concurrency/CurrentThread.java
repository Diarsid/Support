package diarsid.support.tests.concurrency;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BooleanSupplier;
import java.util.function.IntSupplier;

import static diarsid.support.tests.concurrency.SleepData.ASYNC;
import static diarsid.support.tests.concurrency.SleepData.BLOCKING;

public interface CurrentThread {

    interface UnsafeRunnable {

        void run() throws Exception;
    }

    interface UnsafeCountConsumer {

        void consume(long sleepCount) throws Exception;
    }

    static Synced.Blocking blocking() {
        return new SleepData(BLOCKING);
    }

    static Synced.Async async() {
        return new SleepData(ASYNC);
    }

    static Synced.Async async(String name) {
        return new SleepData(name);
    }

    static void awaitFor(String nameOfAsync) {
        Sleep.awaitFor(nameOfAsync);
    }

    static void awaitForAll() {
        Sleep.awaitForAll();
    }

    static void cancel(String name) {

    }

    static void cancelAll() {

    }

    interface Synced {

        interface Async extends Synced {

            AfterAction action(UnsafeRunnable runnable);

            interface AfterAction extends End {

                Synced blocking();

                Synced async();
            }
        }

        interface Blocking extends Synced {

        }

        AboutToSleep sleep(int millis);

        AboutToSleep sleep(AtomicInteger millis);

        AboutToSleep sleep(IntSupplier millis);

        Loop loopEndless();

        Loop loopWhile(AtomicBoolean b);

        Loop loopWhile(BooleanSupplier b);

        Loop loopExact(long count);

        interface AboutToSleep {

            AfterSleepDo afterSleepDo(UnsafeRunnable runnable);

            AfterSleepDo afterSleepAgain();

            End afterSleepNothing();

            interface AfterSleepDo extends End {

                Synced blocking();

                Synced async();
            }
        }

        interface Loop {

            interface UnsafeCountToSleepMillis {

                int takeCountAndGetMillis(long count) throws Exception;
            }

            interface EachTimeDoing extends End {

            }

            interface EachTimeSleeping {

                EachTimeDoing eachTimeDo(UnsafeRunnable runnable);

                EachTimeDoing eachTimeDo(UnsafeCountConsumer countConsumer);
            }

            EachTimeSleeping eachTimeSleep(int millis);

            EachTimeSleeping eachTimeSleep(UnsafeCountToSleepMillis countToSleepMillis);

            EachTimeDoing eachTimeDo(UnsafeRunnable runnable);

            EachTimeDoing eachTimeDo(UnsafeCountConsumer countConsumer);
        }
    }

    interface End {

    }

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


        System.out.println("order: " + 1);
        async("named")
                .action(() -> {
                    blocking()
                            .sleep(1000)
                            .afterSleepNothing();
                    System.out.println("order: " + 4);
                });

        System.out.println("order: " + 2);

        async()
                .action(() -> {
                    awaitFor("named");
                    System.out.println("order: " + 5);
                    blocking()
                            .sleep(1000)
                            .afterSleepNothing();
                    System.out.println("order: " + 6);
                });
        System.out.println("order: " + 3);

        awaitForAll();
        System.out.println("order: " + 7);


//        blocking()
//                .sleep(10_000)
//                .afterSleepDo(() -> System.out.println("end 10 sec"));
    }
}
