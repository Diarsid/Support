package diarsid.support.strings.split;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SplitByStringTest extends SplitBaseTest {

    @BeforeEach
    public void setUp() {
        split = new SplitByString("XXX");
    }

    @Test
    public void case1() {
        source = "   aaa bbbXXXaaa XXXaaa";
        expected = List.of("   aaa bbb", "aaa ", "aaa");
        splitAndAssert();
    }

    @Test
    public void case2() {
        source = "   aaa bbbXXXaaa XXXaaaXXXXXX";
        expected = List.of("   aaa bbb", "aaa ", "aaa");
        splitAndAssert();
    }

    @Test
    public void case3() {
        source = "XXX   aaa bbbXXXaaa XXXaaaXXXXXX";
        expected = List.of("   aaa bbb", "aaa ", "aaa");
        splitAndAssert();
    }

    @Test
    public void case4() {
        source = "XXXXXXXXX   aaa bbbXXXaaa XXXaaaXXXXXX";
        expected = List.of("   aaa bbb", "aaa ", "aaa");
        splitAndAssert();
    }
}
