package diarsid.support.strings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import diarsid.support.objects.references.Possible;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import static diarsid.support.objects.references.References.simplePossibleButEmpty;

public class CharactersCount {

    private static final int NO_OCCURRENCES = 0;

    private final Possible<String> string;
    private final Map<Character, AtomicInteger> countersByChars;
    private final List<Character> foundChars;

    public interface CountConsumer {

        void accept(Character c, int count);
    }

    public static class Result {

        private final String string;
        private final List<Occurrence> occurrences;

        Result(String string) {
            this.string = string;
            this.occurrences = new ArrayList<>();
        }

        void accept(Character c, int count) {
            this.occurrences.add(new Occurrence(c, count));
        }

        public String string() {
            return this.string;
        }

        public List<Occurrence> occurrences() {
            return this.occurrences;
        }
    }

    public static class Occurrence {

        private final Character character;
        private final int count;

        Occurrence(Character character, int count) {
            this.character = character;
            this.count = count;
        }

        public Character character() {
            return this.character;
        }

        public int count() {
            return this.count;
        }
    }

    public CharactersCount() {
        this.countersByChars = new HashMap<>();
        this.foundChars = new ArrayList<>();
        this.string = simplePossibleButEmpty();
    }

    public CharactersCount(String s) {
        this.countersByChars = new HashMap<>();
        this.foundChars = new ArrayList<>();
        this.string = simplePossibleButEmpty();
        this.calculateIn(s);
        this.formResult();
    }

    public void clear() {
        this.countersByChars.values().forEach(this::setCounterToZero);
        this.foundChars.clear();
        this.string.nullify();
    }

    private void setCounterToZero(AtomicInteger counter) {
        counter.set(NO_OCCURRENCES);
    }

    public void calculateIn(String s) {
        this.clear();

        this.string.resetTo(s);

        AtomicInteger counter;
        Character c;

        AtomicInteger prevCounter = null;
        Character prevC = null;

        int length = s.length();
        for ( int i = 0; i < length; i++ ) {
            c = s.charAt(i);
            if ( i > 0 ) {
                if ( prevC == c ) {
                    counter = prevCounter;
                }
                else {
                    counter = this.countersByChars.get(c);
                }
            }
            else {
                counter = this.countersByChars.get(c);
            }


            if ( isNull(counter) ) {
                counter = new AtomicInteger(0);
                this.countersByChars.put(c, counter);
            }

            if ( counter.get() == 0 ) {
                this.foundChars.add(c);
            }

            counter.incrementAndGet();

            prevCounter = counter;
            prevC = c;
        }
    }

    public boolean isFilled() {
        return this.string.isPresent();
    }

    public boolean isNotFilled() {
        return this.string.isNotPresent();
    }

    public int uniqueCharsQty() {
        return this.foundChars.size();
    }

    public int countOf(char c) {
        if ( this.string.isNotPresent() ) {
            return NO_OCCURRENCES;
        }

        AtomicInteger counter = this.countersByChars.get(c);

        int count;
        if ( nonNull(counter) ) {
            count = counter.get();
        }
        else {
            count = NO_OCCURRENCES;
        }

        return count;
    }

    public void forEach(CountConsumer consumer) {
        if ( this.string.isNotPresent() ) {
            return;
        }

        AtomicInteger count;
        for ( Character c : this.foundChars ) {
            count = this.countersByChars.get(c);
            consumer.accept(c, count.get());
        }
    }

    private Result formResult() {
        Result result = new Result(this.string.orThrow());
        this.forEach(result::accept);
        return result;
    }

    public CharactersCount.Result result() {
        return this.formResult();
    }
}
