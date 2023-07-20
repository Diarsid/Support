package diarsid.support.strings.split;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SplitByCharTest extends SplitBaseTest {

    @BeforeEach
    public void setUp() {
        split = new SplitByChar('X');
    }

    @Test
    public void case1() {
        source = "   aaa bbbXaaa Xaaa";
        expected = List.of("   aaa bbb", "aaa ", "aaa");
        splitAndAssert();
    }

    @Test
    public void case2() {
        source = "   aaa bbbXaaa XaaaXXX";
        expected = List.of("   aaa bbb", "aaa ", "aaa");
        splitAndAssert();
    }

    @Test
    public void case3() {
        source = "X   aaa bbbXaaa XaaaXXX";
        expected = List.of("   aaa bbb", "aaa ", "aaa");
        splitAndAssert();
    }
}
