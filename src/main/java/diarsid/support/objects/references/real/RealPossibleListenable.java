/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package diarsid.support.objects.references.real;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import diarsid.support.objects.references.Listening;

import static java.util.Objects.nonNull;

/**
 *
 * @author Diarsid
 */
class RealPossibleListenable<T> extends RealPossible<T> implements PossibleListenable<T> {

    private final Map<RealListening<T>, BiConsumer<T, T>> listeners;

    RealPossibleListenable() {
        super();
        this.listeners = new HashMap<>();
    }

    RealPossibleListenable(T t) {
        super(t);
        this.listeners = new HashMap<>();
    }
    
    @Override
    protected T internalSet(T newT) {
        T oldT = super.internalSet(newT);

        if ( ! this.listeners.isEmpty() ) {
            this.listeners
                    .entrySet()
                    .forEach(pair -> {
                        if ( pair.getKey().isListening() ) {
                            try {
                                pair.getValue().accept(oldT, newT);
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
    public Possible<T> asSimple() {
        return this;
    }

    @Override
    public boolean remove(Listening<T> listening) {
        if (listening instanceof RealListening) {
            BiConsumer<T, T> removed = this.listeners.remove(listening);
            return nonNull(removed);
        }
        else {
            return false;
        }
    }
}
