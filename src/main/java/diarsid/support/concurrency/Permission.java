package diarsid.support.concurrency;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Permission {

    private static final Logger log = LoggerFactory.getLogger(Permission.class);

    private final Lock lock;
    private final Condition condition;
    private volatile boolean forbidden;

    public Permission() {
        this.lock = new ReentrantLock(true);
        this.condition = this.lock.newCondition();
    }

    public void awaitIfForbidden() {
        this.lock.lock();
        try {
            if ( this.forbidden ) {
                this.condition.await();
            }
        }
        catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }
        finally {
            this.lock.unlock();
        }
    }

    public void forbid() {
        this.lock.lock();
        try {
            this.forbidden = true;
        }
        finally {
            this.lock.unlock();
        }
    }

    public void allow() {
        this.lock.lock();
        try {
            this.forbidden = false;
            this.condition.signalAll();
        }
        finally {
            this.lock.unlock();
        }
    }
}
