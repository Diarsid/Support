package diarsid.support.objects.references.impl;

import java.util.Optional;

import static java.util.Arrays.stream;

public class References {

    public static <T> Possible<T> possibleButEmpty() {
        return new RealPossible<>(null);
    }

    public static <T> Possible<T> possibleWith(T t) {
        return new RealPossible<>(t);
    }

    public static <T> Possible<T> possibleOf(Optional<T> optionalT) {
        return new RealPossible<>(optionalT.orElse(null));
    }

    public static <T> PossibleListenable<T> listeneable(Possible<T> possibleT) {
        return new RealPossibleListenable<>(possibleT.or(null));
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

    public static <T> Present<T> presentOf(T t, String name) {
        return new RealPresent<>(t, name);
    }

    public static <T> PresentListenable<T> listenablePresent(T t, String name) {
        return new RealPresentListenable<>(t, name);
    }

    public static <T> PresentListenable<T> listenable(Present<T> presentT, String name) {
        return new RealPresentListenable<>(presentT.get(), name);
    }
}
