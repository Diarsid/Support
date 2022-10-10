package diarsid.support.strings;


import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class StringCacheForRepeatedSeparatedPrefixSuffixTest {

    @Test
    public void test() {
        StringCacheForRepeatedSeparatedPrefixSuffix cache = new StringCacheForRepeatedSeparatedPrefixSuffix(
                "BEFORE_", "?", ", ", "_AFTER");

        assertThat(cache.getFor(3)).isEqualTo("BEFORE_?, ?, ?_AFTER");
    }
}
