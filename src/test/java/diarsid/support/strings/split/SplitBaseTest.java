package diarsid.support.strings.split;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class SplitBaseTest {

    protected Split split;
    protected String source;
    protected List<String> expected;
    protected List<String> actual;

    protected void splitAndAssert() {
        actual = split.process(source);
        assertThat(actual).isEqualTo(expected);
    }
}
