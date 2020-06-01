/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package diarsid.support.objects.references.impl;

import java.util.Objects;
import java.util.UUID;

import diarsid.support.objects.references.Listenable;
import diarsid.support.objects.references.Listening;

import static java.util.UUID.randomUUID;

/**
 *
 * @author Diarsid
 */
class RealListening<T> implements Listening<T> {

    private final UUID uuid;
    private final ListenableRemovable<T> listenable;
    private boolean isListening;

    RealListening(ListenableRemovable<T> listenable) {
        this.uuid = randomUUID();
        this.listenable = listenable;
        this.isListening = true;
    }
    
    public void cancelInternally() {
        this.isListening = false;
    }

    @Override
    public Listenable<T> source() {
        return this.listenable;
    }

    @Override
    public boolean cancel() {
        this.isListening = this.listenable.remove(this);
        return this.isListening;
    }
    
    public boolean isListening() {
        return this.isListening;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RealListening)) return false;
        RealListening<?> that = (RealListening<?>) o;
        return uuid.equals(that.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }
}
