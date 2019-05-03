/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package diarsid.concurrency.async.provider;

/**
 *
 * @author Diarsid
 */
public class RepeatedWaiting {
    
    static final int UNLIMITED_ATTEMPTS = -1;
    
    private final int millisToWaitAfterFail;
    private final int maxAttempts;
    private int remainingAttempts;

    RepeatedWaiting(int millisToWaitAfterFail, int maxTimesToWaitAfterFail) {
        this.millisToWaitAfterFail = millisToWaitAfterFail;
        this.maxAttempts = maxTimesToWaitAfterFail;
        this.remainingAttempts = maxTimesToWaitAfterFail;
    }

    RepeatedWaiting(int millisToWaitAfterFail) {
        this.millisToWaitAfterFail = millisToWaitAfterFail;
        this.maxAttempts = UNLIMITED_ATTEMPTS;
    }

    boolean doesHaveAttempts() {
        if ( this.maxAttempts == UNLIMITED_ATTEMPTS ) {
            return true;
        } else {
            this.remainingAttempts--;
            return this.remainingAttempts > 0;
        }
    }
    
    int millis() {
        return this.millisToWaitAfterFail;
    }
    
}
