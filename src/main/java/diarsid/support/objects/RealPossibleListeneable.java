/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package diarsid.support.objects;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 *
 * @author Diarsid
 */
class RealPossibleListeneable<T> extends RealPossible<T> implements PossibleListeneable<T> {

    private final Map<PossibleListening<T>, BiConsumer<T, T>> listeners;

    RealPossibleListeneable() {
        super();
        this.listeners = new HashMap<>();
    }

    RealPossibleListeneable(T t) {
        super(t);
        this.listeners = new HashMap<>();
    }
    
    BiConsumer<T, T> removeListener(PossibleListening<T> listening) {
        BiConsumer<T, T> listener = this.listeners.remove(listening);
        return listener;
    }
    
    @Override
    protected void internalSet(T newT) {
        if ( ! this.listeners.isEmpty() ) {
            this.listeners
                    .entrySet()
                    .forEach(pair -> {
                        if ( pair.getKey().isListening() ) {
                            pair.getValue().accept(super.t, newT);
                        }                        
                    });
        }
        super.internalSet(newT);
    }
    
    @Override
    public PossibleListening<T> addListener(BiConsumer<T, T> oldNewValueslistener) {
        PossibleListening<T> listening = new PossibleListening<>(this, oldNewValueslistener);
        this.listeners.put(listening, oldNewValueslistener);
        return listening;
    }

    @Override
    public PossibleListeneable<T> clearListeners() {
        this.listeners.keySet().forEach(listening -> listening.cancelInternally());
        this.listeners.clear();
        return this;
    }

    @Override
    public Possible<T> asSimple() {
        return this;
    }
    
}
