package diarsid.support.concurrency;

import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.util.concurrent.CompletableFuture.runAsync;

public abstract class ThreadSleepInLoop {

    public abstract void begin();

    public abstract void stop();

    public static class Blocking extends ThreadSleepInLoop {

        private final long sleep;
        private final Runnable runnable;
        private final AtomicBoolean isRunning;

        public Blocking(long sleep, Runnable runnable, AtomicBoolean isRunning) {
            this.sleep = sleep;
            this.runnable = runnable;
            this.isRunning = isRunning;
        }

        public Blocking(long sleep, Runnable runnable) {
            this(sleep, runnable, new AtomicBoolean(true));
        }

        @Override
        public void begin() {
            this.run();
        }

        @Override
        public void stop() {
            this.isRunning.set(false);
        }

        private void run() {
            while ( this.isRunning.get() ) {
                try {
                    Thread.sleep(sleep);
                    this.runnable.run();
                }
                catch (InterruptedException e) {
                    // ignore
                }
            }
        }
    }

    public static class Async extends ThreadSleepInLoop {

        private final long sleep;
        private final Runnable runnable;
        private final AtomicBoolean isRunning;
        private Future<?> future;

        public Async(long sleep, Runnable runnable, AtomicBoolean isRunning) {
            this.sleep = sleep;
            this.runnable = runnable;
            this.isRunning = isRunning;
        }

        public Async(long sleep, Runnable runnable) {
            this(sleep, runnable, new AtomicBoolean(true));
        }

        @Override
        public void begin() {
            this.future = runAsync(this::run);
        }

        @Override
        public void stop() {
            this.isRunning.set(false);
            if ( this.future != null ) {
                this.future.cancel(true);
                this.future = null;
            }
        }


        private void run() {
            while ( this.isRunning.get() ) {
                try {
                    Thread.sleep(sleep);
                    this.runnable.run();
                }
                catch (InterruptedException e) {
                    // ignore
                }
            }
        }
    }

}
