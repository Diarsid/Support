/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package diarsid.support.strings.replace;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

/**
 *
 * @author Diarsid
 */
public class Replace {
    
    private final List<SingleReplacing> replacings;

    Replace() {
        this.replacings = new ArrayList<>();
    }
    
    public static Replace replace() {
        return new Replace();
    }
    
    public Replace stringToString(String stringToReplace, String replacement) {
        this.replacings.add(new StringBuilderStringReplacing(stringToReplace, replacement));
        return this;
    }    
    
    public Replace regexToString(String regexToReplace, String replacement) {
        this.replacings.add(new StringBuilderRegexReplacing(regexToReplace, replacement));
        return this;
    }

    public Replace stringToStringFirstFrom(ReplacePair... replacePairs) {
        this.replacings.add(new StringBuilderStringReplacingManyPairs(asList(replacePairs)));
        return this;
    }

    public Replace regexToStringFirstFrom(ReplacePair... replacePairs) {
        this.replacings.add(new StringBuilderRegexReplacingManyPairs(asList(replacePairs)));
        return this;
    }
    
    public void doIn(StringBuilder stringBuilder) {
        int replacingsQty = this.replacings.size();
        
        if ( replacingsQty == 0 ) {
            return;
        } else if ( replacingsQty == 1 ) {
            this.replacings.get(0).in(stringBuilder);
            return;
        }
        
        SingleReplacing replacing;
        for (int i = 0; i < this.replacings.size(); i++) {
            replacing = this.replacings.get(i);
            replacing.in(stringBuilder);
        }
    }
    
    public String doFor(String string) {
        StringBuilder builder = new StringBuilder(string);
        this.doIn(builder);
        return builder.toString();
    }
}
