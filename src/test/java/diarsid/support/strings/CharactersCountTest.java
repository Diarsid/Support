package diarsid.support.strings;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CharactersCountTest {

    @Test
    public void test() {
        CharactersCount charactersCount = new CharactersCount();
        charactersCount.calculateIn("aaabbc");
        assertThat(charactersCount.countOf('a')).isEqualTo(3);
        assertThat(charactersCount.countOf('b')).isEqualTo(2);
        assertThat(charactersCount.countOf('c')).isEqualTo(1);
        assertThat(charactersCount.countOf('x')).isEqualTo(0);

        charactersCount.clear();

        String s2 = "xxxyy_";
        charactersCount.calculateIn(s2);

        assertThat(charactersCount.countOf('a')).isEqualTo(0);
        assertThat(charactersCount.countOf('b')).isEqualTo(0);
        assertThat(charactersCount.countOf('c')).isEqualTo(0);
        assertThat(charactersCount.countOf('x')).isEqualTo(3);
        assertThat(charactersCount.countOf('y')).isEqualTo(2);
        assertThat(charactersCount.countOf('_')).isEqualTo(1);

        AtomicInteger counter = new AtomicInteger(0);
        charactersCount.forEach((c, count) -> {
            counter.incrementAndGet();
        });
        assertThat(counter.get()).isEqualTo(3);
        assertThat(charactersCount.uniqueCharsQty()).isEqualTo(3);

    }

    public static void main(String[] args) {
        likeAndPosition();
    }

    private static void like() {
        String word = "cesarchavez";
        CharactersCount charactersCount = new CharactersCount();
        charactersCount.calculateIn(word);

        String sql = charactersCount
                .result()
                .occurrences()
                .stream()
                .map(occurrence -> {
                    if ( occurrence.count() == 1 ) {
                        return "%" + occurrence.character() + "%";
                    }
                    else {
                        StringBuilder builder = new StringBuilder();
                        for (int i = 0; i < occurrence.count()-1; i++) {
                            builder.append('%').append(occurrence.character());
                        }
                        builder.append('%').append(occurrence.character()).append('%');
                        return builder.toString();
                    }
                })
                .map(expression -> "    ( CASE WHEN string LIKE '" + expression + "' THEN 1 ELSE 0 END )")
                .collect(Collectors.joining(" + \n")) + " = " + charactersCount.uniqueCharsQty();


        System.out.println(sql);
    }

    private static void likeAndPosition() {
        String word = "3toolsserv";
        CharactersCount charactersCount = new CharactersCount();
        charactersCount.calculateIn(word);

        String sql = charactersCount
                .result()
                .occurrences()
                .stream()
                .map(occurrence -> {
                    return "    ( CASE WHEN string LIKE '%" + occurrence.character() + "%' THEN TO_CHAR(POSITION('" + occurrence.character() + "', string)) ELSE '' END )";
                })
                .collect(Collectors.joining(" + \n")) + " = " + charactersCount.uniqueCharsQty();



        System.out.println(sql);
    }

    private static void position() {
        String word = "3toolsserv";
        CharactersCount charactersCount = new CharactersCount();
        charactersCount.calculateIn(word);

        String sql = charactersCount
                .result()
                .occurrences()
                .stream()
                .map(occurrence -> {
                    if ( occurrence.count() == 1 ) {
                        return "POSITION('" + occurrence.character() + "', string) >= 0";
                    }
                    else {
                        StringBuilder builder = new StringBuilder();
                        for (int i = 0; i < occurrence.count()-1; i++) {
                            builder.append('%').append(occurrence.character());
                        }
                        builder.append('%');
                        return builder.toString();
                    }
                })
                .map(expression -> "    ( CASE WHEN " + expression + " THEN 1 ELSE 0 END )")
                .collect(Collectors.joining(" + \n")) + " = " + charactersCount.uniqueCharsQty();


        System.out.println(sql);
    }
}
