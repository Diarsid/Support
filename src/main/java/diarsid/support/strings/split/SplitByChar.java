package diarsid.support.strings.split;

import java.util.ArrayList;
import java.util.List;

public class SplitByChar implements Split {

    private final char split;

    public SplitByChar(char c) {
        this.split = c;
    }

    @Override
    public List<String> process(String s) {
        List<String> strings = new ArrayList<>();

        int length = s.length();
        int last = length - 1;

        int nextSplit;
        int next;
        String string;
        char c;
        for ( int i = 0; i < length; i++ ) {
            c = s.charAt(i);
            next = i + 1;

            if ( c == split ) {
                continue;
            }

            nextSplit = s.indexOf(split, next);

            if ( nextSplit < 0 ) {
                if ( i == last ) {
                    string = String.valueOf(c);
                }
                else {
                    string = s.substring(i);
                }
                strings.add(string);
                break;
            }
            else {
                string = s.substring(i, nextSplit);
                strings.add(string);
                i = nextSplit;
            }
        }

        return strings;
    }
}
