package diarsid.support.strings;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Arrays.stream;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import static diarsid.support.strings.StringUtils.lower;

public class StringIgnoreCaseUtil {
    
    private StringIgnoreCaseUtil() {
    }
    
    public static String replaceIgnoreCase(String target, String replaceable, String replacement) {
        if ( containsIgnoreCase(target, replaceable) ) {
            int indexOf = lower(target).indexOf(lower(replaceable));
            return target
                    .substring(0, indexOf)
                    .concat(replacement)
                    .concat(target
                            .substring(
                                    indexOf + replaceable.length(), 
                                    target.length()));
        } else {
            return target;
        }
    }
    
    public static String findAnyInIgnoreCase(
            String whereToSearch, Collection<String> searchedAnyOf) {
        if ( 
                nonNull(whereToSearch) && 
                nonNull(searchedAnyOf) && 
                ! whereToSearch.isEmpty() && 
                ! searchedAnyOf.isEmpty() ) {
            return searchedAnyOf
                    .stream()
                    .filter(any -> containsIgnoreCase(whereToSearch, any))
                    .findFirst()
                    .orElse("");
        } else {
            return "";
        }
    }
    
    public static boolean containsAllPartsIgnoreCase(String whereToSearch, List<String> patterns) {
        for (String searchedPart : patterns) {
            if ( ! lower(whereToSearch).contains(lower(searchedPart)) ) {
                return false;
            }
        }
        return true;
    }
    
    public static boolean containsIgnoreCase(String whereToSearch, String searched) {
        if ( isNull(searched) || isNull(whereToSearch) || searched.isEmpty() ) {
            return false;
        } else {
            return lower(whereToSearch).contains(lower(searched));
        }        
    }
    
    public static int indexOfIgnoreCase(String whereToSearch, String searched) {
        if ( isNull(searched) || isNull(whereToSearch) || searched.isEmpty() ) {
            return -1;
        } else {
            return lower(whereToSearch).indexOf(lower(searched));
        }  
    }
    
    public static int lastIndexOfIgnoreCase(String whereToSearch, String searched) {
        if ( isNull(searched) || isNull(whereToSearch) || searched.isEmpty() ) {
            return -1;
        } else {
            return lower(whereToSearch).lastIndexOf(lower(searched));
        }  
    }
    
    public static boolean startsIgnoreCase(String whereToSearch, String start) {
        if ( isNull(whereToSearch) || isNull(start) || start.isEmpty() ) {
            return false;
        } else {
            return lower(whereToSearch).startsWith(lower(start));
        }
    }
    
    public static boolean endsIgnoreCase(String whereToSearch, String end) {
        if ( isNull(whereToSearch) || isNull(end) || end.isEmpty() ) {
            return false;
        } else {
            return lower(whereToSearch).endsWith(lower(end));
        }
    }
    
    public static boolean containsWordInIgnoreCase(
            Collection<String> whereToSearch, String searched) {
        if ( isNull(whereToSearch) || whereToSearch.isEmpty() || isNull(searched) ) {
            return false;
        } else {
            return whereToSearch
                    .stream()
                    .anyMatch(s -> s.equalsIgnoreCase(searched));
        }        
    }
    
    public static boolean containsSnippetIgnoreCase(
            Collection<String> whereToSearch, String searched) {
        if ( isNull(whereToSearch) || whereToSearch.isEmpty() || isNull(searched) ) {
            return false;
        } else {
            return whereToSearch
                    .stream()
                    .anyMatch(s -> containsIgnoreCase(s, searched));
        }        
    }
    
    public static boolean containsIgnoreCaseAnyFragment(
            String whereToSearch, String... searchedSnippets) {
        return stream(searchedSnippets)
                .anyMatch(snippet -> containsIgnoreCase(whereToSearch, snippet));
    }
    
    public static boolean containsIgnoreCaseAnyFragment(
            String whereToSearch, Collection<String> searchedSnippets) {
        return searchedSnippets
                .stream()
                .anyMatch(snippet -> containsIgnoreCase(whereToSearch, snippet));
    }
    
    public static boolean startsWithIgnoreCaseAnyFragment(
            String whereToSearch, String... searchedStarts) {
        return stream(searchedStarts)
                .anyMatch(starting -> startsIgnoreCase(whereToSearch, starting));
    }
    
    public static boolean startsWithIgnoreCaseAnyFragment(
            String whereToSearch, Collection<String> searchedStarts) {
        return searchedStarts
                .stream()
                .anyMatch(starting -> startsIgnoreCase(whereToSearch, starting));
    }
    
    public static boolean endsWithIgnoreCaseAnyFragment(
            String whereToSearch, String... searchedEnds) {
        return stream(searchedEnds)
                .anyMatch(starting -> endsIgnoreCase(whereToSearch, starting));
    }
    
    public static boolean endsWithIgnoreCaseAnyFragment(
            String whereToSearch, Collection<String> searchedEnds) {
        return searchedEnds
                .stream()
                .anyMatch(starting -> endsIgnoreCase(whereToSearch, starting));
    }
    
    public static boolean containsAnySnippetsInAnyWordsIgnoreCase(
            Collection<String> whereToSearch, Collection<String> snippetsToSearch) {
        return whereToSearch
                .stream()
                .anyMatch(stringToSearchIn ->
                        containsIgnoreCaseAnyFragment(stringToSearchIn, snippetsToSearch));
    }
    
    public static int indexOfIgnoreCase(List<String> whereToSearch, String searched) {
        if ( isNull(whereToSearch) || whereToSearch.isEmpty() || isNull(searched) ) {
            return -1;
        } else {
            Optional<String> optionalElement =  whereToSearch
                    .stream()
                    .filter(s -> s.equalsIgnoreCase(searched))
                    .findFirst();
            if ( optionalElement.isPresent() ) {
                return whereToSearch.indexOf(optionalElement.get());
            } else {
                return -1;
            }
        }
    }
    
    public static boolean containsKeyIgnoreCase(Map<String, Object> mapToSearch, String searched) {
        if ( isNull(mapToSearch) || mapToSearch.isEmpty() || isNull(searched) ) {
            return false;
        } else {
            return mapToSearch.keySet().stream()
                    .anyMatch(s -> s.equalsIgnoreCase(searched));
        }        
    }
    
    public static <T> T getIgnoreCase(Map<String, T> mapToSearch, String keyIgnoreCase) {
        if ( isNull(mapToSearch) || mapToSearch.isEmpty() || isNull(keyIgnoreCase) ) {
            return null;
        } else {
            Optional<String> optionalKey = mapToSearch
                    .keySet()
                    .stream()
                    .filter(s -> s.equalsIgnoreCase(keyIgnoreCase))
                    .findFirst();
            if ( optionalKey.isPresent() ) {
                return mapToSearch.get(optionalKey.get());
            } else {
                return null;
            }
        }    
    }
}
