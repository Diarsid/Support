/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package diarsid.support.strings.replace;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static diarsid.support.log.Logging.logFor;

import diarsid.support.metainfo.IntendedToBeThreadSafe;

/**
 *
 * @author Diarsid
 */
@IntendedToBeThreadSafe
class ReusableRegexMatchers {
    
    private static final Map<String, Queue<Matcher>> MATCHERS_BY_PATTERNS;
    private static final StringBuilder EMPTY_STRING_BUILDER;
    
    static {
        MATCHERS_BY_PATTERNS = new HashMap<>();
        EMPTY_STRING_BUILDER = new StringBuilder();
    }
    
    private ReusableRegexMatchers() {
        
    }
    
    static Matcher takeMatcherFor(Pattern pattern) {
        Matcher matcher;
        Queue<Matcher> matchers;
        
        synchronized ( MATCHERS_BY_PATTERNS ) {
            matchers = MATCHERS_BY_PATTERNS.get(pattern.pattern());
            if ( matchers == null ) {
                matcher = pattern.matcher(EMPTY_STRING_BUILDER);
                Queue<Matcher> newMatchers = new ArrayDeque<>();
                logFor(ReusableRegexMatchers.class).info(
                        "Thread safe Matcher pool created for regex pattern: " + pattern.pattern());
                MATCHERS_BY_PATTERNS.put(pattern.pattern(), newMatchers);
            } else {
                if ( matchers.isEmpty() ) {
                    matcher = pattern.matcher(EMPTY_STRING_BUILDER);
                } else {
                    matcher = matchers.peek();
                }
            }
        }
        
        return matcher;
    }
    
    static void giveMatcherBack(Matcher matcher) {
        Queue<Matcher> matchers;
        
        synchronized ( MATCHERS_BY_PATTERNS ) {
            matchers = MATCHERS_BY_PATTERNS.get(matcher.pattern().pattern());
            if ( matchers == null ) {
                throw new ReplacingException(
                        "Cannot find matchers queue for pattern " + matcher.pattern().pattern());
            }
            matchers.offer(matcher.reset());
        }
    }
    
}
