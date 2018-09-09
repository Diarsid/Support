/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package diarsid.support.strings.replace;


class StringBuilderStringReplacer implements SingleReplacing {
    
    private final String stringToReplace;
    private final String replacement;
    private final int stringToReplaceLength;
    private final int replacementLength;

    public StringBuilderStringReplacer(String stringToReplace, String replacement) {
        this.stringToReplace = stringToReplace;
        this.replacement = replacement;
        this.stringToReplaceLength = this.stringToReplace.length();
        this.replacementLength = this.replacement.length();
    }
    
    @Override
    public void in(StringBuilder stringBuilder) {
        int index = stringBuilder.indexOf(this.stringToReplace, 0);
        while ( index >= 0 ) {
            stringBuilder.replace(index, index + this.stringToReplaceLength, this.replacement);
            index = stringBuilder.indexOf(this.stringToReplace, index + this.replacementLength);
        }
    }
    
}
