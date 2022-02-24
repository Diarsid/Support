package diarsid.support.objects;

import java.util.Collection;

import static java.util.Arrays.stream;
import static java.util.Objects.isNull;

public interface Equalable<T> {

    default boolean is(T other) {
        return this.equals(other);
    }

    default boolean isNot(T other) {
        return ! this.equals(other);
    }

    default boolean isAny(T o1, T o2) {
        return this.equals(o1) || this.equals(o2);
    }

    default boolean isAny(T o1, T o2, T o3) {
        return this.equals(o1) || this.equals(o2) || this.equals(o3);
    }

    default boolean isAny(T o1, T o2, T o3, T o4) {
        return this.equals(o1) || this.equals(o2) || this.equals(o3) || this.equals(o4);
    }

    default boolean isAny(T o1, T o2, T o3, T o4, T o5) {
        return this.equals(o1) || this.equals(o2) || this.equals(o3) || this.equals(o4) || this.equals(o5);
    }

    default boolean isAny(T o1, T o2, T o3, T o4, T o5, T... others) {
        if ( this.isAny(o1, o2, o3, o4, o5) ) {
            return true;
        }
        else {
            if ( isNull(others) || others.length == 0 ) {
                return false;
            }

            return stream(others).anyMatch(this::is);
        }
    }

    default boolean isNotAny(T o1, T o2) {
        return (! this.equals(o1)) && (! this.equals(o2));
    }

    default boolean isNotAny(T o1, T o2, T o3) {
        return (! this.equals(o1)) && (! this.equals(o2)) && (! this.equals(o3));
    }

    default boolean isNotAny(T o1, T o2, T o3, T o4) {
        return (! this.equals(o1)) && (! this.equals(o2)) && (! this.equals(o3)) && (! this.equals(o4));
    }

    default boolean isNotAny(T o1, T o2, T o3, T o4, T o5) {
        return (! this.equals(o1)) && (! this.equals(o2)) && (! this.equals(o3)) && (! this.equals(o4)) && (! this.equals(o5));
    }

    default boolean isNotAny(T o1, T o2, T o3, T o4, T o5, T... others) {
        if ( this.isNotAny(o1, o2, o3, o4, o5) ) {
            if ( isNull(others) || others.length == 0 ) {
                return true;
            }

            return stream(others).noneMatch(this::is);
        }
        else {
            return false;
        }
    }

    default boolean isAny(Collection<T> others) {
        if ( isNull(others) || others.isEmpty() ) {
            return false;
        }

        return others.stream().anyMatch(this::is);
    }
}
