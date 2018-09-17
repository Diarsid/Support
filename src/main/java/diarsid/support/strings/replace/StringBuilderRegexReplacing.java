/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package diarsid.support.strings.replace;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import diarsid.metainfo.ImpliedToBeThreadSafe;

import static java.util.regex.Pattern.compile;

import static diarsid.support.strings.replace.ReusableRegexMatchers.giveBack;
import static diarsid.support.strings.replace.ReusableRegexMatchers.takeFor;

/**
 *
 * @author Diarsid
 */
@ImpliedToBeThreadSafe
class StringBuilderRegexReplacing implements SingleReplacing {
    
    private final Pattern pattern;
    private final String replacement;
    private final int replacementLength;

    StringBuilderRegexReplacing(String regexToReplace, String replacement) {
        this.pattern = compile(regexToReplace);
        this.replacement = replacement;
        this.replacementLength = this.replacement.length();
    }
    
    @Override
    public void in(StringBuilder stringBuilder) {
        Matcher matcher = takeFor(this.pattern).reset(stringBuilder);
        
        try {
            int start = 0;
            int end;
            while ( matcher.find(start) ) {
                start = matcher.start();
                end = matcher.end();
                stringBuilder.replace(start, end, this.replacement);
                start = start + this.replacementLength;
                matcher.reset();
            }
        } finally {
            giveBack(matcher);
        }    
    }
    
}
