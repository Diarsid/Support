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
public interface PossibleListeneable<T> extends Possible<T> {
    
    PossibleListening<T> addListener(BiConsumer<T, T> oldNewValueslistener);
    
    PossibleListeneable<T> clearListeners();
    
    Possible<T> asSimple();
    
}
