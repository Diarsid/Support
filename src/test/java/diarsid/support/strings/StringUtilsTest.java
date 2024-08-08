package diarsid.support.strings;

import java.util.List;

import diarsid.test.BaseTest;
import org.junit.jupiter.api.Test;

import static java.util.Arrays.asList;

import static diarsid.support.strings.StringUtils.normalizeUnderscores;
import static diarsid.support.strings.StringUtils.splitByAnySeparators;
import static diarsid.support.strings.StringUtils.splitByTextSeparators;
import static diarsid.support.strings.StringUtils.splitCamelCase;

import static diarsid.support.strings.StringUtils.countCharMatchesIn;
import static diarsid.support.strings.StringUtils.countOccurrences;
import static diarsid.support.strings.StringUtils.removeAllSeparators;
import static diarsid.support.strings.StringUtils.splitToLines;
import static org.assertj.core.api.Assertions.assertThat;

public class StringUtilsTest extends BaseTest {
    
    @Test
    public void splitToLinesTest() {
        String multilines = "line1\nline2\nline3\n";
        List<String> expected = asList("line1", "line2", "line3");
        
        assertThat(splitToLines(multilines)).isEqualTo(expected);
    }
    
    @Test
    public void countOccurencesTest() {
        assertThat(countOccurrences("ABcABd", "AB")).isEqualTo(2);
    }
    
    @Test
    public void removeAllSeparatorsTest() {
        String target = "0_1-2\\3/4_5--6\\\\7//8__9";
        String result = removeAllSeparators(target);
        assertThat(result).isEqualTo("0123456789");
    }       
    
    @Test
    public void countCharMatchesInTest_1() {
        String first = "abcd";
        String second = "abcd";
        
        int matches = countCharMatchesIn(first, 2, 4, second, 2, 4);
        assertThat(matches).isEqualTo(2);
    }
    
    @Test
    public void countCharMatchesInTest_2() {
        String first = "abcdh";
        String second = "abcd_";
        
        int matches = countCharMatchesIn(first, 2, 5, second, 2, 5);
        assertThat(matches).isEqualTo(2);
    }
    
    @Test
    public void countCharMatchesInTest_3() {
        String first = "abdhc";
        String second = "abcd_";
        
        int matches = countCharMatchesIn(first, 2, 5, second, 2, 5);
        assertThat(matches).isEqualTo(2);
    }
    
    @Test
    public void countCharMatchesInTest_4() {
        String first = "abdhc";
        String second = "abcdc";
        
        int matches = countCharMatchesIn(first, 2, 5, second, 2, 5);
        assertThat(matches).isEqualTo(3);
    }

    @Test
    public void splitByTextSeparatorsTest1() {
        String phrase = "word__one_*&_two_%-and_#third";

        List<String> words = splitByTextSeparators(phrase);
        assertThat(words.size()).isEqualTo(5);
        assertThat(words.get(0)).isEqualTo("word");
        assertThat(words.get(1)).isEqualTo("one");
        assertThat(words.get(2)).isEqualTo("two");
        assertThat(words.get(3)).isEqualTo("and");
        assertThat(words.get(4)).isEqualTo("third");
    }

    @Test
    public void splitByTextSeparatorsTest2() {
        String phrase = "__word__one_*&_two_%-and_#third";

        List<String> words = splitByTextSeparators(phrase);
        assertThat(words.size()).isEqualTo(5);
        assertThat(words.get(0)).isEqualTo("word");
        assertThat(words.get(1)).isEqualTo("one");
        assertThat(words.get(2)).isEqualTo("two");
        assertThat(words.get(3)).isEqualTo("and");
        assertThat(words.get(4)).isEqualTo("third");
    }

    @Test
    public void splitByTextSeparatorsTest3() {
        String phrase = "word__one_*&_two_%-and_#third__";

        List<String> words = splitByTextSeparators(phrase);
        assertThat(words.size()).isEqualTo(5);
        assertThat(words.get(0)).isEqualTo("word");
        assertThat(words.get(1)).isEqualTo("one");
        assertThat(words.get(2)).isEqualTo("two");
        assertThat(words.get(3)).isEqualTo("and");
        assertThat(words.get(4)).isEqualTo("third");
    }

    @Test
    public void splitByTextSeparatorsTest4() {
        String phrase = "word";

        List<String> words = splitByTextSeparators(phrase);
        assertThat(words.size()).isEqualTo(1);
        assertThat(words.get(0)).isEqualTo("word");
    }

    @Test
    public void splitByTextSeparatorsTest5() {
        String phrase = "word__";

        List<String> words = splitByTextSeparators(phrase);
        assertThat(words.size()).isEqualTo(1);
        assertThat(words.get(0)).isEqualTo("word");
    }

    @Test
    public void splitByTextSeparatorsTest6() {
        String phrase = "____word__";

        List<String> words = splitByTextSeparators(phrase);
        assertThat(words.size()).isEqualTo(1);
        assertThat(words.get(0)).isEqualTo("word");
    }

    @Test
    public void splitByTextSeparatorsTest7() {
        String phrase = "__";

        List<String> words = splitByTextSeparators(phrase);
        assertThat(words.size()).isEqualTo(0);
    }

    @Test
    public void splitByAnySeparatorsTest() {
        String phrase = "John D";

        List<String> words = splitByAnySeparators(phrase);
        assertThat(words.size()).isEqualTo(2);
    }

    @Test
    public void splitByAnySeparatorsTest2() {
        String phrase = "John Dd";

        List<String> words = splitByAnySeparators(phrase);
        assertThat(words.size()).isEqualTo(2);
    }

    @Test
    public void splitCamelCaseTest_allowSingleCharSeparation() {
        boolean allowSingleCharSeparation = true;
        String camelCaseWithNumbers = "2ABS45CamelXYZ123CaseSString";
        List<String> words = splitCamelCase(camelCaseWithNumbers, allowSingleCharSeparation);

        List<String> expected = List.of("2", "ABS", "45", "Camel", "XYZ", "123", "Case", "S", "String");

        assertThat(words).isEqualTo(expected);
    }

    @Test
    public void splitCamelCaseTest_singleWord() {
        String camelCase = "String";
        List<String> words0 = splitCamelCase(camelCase, true);
        List<String> words1 = splitCamelCase(camelCase, true);

        List<String> expected = List.of("String");

        assertThat(words0).isEqualTo(expected);
        assertThat(words1).isEqualTo(expected);
    }

    @Test
    public void splitCamelCaseTest_prohibitSingleCharSeparation() {
        boolean prohibitSingleCharSeparation = false;
        String camelCaseWithNumbers = "2ABS45CamelXYZ123CaseSString";
        List<String> words = splitCamelCase(camelCaseWithNumbers, prohibitSingleCharSeparation);

        List<String> expected = List.of("2ABS", "45", "Camel", "XYZ", "123", "Case", "SString");

        assertThat(words).isEqualTo(expected);
    }

    @Test
    public void normalizeUnderscoresTest() {
        String target = "_a__b______c_d__";
        String expected = "a_b_c_d";
        String result = normalizeUnderscores(target);
        assertThat(result).isEqualTo(expected);
    }

    @Test
    public void replaceAllTest() {
        String result = StringUtils.replaceAllWith(
                "?", new StringBuilder("SQL ? ? ?"), 3, List.of("AA", "B?B", "CC"), false);
        String expected = "SQL AA B?B CC";
        assertThat(result).isEqualTo(expected);
    }
}
