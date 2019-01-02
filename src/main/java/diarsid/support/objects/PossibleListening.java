/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package diarsid.support.objects;

import java.util.function.BiConsumer;

/**
 *
 * @author Diarsid
 */
public class PossibleListening<T> {
    
    private final RealPossibleListeneable<T> listeneable;
    private final BiConsumer<T, T> oldNewValueslistener; 
    private boolean isListening;

    public PossibleListening(
            RealPossibleListeneable<T> listeneable, 
            BiConsumer<T, T> oldNewValueslistener) {
        this.listeneable = listeneable;
        this.oldNewValueslistener = oldNewValueslistener;
        this.isListening = true;
    }
    
    public void cancelInternally() {
        this.isListening = false;
    }
    
    public void cancel() {
        BiConsumer<T, T> removedListener = this.listeneable.removeListener(this);
        this.isListening = false;
    }
    
    public boolean isListening() {
        return this.isListening;
    }
    
}
