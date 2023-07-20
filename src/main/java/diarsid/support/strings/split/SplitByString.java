package diarsid.support.strings.split;

import java.util.ArrayList;
import java.util.List;

public class SplitByString implements Split {

    private final String split;
    private final int splitLength;

    public SplitByString(String string) {
        this.split = string;
        this.splitLength = split.length();
    }

    @Override
    public List<String> process(String s) {
        List<String> strings = new ArrayList<>();

        int nextSplit = s.indexOf(split);

        if ( nextSplit < 0 ) {
            strings.add(s);
            return strings;
        }


        int from;
        if ( nextSplit == 0 ) {
            from = splitLength;
            nextSplit = s.indexOf(split, from);
        }
        else {
            from = 0;
        }

        String string;
        int lastSplit;
        do {
            if ( from < nextSplit ) {
                string = s.substring(from, nextSplit);
                strings.add(string);
            }

            lastSplit = nextSplit;
            nextSplit = s.indexOf(split, lastSplit + splitLength);
            from = lastSplit + splitLength;
        } while ( nextSplit > -1 );

        if ( from < s.length()-1 ) {
            string = s.substring(from);
            strings.add(string);
        }

        return strings;
    }
}
