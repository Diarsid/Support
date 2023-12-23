package diarsid.support.tests.concurrency;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BooleanSupplier;
import java.util.function.IntSupplier;

public class SleepData implements
        CurrentThread.Synced,
        CurrentThread.Synced.Async,
        CurrentThread.Synced.Async.AfterAction,
        CurrentThread.Synced.Blocking,
        CurrentThread.Synced.AboutToSleep,
        CurrentThread.Synced.AboutToSleep.AfterSleepDo,
        CurrentThread.Synced.Loop,
        CurrentThread.Synced.Loop.EachTimeDoing,
        CurrentThread.Synced.Loop.EachTimeSleeping {

    static final boolean ASYNC = true;
    static final boolean BLOCKING = false;

    String name;
    IntSupplier millisToSleepInitial;
    boolean isAsync;
    boolean isLoop;
    IntSupplier millisToSleepInLoop;
    long loopsCount = -1;
    BooleanSupplier isLoopingAgain;
    CurrentThread.UnsafeCountConsumer unsafeCountConsumer;
    UnsafeCountToSleepMillis countToSleepMillis;

    public SleepData(boolean isAsync) {
        this.isAsync = isAsync;
    }

    public SleepData(String name) {
        this.isAsync = true;
        this.name = name;
    }

    @Override
    public CurrentThread.Synced blocking() {
        return new SleepData(BLOCKING);
    }

    @Override
    public CurrentThread.Synced async() {
        return new SleepData(ASYNC);
    }

    @Override
    public AboutToSleep sleep(int millis) {
        this.millisToSleepInitial = () -> millis;
        return this.orNewIfSubclassOfEnd(AboutToSleep.class);
    }

    @Override
    public AboutToSleep sleep(AtomicInteger millis) {
        this.millisToSleepInitial = () -> millis.get();
        return this.orNewIfSubclassOfEnd(AboutToSleep.class);
    }

    @Override
    public AboutToSleep sleep(IntSupplier millis) {
        this.millisToSleepInitial = () -> millis.getAsInt();
        return this.orNewIfSubclassOfEnd(AboutToSleep.class);
    }

    @Override
    public Loop loopEndless() {
        this.isLoop = true;
        this.isLoopingAgain = () -> true;
        return this.orNewIfSubclassOfEnd(Loop.class);
    }

    @Override
    public Loop loopWhile(AtomicBoolean bool) {
        this.isLoop = true;
        this.isLoopingAgain = bool::get;
        return this.orNewIfSubclassOfEnd(Loop.class);
    }

    @Override
    public Loop loopWhile(BooleanSupplier b) {
        this.isLoop = true;
        this.isLoopingAgain = b;
        return this.orNewIfSubclassOfEnd(Loop.class);
    }

    @Override
    public Loop loopExact(long count) {
        this.isLoop = true;
        this.loopsCount = count;
        return this.orNewIfSubclassOfEnd(Loop.class);
    }

    @Override
    public AfterSleepDo afterSleepDo(CurrentThread.UnsafeRunnable runnable) {
        this.isLoop = false;
        this.unsafeCountConsumer = (i) -> {
            runnable.run();
        };
        new Sleep(this);
        return this.orNewIfSubclassOfEnd(AfterSleepDo.class);
    }

    @Override
    public AfterSleepDo afterSleepAgain() {
        new Sleep(this);
        return this.orNewIfSubclassOfEnd(AfterSleepDo.class);
    }

    @Override
    public CurrentThread.End afterSleepNothing() {
        new Sleep(this);
        return this.orNewIfSubclassOfEnd(CurrentThread.End.class);
    }

    @Override
    public EachTimeSleeping eachTimeSleep(int millis) {
        this.countToSleepMillis = (i) -> millis;
        return this.orNewIfSubclassOfEnd(EachTimeSleeping.class);
    }

    @Override
    public EachTimeSleeping eachTimeSleep(UnsafeCountToSleepMillis countToSleepMillis) {
        this.countToSleepMillis = countToSleepMillis;
        return this.orNewIfSubclassOfEnd(EachTimeSleeping.class);
    }

    @Override
    public EachTimeDoing eachTimeDo(CurrentThread.UnsafeRunnable runnable) {
        this.unsafeCountConsumer = (i) -> {
            runnable.run();
        };
        new Sleep(this);
        return this.orNewIfSubclassOfEnd(EachTimeDoing.class);
    }

    @Override
    public EachTimeDoing eachTimeDo(CurrentThread.UnsafeCountConsumer countConsumer) {
        this.unsafeCountConsumer = countConsumer;
        new Sleep(this);
        return this.orNewIfSubclassOfEnd(EachTimeDoing.class);
    }

    @SuppressWarnings("unchecked")
    private <T> T orNewIfSubclassOfEnd(Class<T> type) {
        if ( CurrentThread.End.class.isAssignableFrom(type) ) {
            if ( CurrentThread.End.class.equals(type) ) {
                return (T) this;
            }
            else {
                // T extends End, but contains methods for further usage on new instance.
                return (T) new SleepData(this.isAsync);
            }
        }
        else {
            return (T) this;
        }
    }

    @Override
    public AfterAction action(CurrentThread.UnsafeRunnable runnable) {
        this.unsafeCountConsumer = (i) -> {
            runnable.run();
        };
        new Sleep(this);
        return this.orNewIfSubclassOfEnd(AfterAction.class);
    }
}
