package diarsid.support.objects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import diarsid.support.exceptions.FailedExpectationException;
import diarsid.support.exceptions.UnsupportedEnumException;

import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.Objects.isNull;

public interface CommonEnum<E extends Enum<E>> extends Serializable, Equalable<E> {

    default boolean equalTo(E other) {
        return this.equals(other);
    }

    default boolean notEqualTo(E other) {
        return ! this.equals(other);
    }

    default boolean equalToAny(E o1, E o2) {
        return this.equals(o1) || this.equals(o2);
    }

    default boolean equalToAny(E o1, E o2, E o3) {
        return this.equals(o1) || this.equals(o2) || this.equals(o3);
    }

    default boolean equalToAny(E o1, E o2, E o3, E o4) {
        return this.equals(o1) || this.equals(o2) || this.equals(o3) || this.equals(o4);
    }

    default boolean equalToAny(E o1, E o2, E o3, E o4, E o5) {
        return this.equals(o1) || this.equals(o2) || this.equals(o3) || this.equals(o4) || this.equals(o5);
    }

    default boolean equalToAny(E o1, E o2, E o3, E o4, E o5, E... others) {
        if ( this.equalToAny(o1, o2, o3, o4, o5) ) {
            return true;
        } else {
            if ( isNull(others) || others.length == 0 ) {
                return false;
            }

            return stream(others).anyMatch(this::equalTo);
        }
    }

    default boolean notEqualToAny(E o1, E o2) {
        return (! this.equals(o1)) && (! this.equals(o2));
    }

    default boolean notEqualToAny(E o1, E o2, E o3) {
        return (! this.equals(o1)) && (! this.equals(o2)) && (! this.equals(o3));
    }

    default boolean notEqualToAny(E o1, E o2, E o3, E o4) {
        return (! this.equals(o1)) && (! this.equals(o2)) && (! this.equals(o3)) && (! this.equals(o4));
    }

    default boolean notEqualToAny(E o1, E o2, E o3, E o4, E o5) {
        return (! this.equals(o1)) && (! this.equals(o2)) && (! this.equals(o3)) && (! this.equals(o4)) && (! this.equals(o5));
    }

    default boolean notEqualToAny(E o1, E o2, E o3, E o4, E o5, E... others) {
        if ( this.notEqualToAny(o1, o2, o3, o4, o5) ) {
            if ( isNull(others) || others.length == 0 ) {
                return true;
            }

            return stream(others).noneMatch(this::equalTo);
        } else {
            return false;
        }
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

    default UnsupportedEnumException unsupported() {
        return new UnsupportedEnumException((Enum<?>) this);
    }

    default void throwUnsupported() {
        throw new UnsupportedEnumException((Enum<?>) this);
    }

    default void mustBe(E other) {
        if ( this.notEqualTo(other) ) {
            throw new FailedExpectationException(other, this);
        }
    }

    @SuppressWarnings("unchecked")
    default void mustNotBe(E other) {
        if ( this.equalTo(other) ) {
            CommonEnum<E> e = (CommonEnum<E>) other;
            throw new FailedExpectationException(e.others(), this);
        }
    }

    @SuppressWarnings("unchecked")
    default List<E> all() {
        Enum<E> eEnum = (Enum<E>) this;
        E[] values = eEnum.getDeclaringClass().getEnumConstants();
        return asList(values);
    }

    @SuppressWarnings("unchecked")
    default List<E> others() {
        Enum<E> eEnum = (Enum<E>) this;
        E[] values = eEnum.getDeclaringClass().getEnumConstants();
        List<E> all = new ArrayList<>();
        for ( E e : values ) {
            all.add(e);
        }
        all.remove(this);
        return all;
    }

}
