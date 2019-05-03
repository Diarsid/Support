/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package diarsid.support.strings;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringJoiner;
import java.util.function.Predicate;

import diarsid.support.strings.replace.Replace;

import static java.lang.String.join;
import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.Locale.ENGLISH;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;

import static diarsid.support.strings.replace.Replace.replace;

/**
 *
 * @author Diarsid
 */
public class StringUtils {
    
    private final static RandomHexadecimalStringGenerator GENERATOR;
    private final static Predicate<String> NON_EMPTY;
    private static final Replace ALL_SEPARATORS_REPLACE;
    
    static {
        GENERATOR = new RandomHexadecimalStringGenerator();
        NON_EMPTY = s -> nonEmpty(s);
        ALL_SEPARATORS_REPLACE = replace()
                .regexToString("[/\\\\]+", "")
                .regexToString("-+", "")
                .regexToString("\\s+", "")
                .regexToString("_+", "");
    }
    
    private StringUtils() {
    }
    
    public static Predicate<String> nonEmpty() {
        return NON_EMPTY;
    }
    
    public static String randomString(int length) {
        return GENERATOR.randomString(length);
    }
    
    public static String lower(String target) {
        if ( isLower(target) ) {
            return target;
        } else {
            return target.toLowerCase(ENGLISH);
        }        
    }
    
    public static boolean isLower(String target) {
        int length = target.length();
        for (int i = 0; i < length; i++) {
            if ( ! Character.isLowerCase(target.charAt(i)) ) {
                return false;
            }
        }
        return true;
    }
    
    public static List<String> lower(List<String> targets) {
        return targets
                .stream()
                .map(target -> lower(target))
                .collect(toList());
    }
    
    public static String upper(String target) {
        return target.toUpperCase(ENGLISH);
    }
    
    public static boolean nonEmpty(CharSequence s) {
        return nonNull(s) && s.length() > 0;
    }
    
    public static boolean isEmpty(CharSequence s) {
        return isNull(s) || s.length() == 0;
    }
    
    public static String normalizeSpaces(String target) {
        return target.replaceAll("\\s+", " ").trim();
    }
    
    public static String removeWildcards(String target) {
        return target.replaceAll("-+", "");
    }

    public static String[] splitBySpaces(String target) {
        return target.split("\\s+");
    }
    
    public static String removeAllSeparators(String target) {
        return ALL_SEPARATORS_REPLACE.doFor(target);
    }

    public static int countSpaces(String target) {
        // bad code, should use more efficient function here
        // possible dead code
        return target.split("\\s+").length - 1;
    }

    public static List<String> splitBySpacesToList(String target) {
        return new ArrayList<>(asList(target.split("\\s+")));
    }
    
    public static List<String> splitToLines(String multilines) {
        return new ArrayList<>(asList(multilines.split("\\r?\\n")));
    }
    
    public static String joinFromIndex(int start, List<String> list) {
        return join(" ", list.subList(start, list.size()));
    }
    
    public static int indexOfAny(String whereToSearch, String... any) {
        return stream(any)
                .mapToInt(oneOfAny -> whereToSearch.indexOf(oneOfAny))
                .filter(index -> index > -1)
                .findFirst()
                .orElse(-1);
    }
    
    public static boolean isWordsSeparator(char c) {
        return isPathSeparator(c) || isTextSeparator(c);
    }
    
    public static boolean isTextSeparator(char c) {
        return 
                c == '.' ||
                c == ',' ||
                c == ';' ||
                c == ':' ||
                c == ' ' || 
                c == '_' || 
                c == '-' ||
                c == '&' || 
                c == '*' || 
                c == '?' || 
                c == '>' || 
                c == '<' || 
                c == '=' || 
                c == '%';
    }
    
    public static boolean isPathSeparator(char c) {
        return 
                c == '/' || 
                c == '\\';
    }
    
    public static int countWordSeparatorsInBetween(
            String where, int fromInclusive, int toInclusive) {
        int separators = 0;
        
        for (int i = fromInclusive; i <= toInclusive; i++) {
            if ( isWordsSeparator(where.charAt(i)) ) {
                separators++;
            }
        }
        
        return separators;
    }
    
    public static boolean containsWordsSeparator(String target) {
        for (int i = 0; i < target.length(); i++) {
            if ( isWordsSeparator(target.charAt(i))) {
                return true;
            }
        }
        return false;
    }
    
    public static String joining(String... strings) {
        return join("", strings);
    }
    
    public static String joinAll(String separator, Object... objects) {
        Object object;
        
        StringJoiner joiner = new StringJoiner(separator);
        for (int i = 0; i < objects.length; i++) {
            object = objects[i];
            if ( object instanceof Collection ) {
                for (Object nestedObject : ((Collection) object)) {
                    joiner.add(nestedObject.toString());
                }
            } else {
                joiner.add(object.toString());
            }
        }
        
        return joiner.toString();
    }
    
    public static boolean haveEqualLength(String one, String two) {
        return one.length() == two.length();
    }
    
    public static void purge(StringBuilder stringBuilder) {
        stringBuilder.delete(0, stringBuilder.length());
    }
    
    public static void replaceAll(StringBuilder sb, String whatToReplace, String replacement) {
        int index = sb.indexOf(whatToReplace, 0);
        int whatToReplaceLength = whatToReplace.length();
        int replacementLength = replacement.length();
        while ( index >= 0 ) {
            sb.replace(index, index + whatToReplaceLength, replacement);
            index = sb.indexOf(whatToReplace, index + replacementLength);
        }
    }
    
    public static int countOccurences(String where, String what) {
        int count = 0;
        int lastOccurence = where.indexOf(what);
        while ( lastOccurence > -1 ) {
            count++;
            lastOccurence = where.indexOf(what, lastOccurence + 1);
        }
        return count;
    }
    
    public static int countCharMatchesIn(
            String first, int firstFromIncl, int firstToExcl, 
            String second, int secondFromIncl, int secondToExcl) {
        if ( firstFromIncl < 0 ) {
            throw new IllegalArgumentException();
        } 
        if ( secondFromIncl < 0 ) {
            throw new IllegalArgumentException();
        }
        if ( firstFromIncl >= firstToExcl ) {
            throw new IllegalArgumentException();
        }
        if ( secondFromIncl >= secondToExcl ) {
            throw new IllegalArgumentException();
        }
        
        int firstLength = first.length();
        int secondLength = second.length();
        
        if ( firstToExcl > firstLength ) {
            throw new IllegalArgumentException();
        }
        if ( secondToExcl > secondLength ) {
            throw new IllegalArgumentException();
        }
        
        int matches = 0;
        
        char charInFirst;
        char charInSecond;
        
        for (int i1 = firstFromIncl; i1 < firstToExcl && i1 < firstLength; i1++) {
            charInFirst = first.charAt(i1);
            for (int i2 = secondFromIncl; i2 < secondToExcl && i2 < secondLength; i2++) {
                charInSecond = second.charAt(i2);
                if ( charInFirst == charInSecond ) {
                    matches++;
                }
            }
        }
        
        return matches;
    }
    
}
