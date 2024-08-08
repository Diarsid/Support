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
import java.util.stream.Collectors;

import diarsid.support.exceptions.UnsupportedLogicException;
import diarsid.support.strings.replace.Replace;

import static java.lang.String.format;
import static java.lang.String.join;
import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.Collections.emptyList;
import static java.util.Locale.ENGLISH;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;

import static diarsid.support.strings.PathUtils.normalizeSeparators;
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

    public static String normalizeDashes(String target) {
        return target.replaceAll("[-]+", "-");
    }

    public static String normalizeUnderscores(String target) {
        String result = target.replaceAll("[_]+", "_");
        if ( result.endsWith("_") ) {
            result = result.substring(0, result.length() - 1);
        }
        if ( result.startsWith("_") ) {
            result = result.substring(1);
        }
        return result;
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
                c == '#' ||
                c == '&' || 
                c == '*' ||
                c == '$' ||
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

    public static boolean containsPathSeparator(String target) {
        for (int i = 0; i < target.length(); i++) {
            if ( isPathSeparator(target.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    public static boolean containsTextSeparator(String target) {
        for (int i = 0; i < target.length(); i++) {
            if ( isTextSeparator(target.charAt(i))) {
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
    
    public static int countOccurrences(String where, String what) {
        int count = 0;
        int lastOccurrence = where.indexOf(what);
        while ( lastOccurrence > -1 ) {
            count++;
            lastOccurrence = where.indexOf(what, lastOccurrence + 1);
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

    public static List<String> splitByTextSeparators(String target) {
        List<String> words = new ArrayList<>();
        int wordBeganIndex = -1;
        boolean wordContinue = false;
        char current;
        String word;
        int last = target.length() - 1;

        for (int i = 0; i < target.length(); i++) {
            current = target.charAt(i);

            if ( isTextSeparator(current) ) {
                if ( wordContinue ) {
                    word = target.substring(wordBeganIndex, i);
                    words.add(word);
                    wordContinue = false;
                }
            }
            else {
                if ( wordContinue ) {
                    if ( i == last ) {
                        word = target.substring(wordBeganIndex);
                        words.add(word);
                    }
                }
                else {
                    wordContinue = true;
                    wordBeganIndex = i;
                }
            }
        }

        return words;
    }

    public static List<String> splitByAnySeparators(String target) {
        List<String> words = new ArrayList<>();
        int wordBeganIndex = -1;
        boolean wordContinue = false;
        char current;
        String word;
        int last = target.length() - 1;

        for (int i = 0; i < target.length(); i++) {
            current = target.charAt(i);

            if ( isTextSeparator(current) || isPathSeparator(current) ) {
                if ( wordContinue ) {
                    word = target.substring(wordBeganIndex, i);
                    words.add(word);
                    wordContinue = false;
                }
            }
            else {
                if ( wordContinue ) {
                    if ( i == last ) {
                        word = target.substring(wordBeganIndex);
                        words.add(word);
                    }
                }
                else {
                    if ( i == last ) {
                        word = target.substring(i);
                        words.add(word);
                    } else {
                        wordContinue = true;
                        wordBeganIndex = i;
                    }
                }
            }
        }

        return words;
    }

    public static int firstTextSeparator(String target) {
        if ( isNull(target) ) {
            throw new IllegalArgumentException();
        }

        if ( target.isEmpty() ) {
            return -1;
        }

        for (int i = 0; i < target.length(); i++) {
            if ( isTextSeparator(target.charAt(i)) ) {
                return i;
            }
        }

        return -1;
    }

    public static int firstTextSeparator(String target, int from) {
        if ( isNull(target) ) {
            throw new IllegalArgumentException();
        }

        if ( target.isEmpty() ) {
            return -1;
        }

        if ( from >= target.length() ) {
            throw new IllegalArgumentException();
        }

        for (int i = 0; i < target.length(); i++) {
            if ( isTextSeparator(target.charAt(i)) ) {
                return i;
            }
        }

        return -1;
    }

    public static String stringify(Object obj) {
        if ( obj instanceof Enum ) {
            return ((Enum) obj).name();
        } else if ( obj instanceof byte[] || obj instanceof Byte[] ) {
            return format("bytes:%s", ((byte[]) obj).length );
        } else if ( obj instanceof Collection ) {
            return stringifyAsCollection(obj);
        } else if ( obj.getClass().isArray() ) {
            return stringifyAsArray(obj);
        } else {
            return obj.toString();
        }
    }

    public static String stringifyAsCollection(Object obj) {
        return ((Collection<Object>) obj)
                .stream()
                .map(StringUtils::stringify)
                .collect(Collectors.joining(", "));
    }

    public static String stringifyAsArray(Object obj) {
        return stream(((Object[]) obj))
                .map(StringUtils::stringify)
                .collect(Collectors.joining(", "));
    }

    public static String replaceFirst(String what, String where, String replace) {
        return replaceFirst(what, where, replace, 0);
    }

    public static String replaceFirst(String what, String where, String replace, int fromIncl) {
        int i = where.indexOf(what, fromIncl);

        if ( i < 0 ) {
            return where;
        }

        StringBuilder whereBuffer = new StringBuilder(where);
        whereBuffer.replace(i, i + what.length(), replace);

        return whereBuffer.toString();
    }

    public static String replaceAllWith(String what, String where, List<String> replaces, boolean checkSizeEquality) {
        return replaceAllWith(what, new StringBuilder(where), replaces, checkSizeEquality);
    }

    public static String replaceAllWith(
            String what, StringBuilder where, List<String> replaces, boolean checkSizeEquality) {
        int i = 0;
        int iReplace = 0;
        String replace;
        while ( true ) {
            i = where.indexOf(what, i + 1);

            if ( i < 0 ) {
                if ( checkSizeEquality && (iReplace != replaces.size()) ) {
                    throw new IllegalArgumentException();
                }
                break;
            }

            replace = replaces.get(iReplace);
            iReplace++;

            where.replace(i, i + what.length(), replace);
        }

        return where.toString();
    }

    public static String replaceAllWith(
            String what, StringBuilder where, int fromIndex, List<String> replaces, boolean checkSizeEquality) {
        int i = fromIndex;
        int iReplace = 0;
        String replace;
        while ( true ) {
            i = where.indexOf(what, i + 1);

            if ( i < 0 ) {
                if ( checkSizeEquality && (iReplace != replaces.size()) ) {
                    throw new IllegalArgumentException();
                }
                break;
            }

            replace = replaces.get(iReplace);
            iReplace++;

            where.replace(i, i + what.length(), replace);
            i = i + replace.length();
        }

        return where.toString();
    }

    public static List<String> splitCamelCase(String string, boolean allowOneCharSeparation) {
        string = string.trim().strip();

        if ( string.isBlank() ) {
            return emptyList();
        }

        if ( string.length() == 1 ) {
            return List.of(string);
        }

        List<String> results = new ArrayList<>();

        final int WORD_NOT_FOUND = -1;

        char c;
        int current;
        int previous;
        int last = string.length() - 1;
        boolean currentIsUpper;
        boolean currentIsDigit;
        boolean previousIsUpper = false;
        boolean previousIsDigit = false;
        int wordStartIncl = 0;
        int wordEndExcl;
        String substring;

        for (int i = 0; i < string.length(); i++) {
            wordEndExcl = WORD_NOT_FOUND;
            current = i;
            c = string.charAt(current);
            currentIsUpper = Character.isUpperCase(c);

            if ( currentIsUpper ) {
                currentIsDigit = false;
            } else {
                currentIsDigit = Character.isDigit(c);
            }

            if (i > 0) {
                if ( current == last ) {
                    wordEndExcl = current + 1;
                }
                else {
                    if ( currentIsUpper ) {
                        if ( ! previousIsUpper ) {
                            wordEndExcl = current;
                        }
                    }
                    else {
                        if ( currentIsDigit ) {
                            if ( ! previousIsDigit ) {
                                wordEndExcl = current;
                            }
                        }
                        else {
                            previous = current - 1;
                            if ( previousIsUpper ) {
                                if ( wordStartIncl < previous ) {
                                    wordEndExcl = previous;
                                }
                            }
                            else if ( previousIsDigit ) {
                                if (wordStartIncl < previous) {
                                    wordEndExcl = previous;
                                }
                            }
                        }
                    }
                }
            }

            if ( wordEndExcl > WORD_NOT_FOUND ) {
                if ( allowOneCharSeparation ) {
                    substring = string.substring(wordStartIncl, wordEndExcl);
                    results.add(substring);
                    wordStartIncl = wordEndExcl;
                }
                else if (wordEndExcl > wordStartIncl + 1) {
                    substring = string.substring(wordStartIncl, wordEndExcl);
                    results.add(substring);
                    wordStartIncl = wordEndExcl;
                }
            }

            previousIsUpper = currentIsUpper;
            previousIsDigit = currentIsDigit;
        }

        return results;
    }

    public static String removeSpecialCharsFrom(String target) {
        StringBuilder sb = new StringBuilder();
        char c;
        for (int i = 0; i < target.length(); i++) {
            c = target.charAt(i);
            if ( isLanguageChar(c) ) {
                sb.append(c);
            }
        }

        return sb.toString();
    }

    public static String removeSpecialCharsFrom(String target, char... excluding) {
        StringBuilder sb = new StringBuilder();
        char c;
        charsLoop: for (int i = 0; i < target.length(); i++) {
            c = target.charAt(i);
            for ( char cExcl : excluding ) {
                if ( c == cExcl ) {
                    sb.append(c);
                    continue charsLoop;
                }
            }
            if ( isLanguageChar(c) ) {
                sb.append(c);
            }
        }

        return sb.toString();
    }

    public static boolean isLanguageChar(char c) {
        return Character.isAlphabetic(c) || Character.isDigit(c);
    }

    public static void main(String[] args) {
        System.out.println(removeSpecialCharsFrom(
                "tolkien's a~b z`s 1234^g (a) [b] {c} Її ъ ش \"x\" <j> ", ' '));
    }
    
}
