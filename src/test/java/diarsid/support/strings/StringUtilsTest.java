package diarsid.support.strings;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.util.List;

import org.junit.Test;

import diarsid.test.BaseTest;

import static java.util.Arrays.asList;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import static diarsid.support.strings.StringUtils.countCharMatchesIn;
import static diarsid.support.strings.StringUtils.countOccurences;
import static diarsid.support.strings.StringUtils.removeAllSeparators;
import static diarsid.support.strings.StringUtils.splitToLines;

/**
 *
 * @author Diarsid
 */
public class StringUtilsTest extends BaseTest {
    
    @Test
    public void splitToLinesTest() {
        String multilines = "line1\nline2\nline3\n";
        List<String> expected = asList("line1", "line2", "line3");
        
        assertThat(splitToLines(multilines), equalTo(expected));
    }
    
    @Test
    public void countOccurencesTest() {
        assertThat(countOccurences("ABcABd", "AB"), equalTo(2));
    }
    
    @Test
    public void removeAllSeparatorsTest() {
        String target = "0_1-2\\3/4_5--6\\\\7//8__9";
        String result = removeAllSeparators(target);
        assertThat(result, equalTo("0123456789"));
    }       
    
    @Test
    public void countCharMatchesInTest_1() {
        String first = "abcd";
        String second = "abcd";
        
        int matches = countCharMatchesIn(first, 2, 4, second, 2, 4);
        assertThat(matches, equalTo(2));
    }
    
    @Test
    public void countCharMatchesInTest_2() {
        String first = "abcdh";
        String second = "abcd_";
        
        int matches = countCharMatchesIn(first, 2, 5, second, 2, 5);
        assertThat(matches, equalTo(2));
    }
    
    @Test
    public void countCharMatchesInTest_3() {
        String first = "abdhc";
        String second = "abcd_";
        
        int matches = countCharMatchesIn(first, 2, 5, second, 2, 5);
        assertThat(matches, equalTo(2));
    }
    
    @Test
    public void countCharMatchesInTest_4() {
        String first = "abdhc";
        String second = "abcdc";
        
        int matches = countCharMatchesIn(first, 2, 5, second, 2, 5);
        assertThat(matches, equalTo(3));
    }
}
