package diarsid.support.model;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;

public interface Joined<LEFT, RIGHT> {

    LEFT left();

    RIGHT right();

    default boolean hasLeft() {
        return nonNull(this.left());
    }
    default boolean hasNoLeft() {
        return isNull(this.left());
    }

    default boolean hasRight() {
        return nonNull(this.right());
    }

    default boolean hasNoRight() {
        return isNull(this.right());
    }

    default boolean hasLeft(LEFT otherLeft) {
        return this.hasLeft() && this.left().equals(otherLeft);
    }

    default boolean hasRight(RIGHT otherRight) {
        return this.hasRight() && this.right().equals(otherRight);
    }

    static <J extends Joined<L, R>, L, R> List<J> makeJoined(L left, List<R> rights, BiFunction<L, R, J> joining) {
        return rights
                .stream()
                .map(rigt -> joining.apply(left, rigt))
                .collect(toList());
    }

    static <J extends Joined<L, R>, L, R> List<L> distinctLeftsOf(List<J> joins) {
        return joins
                .stream()
                .map(Joined::left)
                .distinct()
                .collect(toList());
    }

    static <J extends Joined<L, R>, L, R> List<R> distinctRightsOf(List<J> joins) {
        return joins
                .stream()
                .map(Joined::right)
                .distinct()
                .collect(toList());
    }

    static <J extends Joined<L, R>, L, R> List<L> allLeftsOf(List<J> joins) {
        return joins
                .stream()
                .map(Joined::left)
                .collect(toList());
    }

    static <J extends Joined<L, R>, L, R> List<R> allRightsOf(List<J> joins) {
        return joins
                .stream()
                .map(Joined::right)
                .collect(toList());
    }

    static <J extends Joined<L, R>, L, R> Map<L, List<R>> mapLeftKeys(List<J> joins) {
        Map<L, List<R>> map = joins
                .stream()
                .collect(groupingBy(
                        Joined::left,
                        mapping(Joined::right, toList())));

        return map;
    }

}
