/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package diarsid.support.objects.references.impl;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import static diarsid.support.objects.references.Reference.ValuePresence.POSSIBLE;

/**
 *
 * @author Diarsid
 */
class RealPossible<T> implements Possible<T> {
    
    protected T t;

    protected RealPossible() {
        this.t = null;
    }
    
    RealPossible(T t) {
        this.t = t;
    }
    
    protected T internalSet(T newT) {
        T oldT = this.t;
        this.t = newT;
        return oldT;
    }
    
    @Override
    public RealPossible<T> orDefault(T defaultT) {
        if ( isNull(this.t) ) {
            this.internalSet(defaultT);
        }
        return this;
    }
    
    private void checkValueNotNull() {
        if ( isNull(this.t) ) {
            throw new NullPointerException();
        }
    }
    
    @Override
    public boolean match(Predicate<T> predicate) {
        return this.isPresent() && predicate.test(this.t);
    }
    
    @Override
    public boolean notMatch(Predicate<T> predicate) {
        return this.isNotPresent() || ! predicate.test(this.t);
    }
    
    @Override
    public boolean isPresent() {
        return nonNull(this.t);
    }
    
    @Override
    public boolean isNotPresent() {
        return isNull(this.t);
    }
    
    @Override
    public void ifPresent(Consumer<T> consumer) {
        if ( this.isPresent() ) {
            consumer.accept(this.t);
        }
    }
    
    @Override
    public void ifNotPresent(Runnable runnable) {
        if ( this.isNotPresent() ) {
            runnable.run();
        }
    }
    
    @Override
    public <R> RealPossible<R> map(Function<T, R> mapper) {
        if ( this.isPresent() ) {
            return new RealPossible<>(mapper.apply(this.t));
        } else {
            return new RealPossible<>();
        }        
    }
    
    @Override
    public T orThrow() {
        this.checkValueNotNull();
        return this.t;
    }
    
    @Override
    public T extractOrThrow() {
        this.checkValueNotNull();
        return this.nullify();
    }
    
    @Override
    public T orThrow(Supplier<? extends RuntimeException> exceptionCreator) {
        if ( isNull(this.t) ) {
            throw exceptionCreator.get();
        } else {
            return this.t;
        }
    }
    
    @Override
    public T or(T otherT) {
        return isNull(this.t) ? otherT : t;
    }

    @Override
    public T orOther(Possible<T> otherPossibleT) {
        return isNull(this.t) ? otherPossibleT.orThrow() : t;
    }
    
    @Override
    public Optional<T> optional() {
        return Optional.ofNullable(this.t);
    }
    
    @Override
    public T resetTo(T newT) {
        if ( this.isPresent() && this.equalsTo(newT) ) {
            return this.t;
        }

        if ( this.isNotPresent() && isNull(newT) ) {
            return this.t;
        }

        T oldT = this.t;
        this.internalSet(newT);
        return oldT;
    }

    @Override
    public T modify(Function<T, T> oldToNew) {
        T oldT = this.t;
        T newT = oldToNew.apply(oldT);

        if ( this.isPresent() && this.equalsTo(newT) ) {
            return this.t;
        }

        if ( this.isNotPresent() && isNull(newT) ) {
            return this.t;
        }

        this.internalSet(newT);
        return oldT;
    }

    @Override
    public ValuePresence valuePresence() {
        return POSSIBLE;
    }

    @Override
    public T resetTo(Optional<T> optionalT) {
        return this.resetTo(optionalT.orElse(null));
    }
    
    @Override
    public T resetTo(Possible<T> possibleT) {
        return this.resetTo(possibleT.or(null));
    }
    
    @Override
    public T ifPresentResetTo(T newT) {
        if ( this.isPresent() ) {
            return this.resetTo(newT);
        } else {
            return this.t;
        }
    }
    
    @Override
    public T ifNotPresentResetTo(T newT) {
        if ( this.isNotPresent() ) {
            return this.resetTo(newT);
        } else {
            return this.t;
        }
    }
    
    @Override
    public T nullify() {
        T oldT = this.t;
        this.internalSet(null);
        return oldT;
    }    
    
    @Override
    public boolean equals(Object o) {
        return 
                nonNull(this.t) && 
                nonNull(o) && 
                o instanceof RealPossible && 
                this.t.equals(((RealPossible) o).t);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 37 * hash + Objects.hashCode(this.t);
        return hash;
    }
    
    @Override
    public boolean notEquals(Possible<T> possibleT) {
        return ! this.equals(possibleT);
    }
    
    @Override
    public boolean equalsTo(T otherT) {
        return nonNull(this.t) && this.t.equals(otherT);
    }
    
    @Override
    public boolean notEqualsTo(T otherT) {
        return nonNull(this.t) && ! this.t.equals(otherT);
    }
}
