package diarsid.support.string.replace;

import diarsid.support.strings.replace.Replace;
import diarsid.support.strings.replace.ReplacePair;
import org.junit.Test;

import static diarsid.support.strings.replace.Replace.replace;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class ReplaceTest {

    @Test
    public void test() {
        Replace replace = replace().stringToStringFirstFrom(
                new ReplacePair("left join", "LEFT JOIN"),
                new ReplacePair("join", "JOIN_X"));

        String target = "... left join ...";
        String expected = "... LEFT JOIN ...";
        String actual = replace.doFor(target);

        assertThat(actual, equalTo(expected));
    }
}
