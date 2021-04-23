package diarsid.support.strings;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class StringCacheForRepeatedSeparatedTest {

    @Test
    public void test() {
        StringCacheForRepeatedSeparated cache = new StringCacheForRepeatedSeparated("?", ", ");

        assertThat(cache.getFor(3), equalTo("?, ?, ?"));
    }
}
