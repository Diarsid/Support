package diarsid.support.strings.split;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SplitBySpacesIgnoringQuotesTest extends SplitBaseTest {

    @BeforeEach
    public void setUp() {
        split = new SplitBySpacesIgnoringQuotes();
    }

    @Test
    public void case1() {
        source = "   aaa bbb ' aaa ccc'xxxx yyy ";
        expected = List.of("aaa", "bbb", " aaa ccc", "xxxx", "yyy");
        splitAndAssert();
    }

    @Test
    public void case2() {
        source = "   aaa bbb \" aaa ccc\"xxxx yyy ";
        expected = List.of("aaa", "bbb", " aaa ccc", "xxxx", "yyy");
        splitAndAssert();
    }

    @Test
    public void case3() {
        source = "   aaa      bbb     \"aaa   ccc \" xxxx   yyy   ";
        expected = List.of("aaa", "bbb", "aaa   ccc ", "xxxx", "yyy");
        splitAndAssert();
    }

    @Test
    public void case4() {
        source = "   aaa bbb ' aaa cccxxxx yyy ";
        expected = List.of("aaa", "bbb", "'", "aaa", "cccxxxx", "yyy");
        splitAndAssert();
    }

    @Test
    public void case5() {
        source = "   aaa bbb'aaa cccxxxx yyy ";
        expected = List.of("aaa", "bbb'aaa", "cccxxxx", "yyy");
        splitAndAssert();
    }

    @Test
    public void case6() {
        source = "   aaa bbb' aaa cccxxxx yyy ";
        expected = List.of("aaa", "bbb'", "aaa", "cccxxxx", "yyy");
        splitAndAssert();
    }

    @Test
    public void case7() {
        source = "   aaa bbb 'aaa cccxxxx yyy ";
        expected = List.of("aaa", "bbb", "'aaa", "cccxxxx", "yyy");
        splitAndAssert();
    }

    @Test
    public void case8() {
        source = "   aaa bbb '' aaa cccxxxx yyy ";
        expected = List.of("aaa", "bbb", "", "aaa", "cccxxxx", "yyy");
        splitAndAssert();
    }

    @Test
    public void case9() {
        source = "   aaa bbb '   ' aaa cccxxxx yyy ";
        expected = List.of("aaa", "bbb", "   ", "aaa", "cccxxxx", "yyy");
        splitAndAssert();
    }
}
