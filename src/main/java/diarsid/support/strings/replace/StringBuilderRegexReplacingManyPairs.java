package diarsid.support.strings.replace;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;

import static diarsid.support.strings.replace.ReusableRegexMatchers.giveMatcherBack;
import static diarsid.support.strings.replace.ReusableRegexMatchers.takeMatcherFor;

class StringBuilderRegexReplacingManyPairs implements SingleReplacing {

    private final List<Pattern> patternsByOrder;
    private final Map<Pattern, String> replacementsByPatterns;

    StringBuilderRegexReplacingManyPairs(List<ReplacePair> replacePairs) {
        this.patternsByOrder = new ArrayList<>();
        this.replacementsByPatterns = new HashMap<>();

        replacePairs.forEach(replacePair -> {
            Pattern pattern = compile(replacePair.target());
            this.patternsByOrder.add(pattern);
            this.replacementsByPatterns.put(pattern, replacePair.replacement());
        });
    }

    @Override
    public void in(StringBuilder stringBuilder) {
        Matcher matcher;
        String replacement;
        boolean alreadyDone = false;

        for ( Pattern pattern : this.patternsByOrder ) {
            if ( alreadyDone ) {
                break;
            }

            matcher = takeMatcherFor(pattern).reset(stringBuilder);

            try {
                int start = 0;
                int end;
                while ( matcher.find(start) ) {
                    start = matcher.start();
                    end = matcher.end();
                    replacement = this.replacementsByPatterns.get(pattern);
                    stringBuilder.replace(start, end, replacement);
                    start = start + replacement.length();
                    matcher.reset();
                    alreadyDone = true;
                }
            } finally {
                giveMatcherBack(matcher);
            }
        }
    }
}
