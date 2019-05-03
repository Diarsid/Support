/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package diarsid.concurrency.async.provider;

import java.util.Objects;
import java.util.Optional;

import static java.util.Optional.ofNullable;

/**
 *
 * @author Diarsid
 */
public class Awaited<T> {
    
    private final T t;
    private final AwaitedResult result;

    Awaited(T t, AwaitedResult result) {
        this.t = t;
        this.result = result;
    }

    public Optional<T> optional() {
        return ofNullable(this.t);
    }

    public AwaitedResult awaitedResult() {
        return this.result;
    }

    @Override
    public String toString() {
        return "Awaited{" + "value=" + this.t + ", result=" + this.result + '}';
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 83 * hash + Objects.hashCode(this.t);
        hash = 83 * hash + Objects.hashCode(this.result);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) {
            return true;
        }
        if ( obj == null ) {
            return false;
        }
        if ( getClass() != obj.getClass() ) {
            return false;
        }
        final Awaited<?> other = ( Awaited<?> ) obj;
        if ( !Objects.equals(this.t, other.t) ) {
            return false;
        }
        if ( this.result != other.result ) {
            return false;
        }
        return true;
    }
    
    
    
}
