package diarsid.support.model.versioning;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import diarsid.support.model.Named;
import diarsid.support.objects.CommonEnum;

import static java.lang.Integer.min;
import static java.lang.String.format;
import static java.util.Collections.unmodifiableList;
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toUnmodifiableList;

import static diarsid.support.model.versioning.Version.CountStrategy.SINGLE_INTEGER;

public final class Version implements Named, Comparable<Version> {

    public static enum CountStrategy implements CommonEnum<CountStrategy> {

        MANY_INTEGERS,
        SINGLE_INTEGER;

        public static CountStrategy defineOnCount(int count) {
            switch ( count ) {
                case 0 : throw new IllegalArgumentException("numbers must not be empty!");
                case 1 : return SINGLE_INTEGER;
                default : return MANY_INTEGERS;
            }
        }
    }

    public final List<Integer> numbers;
    public final String name;
    public final String fullName;
    public final CountStrategy strategy;

    public Version(int... ints) {
        this.numbers = Arrays.stream(ints).boxed().collect(toUnmodifiableList());
        this.name = null;
        this.fullName = this.numbers
                .stream()
                .map(String::valueOf)
                .collect(joining("."));
        this.strategy = CountStrategy.defineOnCount(ints.length);
    }

    public Version(String name, int... ints) {
        requireNonNull(name, "Name must not be null!");
        this.numbers = Arrays.stream(ints).boxed().collect(toUnmodifiableList());
        this.name = name;
        this.fullName = this.numbers
                .stream()
                .map(String::valueOf)
                .collect(joining("."))
                + "-" + this.name;
        this.strategy = CountStrategy.defineOnCount(ints.length);
    }

    public Version(String version) {
        version = version.strip().trim();

        int firstDashIndex = version.indexOf('-');

        if ( firstDashIndex < 0 ) {
            this.numbers = unmodifiableList(splitToNumbersByDot(version));
            this.name = null;
        }
        else {
            String integers = version.substring(0, firstDashIndex);
            this.numbers = unmodifiableList(splitToNumbersByDot(integers));
            this.name = version.substring(firstDashIndex+1);
        }

        this.fullName = version;
        this.strategy = CountStrategy.defineOnCount(this.numbers.size());
    }

    public Version(int number) {
        this.numbers = List.of(number);
        this.name = null;
        this.fullName = String.valueOf(number);
        this.strategy = SINGLE_INTEGER;
    }

    public Version(int number, String name) {
        this.numbers = List.of(number);
        this.name = name;
        this.fullName = String.valueOf(number);
        this.strategy = SINGLE_INTEGER;
    }

    private Version(List<Integer> numbers, String name) {
        this.numbers = numbers;
        this.name = name;

        String full = this.numbers
                .stream()
                .map(String::valueOf)
                .collect(joining("."));


        if ( nonNull(this.name) ) {
            full = full + "-" + this.name;
        }

        this.fullName = full;
        this.strategy = CountStrategy.defineOnCount(numbers.size());
    }

    @Override
    public String name() {
        return this.fullName;
    }

    public static List<Integer> splitToNumbersByDot(String integers) {
        List<Integer> numbers = new ArrayList<>();
        int start = 0;
        int end = integers.indexOf('.');
        if ( end < 0 ) {
            end = integers.length();
        }
        String numberString;
        int number;
        while ( start < integers.length() ) {
            if ( end < 0 ) {
                end = integers.length();
            }
            numberString = integers.substring(start, end);
            number = Integer.parseInt(numberString);
            numbers.add(number);
            start = end + 1;
            end = integers.indexOf('.', start);
        }

        return numbers;
    }

    @Override
    public int compareTo(Version other) {
        if ( this.strategy.isNot(other.strategy) ) {
            throw new VersionNotComparableException(format(
                    "This %s cannot be compared to %s - they have different %s: %s -vs- %s",
                    this.fullName,
                    other.fullName,
                    CountStrategy.class.getSimpleName(),
                    this.strategy.name(),
                    other.strategy.name()));
        }

        int thisLevels = this.numbers.size();
        int otherLevels = other.numbers.size();

        int minLevels = min(thisLevels, otherLevels);

        int thisNumber;
        int otherNumber;

        for (int level = 0; level < minLevels + 1; level ++ ) {
            if ( level == minLevels ) {
                if ( thisLevels > otherLevels ) {
                    thisNumber = this.numbers.get(level);
                    otherNumber = 0;
                }
                else if ( thisLevels < otherLevels ) {
                    thisNumber = 0;
                    otherNumber = other.numbers.get(level);
                }
                else {
                    thisNumber = 0;
                    otherNumber = 0;
                }

                if ( thisNumber > otherNumber ) {
                    return 1;
                }
                else if ( thisNumber < otherNumber ) {
                    return -1;
                }
                else {
                    return 0;
                }
            }
            else {
                thisNumber = this.numbers.get(level);
                otherNumber = other.numbers.get(level);

                if ( thisNumber > otherNumber ) {
                    return 1;
                }
                else if ( thisNumber < otherNumber ) {
                    return -1;
                }
            }
        }

        return 0;
    }

    public boolean isComparableTo(Version other) {
        return this.strategy.is(other.strategy);
    }

    public boolean hasSameName(Version other) {
        return this.name.equals(other.name);
    }

    @Override
    public String toString() {
        return "Version[" + this.fullName + "]";
    }

    public Version increment() {
        if ( this.strategy.is(SINGLE_INTEGER) ) {
            return new Version(this.numbers.get(0) + 1, this.name);
        }
        else {
            return this.increment(this.numbers.size() - 1);
        }
    }

    public Version increment(int level) {
        if ( this.strategy.is(SINGLE_INTEGER) && level > 0 ) {
            throw new IllegalArgumentException("There is no levels in this version!");
        }

        List<Integer> newNumbers = new ArrayList<>(this.numbers);
        int value = newNumbers.get(level);
        value = value + 1;
        newNumbers.set(level, value);
        return new Version(newNumbers, this.name);
    }
}
