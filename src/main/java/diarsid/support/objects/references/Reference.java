package diarsid.support.objects.references;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

public interface Reference<T> {

    enum ValuePresence {
        POSSIBLE,
        PRESENT
    }

    enum Type {
        VALUE,
        PROPERTY
    }

    boolean equalsTo(T otherT);

    boolean notEqualsTo(T otherT);

    boolean match(Predicate<T> predicate);

    boolean notMatch(Predicate<T> predicate);

    T resetTo(T newT);

    T modify(Function<T, T> oldToNew);

    Reference.ValuePresence valuePresence();

    Reference.Type type();
}
