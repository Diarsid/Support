package diarsid.support.concurrency.signals;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import diarsid.support.concurrency.ThreadSleepInLoop;
import diarsid.support.concurrency.signals.impl.SignalsRuntimeImpl;

import static java.util.concurrent.CompletableFuture.runAsync;

import static diarsid.support.concurrency.threads.ThreadsUtil.sleepSafely;

public class SignalTest {

    private static final Logger log = LoggerFactory.getLogger(SignalTest.class);

//    @Test
//    public void commonTest() {
//        Signals signals = new SignalsRuntimeImpl();
//
//        Signal signal = signals.signal("Event");
//
//        Signal.SimpleListenerOfData listenerA = new Signal.builder().withReaction(
//                () -> log.info("listener A: ")).build();
//
////        var listenerB = new Signal.SimpleListenerOfData(
////                randomUUID(),
////                () -> log.info("listener B: "));
////
////        var listenerC = new Signal.SimpleListenerOfData(
////                randomUUID(),
////                () -> {
////                    log.info("listener C: ");
////                    sleepSafely(20_000);
////                });
////
////        var listenerNext = new Signal.SimpleListenerOfData(
////                randomUUID(),
////                () -> log.info("listener Next: "));
//
//        signal.listen(listenerA);
//        signal.listen(listenerB);
//        signal.listen(listenerC);
//
//        AtomicBoolean working = new AtomicBoolean(true);
//
//        runAsync(() -> {
//            new SleepLoop(1000, () -> signal.emit(), working);
//        });
//
//        runAsync(() -> {
//            sleepSafely(4_000);
//            signals.signal("Next").listen(listenerNext);
//            signals.signal("Next").emit();
////            sleepSafely(1);
//            signals.signal("Next").close();
//            signals.signal("Event").close();
//            sleepSafely(3_000);
//            log.info("after sleep");
//            working.set(false);
////            events.close();
////            events.event("Event").stop();
//            log.info("closed");
//        });
//    }

    public static void main(String[] args) {
        Signals signals = new SignalsRuntimeImpl();

        Signal signal = signals.signal("Event");

        var listenerA = new SignalSimpleListener(
                () -> log.info("listener A: "));

        var listenerB = new SignalSimpleListener(
                (data) -> log.info("listener B: " + data));

        var listenerC = new SignalSimpleListener(
                () -> {
                    log.info("listener C: ");
                });

        signal.listen(listenerA);
        signal.listen(listenerB);
        signal.listen(listenerC);

        AtomicBoolean working = new AtomicBoolean(true);

        AtomicInteger counter = new AtomicInteger(0);
        var asyncSignalEmitting = new ThreadSleepInLoop.Async(
                1000,
                () -> signal.emitWith(counter.incrementAndGet()),
                working);
        asyncSignalEmitting.begin();

        runAsync(() -> {
            sleepSafely(5_000);
            signals.signal("Event").close();
            log.info("after sleep");
            working.set(false);
//            events.close();
//            events.event("Event").stop();
            log.info("closed");
        });
    }

}
