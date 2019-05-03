/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package diarsid.concurrency.async.provider;

import static java.util.Objects.isNull;

import static diarsid.concurrency.async.provider.AwaitedResult.AWAITING_COMPLETED;
import static diarsid.concurrency.async.provider.AwaitedResult.AWAITING_FAILED;
import static diarsid.concurrency.async.provider.AwaitedResult.AWAITING_STOPPED;

/**
 *
 * @author Diarsid
 */
public enum AsyncProviderState {
    
    PROVIDING_RUNNING,
    PROVIDING_COMPLETED(AWAITING_COMPLETED),
    PROVIDING_FAILED(AWAITING_FAILED),
    PROVIDING_STOPPED(AWAITING_STOPPED);
    
    private final AwaitedResult awaitedResult;

    private AsyncProviderState() {
        this.awaitedResult = null;
    }

    private AsyncProviderState(AwaitedResult awaitedResult) {
        this.awaitedResult = awaitedResult;
    }

    public AwaitedResult awaitedResult() {
        if ( isNull(this.awaitedResult) ) {
            throw new IllegalStateException();
        }
        return this.awaitedResult;
    }
    
    
}
