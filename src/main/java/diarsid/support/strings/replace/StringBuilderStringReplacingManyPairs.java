package diarsid.support.strings.replace;

import java.util.List;

class StringBuilderStringReplacingManyPairs implements SingleReplacing {

    private final List<ReplacePair> replacePairs;

    StringBuilderStringReplacingManyPairs(List<ReplacePair> replacePairs) {
        this.replacePairs = replacePairs;
    }

    @Override
    public void in(StringBuilder stringBuilder) {
        for ( ReplacePair replacePair : this.replacePairs ) {
            int index = stringBuilder.indexOf(replacePair.target(), 0);
            while ( index >= 0 ) {
                stringBuilder.replace(index, index + replacePair.target().length(), replacePair.replacement());
                index = stringBuilder.indexOf(replacePair.target(), index + replacePair.replacement().length());
            }
        }
    }
}
