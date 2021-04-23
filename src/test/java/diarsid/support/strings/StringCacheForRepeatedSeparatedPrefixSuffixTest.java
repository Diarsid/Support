package diarsid.support.strings;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class StringCacheForRepeatedSeparatedPrefixSuffixTest {

    @Test
    public void test() {
        StringCacheForRepeatedSeparatedPrefixSuffix cache = new StringCacheForRepeatedSeparatedPrefixSuffix(
                "BEFORE_", "?", ", ", "_AFTER");

        assertThat(cache.getFor(3), equalTo("BEFORE_?, ?, ?_AFTER"));
    }
}
