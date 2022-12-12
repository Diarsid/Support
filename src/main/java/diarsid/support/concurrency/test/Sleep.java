package diarsid.support.concurrency.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.function.BooleanSupplier;
import java.util.function.IntSupplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.concurrent.CompletableFuture.runAsync;

class Sleep {

    private static final Logger log = LoggerFactory.getLogger(Sleep.class);

    private static final Map<String, CompletableFuture<?>> ASYNC_BY_NAMES = new HashMap<>();

    private long count = 0;

    private final String name;
    private final IntSupplier millisToSleepInitial;
    private final boolean isAsync;
    private final boolean isLoop;
    private final IntSupplier millisToSleepInLoop;
    private final long loopsCount;
    private final BooleanSupplier isLoopingAgain;
    private final CurrentThread.UnsafeCountConsumer unsafeCountConsumer;
    private final CurrentThread.Synced.Loop.UnsafeCountToSleepMillis countToSleepMillis;
    private final BooleanSupplier isLoopAgain;

    Sleep(SleepData sleepData) {
        this.name = sleepData.name != null ? sleepData.name : UUID.randomUUID().toString();
        this.millisToSleepInitial = sleepData.millisToSleepInitial;
        this.isAsync = sleepData.isAsync;
        this.isLoop = sleepData.isLoop;
        this.millisToSleepInLoop = sleepData.millisToSleepInLoop;
        this.loopsCount = sleepData.loopsCount;
        this.isLoopingAgain = sleepData.isLoopingAgain;
        this.unsafeCountConsumer = sleepData.unsafeCountConsumer;
        this.countToSleepMillis = sleepData.countToSleepMillis;

        if ( sleepData.loopsCount > -1 ) {
            this.isLoopAgain = () -> {
                return this.count < sleepData.loopsCount;
            };
        }
        else {
            this.isLoopAgain = sleepData.isLoopingAgain;
        }

        if ( this.isAsync ) {
            CompletableFuture<?> async = runAsync(() -> {
                logic();
            });
            ASYNC_BY_NAMES.put(this.name, async);
        }
        else {
            logic();
        }
    }

    static void awaitFor(String name) {
        ASYNC_BY_NAMES
                .getOrDefault(name, CompletableFuture.completedFuture(null))
                .join();
    }

    static void awaitForAll() {
        List<CompletableFuture<?>> all = new ArrayList<>(ASYNC_BY_NAMES.values());
        CompletableFuture<?>[] allArray = all.toArray(new CompletableFuture<?>[0]);
        CompletableFuture
                .allOf(allArray)
                .join();
    }

    private void logic() {
        if ( this.isLoop ) {
            int initialSleepMillis = 0;
            if ( this.millisToSleepInitial != null ) {
                initialSleepMillis = this.millisToSleepInitial.getAsInt();

                if ( initialSleepMillis != 0 ) {
                    try {
                        Thread.sleep(initialSleepMillis);
                    }
                    catch (InterruptedException e) {
                        log.error(e.getMessage(), e);
                    }
                }
            }

            int loopSleepMillis = initialSleepMillis;
            while ( this.isLoopAgain.getAsBoolean() ) {

                if ( this.countToSleepMillis != null ) {
                    try {
                        loopSleepMillis = this.countToSleepMillis.takeCountAndGetMillis(count);
                    }
                    catch (Exception e) {
                        loopSleepMillis = initialSleepMillis;
                    }
                }
                else if ( this.millisToSleepInLoop != null ){
                    loopSleepMillis = this.millisToSleepInLoop.getAsInt();
                }

                try {
                    Thread.sleep(loopSleepMillis);
                }
                catch (InterruptedException e) {
                    log.error(e.getMessage(), e);
                }

                if ( this.unsafeCountConsumer != null ) {
                    try {
                        this.unsafeCountConsumer.consume(count);
                    }
                    catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                }

                count++;
            }
        }
        else {
            try {
                if ( this.millisToSleepInitial != null ) {
                    Thread.sleep(this.millisToSleepInitial.getAsInt());
                }

                if ( this.unsafeCountConsumer != null ) {
                    this.unsafeCountConsumer.consume(count);
                }
            }
            catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }
}
