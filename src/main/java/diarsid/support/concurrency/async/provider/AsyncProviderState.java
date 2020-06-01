/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package diarsid.support.concurrency.async.provider;

import static java.util.Objects.isNull;

/**
 *
 * @author Diarsid
 */
public enum AsyncProviderState {
    
    PROVIDING_RUNNING,
    PROVIDING_COMPLETED(AwaitedResult.AWAITING_COMPLETED),
    PROVIDING_FAILED(AwaitedResult.AWAITING_FAILED),
    PROVIDING_STOPPED(AwaitedResult.AWAITING_STOPPED);
    
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
