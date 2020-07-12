package diarsid.support.objects;

import java.util.Collection;

import static java.util.Arrays.stream;
import static java.util.Objects.isNull;

public interface CommonEnum<E extends Enum<E>> {

    default boolean equalTo(E other) {
        return this.equals(other);
    }

    default boolean notEqualTo(E other) {
        return ! this.equals(other);
    }

    default boolean equalToAny(E... others) {
        if ( isNull(others) || others.length == 0 ) {
            return false;
        }

        return stream(others).anyMatch(this::equalTo);
    }

    default boolean notEqualToAny(E... others) {
        if ( isNull(others) || others.length == 0 ) {
            return true;
        }

        return stream(others).noneMatch(this::equalTo);
    }

    default boolean equalToAny(Collection<E> others) {
        if ( isNull(others) || others.isEmpty() ) {
            return false;
        }

        return others.stream().anyMatch(this::equalTo);
    }

    default boolean notEqualToAny(Collection<E> others) {
        if ( isNull(others) || others.isEmpty() ) {
            return true;
        }

        return others.stream().noneMatch(this::equalTo);
    }

    default boolean hasName(String name) {
        if ( isNull(name) ) {
            return false;
        }

        E thisEnum = (E) this;
        return thisEnum.name().equalsIgnoreCase(name.strip());
    }
}
