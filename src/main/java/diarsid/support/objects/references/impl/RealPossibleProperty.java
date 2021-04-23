/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package diarsid.support.objects.references.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import diarsid.support.objects.references.Listenable;
import diarsid.support.objects.references.Listening;
import diarsid.support.objects.references.PossibleProperty;

import static java.util.Objects.nonNull;

/**
 *
 * @author Diarsid
 */
public class RealPossibleProperty<T>
        extends SimplePossible<T>
        implements PossibleProperty<T>, RealBindable<T>, ListenableRemovable<T> {

    private final Map<RealListening<T>, BiConsumer<T, T>> listeners;
    private final Map<Listenable<T>, Listening<T>> bindings;

    public RealPossibleProperty() {
        super();
        this.listeners = new HashMap<>();
        this.bindings = new HashMap<>();
    }

    public RealPossibleProperty(T t) {
        super(t);
        this.listeners = new HashMap<>();
        this.bindings = new HashMap<>();
    }
    
    @Override
    protected T internalSet(T newT) {
        T oldT = super.internalSet(newT);

        if ( ! this.listeners.isEmpty() ) {
            this.listeners.forEach((listening, listener) -> {
                if ( listening.isListening() ) {
                    try {
                        listener.accept(oldT, newT);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        return oldT;
    }

    @Override
    public Listening<T> listen(BiConsumer<T, T> listener) {
        RealListening<T> listening = new RealListening<>(this);
        this.listeners.put(listening, listener);
        return listening;
    }

    @Override
    public void clearListeners() {
        this.listeners.keySet().forEach(RealListening::cancelInternally);
        this.listeners.clear();
    }

    @Override
    public Map<Listenable<T>, Listening<T>> bindings() {
        return this.bindings;
    }

    @Override
    public boolean remove(Listening<T> listening) {
        if ( listening instanceof RealListening ) {
            BiConsumer<T, T> removed = this.listeners.remove(listening);
            return nonNull(removed);
        }
        else {
            return false;
        }
    }
}
