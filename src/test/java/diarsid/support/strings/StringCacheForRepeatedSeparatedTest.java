package diarsid.support.strings;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class StringCacheForRepeatedSeparatedTest {

    @Test
    public void test() {
        StringCacheForRepeatedSeparated cache = new StringCacheForRepeatedSeparated("?", ", ");

        assertThat(cache.getFor(3)).isEqualTo("?, ?, ?");
    }
}
