/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package diarsid.support.objects;

import java.util.Optional;

import static java.util.Arrays.stream;

/**
 *
 * @author Diarsid
 */
public class Possibles {    
    
    public static <T> Possible<T> possibleButEmpty() {
        return new Possible<>(null);
    }
    
    public static <T> Possible<T> possibleWith(T t) {
        return new Possible<>(t);
    }
    
    public static <T> Possible<T> possibleOf(Optional<T> optionalT) {
        return new Possible<>(optionalT.orElse(null));
    }
    
    public static boolean allPresent(Possible one, Possible two) {
        return one.isPresent() && two.isPresent();
    }
    
    public static boolean allPresent(
            Possible one, Possible two, Possible three, Possible... possibles) {
        if ( one.isPresent() && two.isPresent() && three.isPresent() ) {
            if ( possibles.length == 0 ) {
                return true;
            } else {
                return stream(possibles).allMatch(possible -> possible.isPresent());
            }
        } else {
            return false;
        }      
    }
}
