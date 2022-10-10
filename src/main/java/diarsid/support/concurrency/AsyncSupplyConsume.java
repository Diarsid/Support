package diarsid.support.concurrency;

import java.util.function.Consumer;

import diarsid.support.concurrency.stateful.workers.AbstractStatefulPausableWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AsyncSupplyConsume<T> extends AbstractStatefulPausableWorker {

    private static final Logger log = LoggerFactory.getLogger(AsyncSupplyConsume.class);

    public interface InterruptableSupplier<T> {

        T get() throws InterruptedException;
    }

    private final InterruptableSupplier<T> waiting;
    private final Consumer<T> notified;
    private final Object monitor;
    private final Runnable waitLogic;
    private final Runnable notifyLogic;
    private long starts;
    private T t;
    private Thread waitingThread;
    private Thread notifiedThread;

    public AsyncSupplyConsume(
            InterruptableSupplier<T> waiting,
            Consumer<T> notified,
            boolean synchronizedConsumer) {
        this(null, waiting, notified, synchronizedConsumer);
    }

    public AsyncSupplyConsume(
            String name,
            InterruptableSupplier<T> waiting,
            Consumer<T> notified,
            boolean synchronizedConsumer) {
        super(name);
        this.waiting = waiting;
        this.notified = notified;
        this.monitor = new Object();
        this.starts = 0;

        this.waitLogic = () -> {
            T current;
            log.info("starting...");
            while ( this.isWorkingOrTransitingToWorking() ) {
                try {
                    current = this.waiting.get();

                    if ( this.isPausedOrTransitingToPaused() ) {
                        break;
                    }

                    synchronized ( this.monitor ) {
                        this.t = current;
                        this.monitor.notify();
                    }
                }
                catch (InterruptedException e) {
                    if ( this.isPausedOrTransitingToPaused() ) {
                        log.info("stopping by interruption");
                    }
                    else {
                        log.error("unexpected interruption: ", e);
                    }
                }
                catch (Exception e) {
                    log.error("unexpected exception on get(): ", e);
                }
            }
            log.info("stopped");
        };

        this.notifyLogic = () -> {
            T current;
            log.info("starting...");
            while ( this.isWorkingOrTransitingToWorking() ) {
                try {
                    synchronized ( this.monitor ) {
                        this.monitor.wait();

                        if ( this.isPausedOrTransitingToPaused() ) {
                            break;
                        }

                        current = this.t;
                        if ( synchronizedConsumer ) {
                            this.notified.accept(current);
                        }
                    }
                    if ( ! synchronizedConsumer ) {
                        this.notified.accept(current);
                    }
                }
                catch (InterruptedException e) {
                    log.error("unexpected interruption: ", e);
                }
                catch (Exception e) {
                    log.error("unexpected exception on accept(T): ", e);
                }
            }
            log.info("stopped");
        };
    }

    @Override
    protected boolean doSynchronizedStartWork() {
        this.starts++;

        this.waitingThread = new Thread(this.waitLogic);
        this.notifiedThread = new Thread(this.notifyLogic);

        this.waitingThread.setName(
                AsyncSupplyConsume.class.getSimpleName() +
                "." + super.name() +
                "." + "waiting" +
                "." + this.starts);

        this.notifiedThread.setName(
                AsyncSupplyConsume.class.getSimpleName() +
                "." + super.name() +
                "." + "notified" +
                "." + this.starts);

        this.notifiedThread.start();
        this.waitingThread.start();

        return true;
    }

    @Override
    protected boolean doSynchronizedPauseWork() {
        synchronized ( this.monitor ) {
            this.monitor.notify();
        }

        try {
            boolean stopped = false;
            for ( int i = 0; i < 3; i ++ ) {
                this.waitingThread.join(5);
                if ( ! this.waitingThread.isAlive() ) {
                    stopped = true;
                    break;
                }
            }

            if ( ! stopped ) {
                this.waitingThread.interrupt();
            }
        }
        catch (InterruptedException e) {
            System.out.println("e 3");
            return false;
        }

        return true;
    }
}
