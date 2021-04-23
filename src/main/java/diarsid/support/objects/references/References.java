package diarsid.support.objects.references;

import java.util.Optional;

import diarsid.support.objects.references.impl.RealPossibleProperty;
import diarsid.support.objects.references.impl.RealPresentProperty;
import diarsid.support.objects.references.impl.SimplePossible;
import diarsid.support.objects.references.impl.SimplePresent;

import static java.util.Arrays.stream;

public class References {

    public static <T> Possible<T> simplePossibleButEmpty() {
        return new SimplePossible<>(null);
    }

    public static <T> Possible<T> simplePossibleWith(T t) {
        return new SimplePossible<>(t);
    }

    public static <T> Possible<T> simplePossibleOf(Optional<T> optionalT) {
        return new SimplePossible<>(optionalT.orElse(null));
    }

    public static <T> PossibleProperty<T> possiblePropertyOf(T t) {
        return new RealPossibleProperty<>(t);
    }

    public static <T> PossibleProperty<T> possiblePropertyOf(Optional<T> optionalT) {
        return new RealPossibleProperty<>(optionalT.orElse(null));
    }

    public static <T> PossibleProperty<T> possiblePropertyOf(Possible<T> possibleT) {
        return new RealPossibleProperty<>(possibleT.or(null));
    }

    public static <T> PossibleProperty<T> possiblePropertyButEmpty() {
        return new RealPossibleProperty<>();
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

    public static <T> Present<T> simplePresentOf(T t) {
        return new SimplePresent<>(t);
    }

    public static <T> PresentProperty<T> presentPropertyOf(T t, String name) {
        return new RealPresentProperty<>(t, name);
    }

    public static <T> PresentProperty<T> presentPropertyOf(Present<T> presentT, String name) {
        return new RealPresentProperty<>(presentT.get(), name);
    }
}
