/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package diarsid.support.objects.references.real;

/**
 *
 * @author Diarsid
 */
public interface PossibleListenable<T> extends Possible<T>, ListenableRemovable<T> {
    
    Possible<T> asSimple();
    
}
