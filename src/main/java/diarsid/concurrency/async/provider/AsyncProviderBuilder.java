/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package diarsid.concurrency.async.provider;

import java.util.function.Consumer;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

import static diarsid.concurrency.async.provider.RepeatedWaiting.UNLIMITED_ATTEMPTS;

/**
 *
 * @author Diarsid
 */
public class AsyncProviderBuilder<T> {
    
    private final static int UNINITIALIZED = -3;
    
    private final Supplier<T> tSupplier;
    private int millisToWaitAfterFail;
    private int maxAttempts;
    private Consumer<Exception> callbackOnSupplierFail;
    private Consumer<Throwable> callbackOnUnexpectedFail;
    private Runnable callbackOnAttemptsExhausted;
    private Runnable callbackOnStopped;

    AsyncProviderBuilder(Supplier<T> tSupplier) {
        requireNonNull(tSupplier);
        this.tSupplier = tSupplier;
        this.millisToWaitAfterFail = UNINITIALIZED;
        this.maxAttempts = UNINITIALIZED;
    }
    
    public AsyncProviderBuilder<T> withMillisToWaitAfterFail(int millis) {
        this.millisToWaitAfterFail = millis;
        return this;
    }
    
    public AsyncProviderBuilder<T> withMaxAttempts(int attempts) {
        this.maxAttempts = attempts;
        return this;
    }
    
    public AsyncProviderBuilder<T> withUnlimitedAttempts() {
        this.maxAttempts = UNLIMITED_ATTEMPTS;
        return this;
    }
    
    public AsyncProviderBuilder<T> withCallbackOnSupplierFail(Consumer<Exception> callback) {
        this.callbackOnSupplierFail = callback;
        return this;
    }    
    
    public AsyncProviderBuilder<T> withCallbackOnUnexpectedFail(Consumer<Throwable> callback) {
        this.callbackOnUnexpectedFail = callback;
        return this;
    }    
    
    public AsyncProviderBuilder<T> withCallbackOnAttemptsExhausted(Runnable callback) {
        this.callbackOnAttemptsExhausted = callback;
        return this;
    }      
    
    public AsyncProviderBuilder<T> withCallbackOnStopped(Runnable callback) {
        this.callbackOnStopped = callback;
        return this;
    }    
    
    public AsyncProvider<T> begin() {
        this.checkState();
        RepeatedWaiting waiting = new RepeatedWaiting(this.millisToWaitAfterFail, this.maxAttempts);
        AsyncProviderImpl<T> provider = new AsyncProviderImpl<>(
                this.tSupplier, 
                waiting, 
                this.callbackOnSupplierFail,
                this.callbackOnUnexpectedFail,
                this.callbackOnAttemptsExhausted,
                this.callbackOnStopped);
        provider.asyncStart();
        return provider;
    }
    
    private void checkState() {
        if ( this.maxAttempts == UNINITIALIZED ) {
            throw new IllegalStateException();
        }
        if ( this.millisToWaitAfterFail == UNINITIALIZED ) {
            throw new IllegalStateException();
        }
    }
}
