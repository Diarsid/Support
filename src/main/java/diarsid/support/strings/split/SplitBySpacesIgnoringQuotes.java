package diarsid.support.strings.split;

import java.util.ArrayList;
import java.util.List;

public class SplitBySpacesIgnoringQuotes implements Split {

    private static final char SPACE = ' ';
    private static final char SINGLE_QUOTE = '\'';
    private static final char DOUBLE_QUOTE = '"';
    private static final char NO_QUOTE = '_';

    @Override
    public List<String> process(String s) {
        List<String> strings = new ArrayList<>();

        final int length = s.length();
        final int last = length - 1;

        if ( length == 0 ) {
            return strings;
        }
        else if ( length == 1 ) {
            if ( ! s.equals(" ") ) {
                strings.add(s);
            }
            return strings;
        }

        char c;
        String string;
        char quote;
        int nextQuote;
        int nextSpace;
        int next;
        for ( int i = 0; i < length; i++ ) {
            c = s.charAt(i);
            next = i + 1;

            if ( c == SPACE ) {
                continue;
            }

            if ( c == SINGLE_QUOTE ) {
                quote = SINGLE_QUOTE;
            }
            else if ( c == DOUBLE_QUOTE ) {
                quote = DOUBLE_QUOTE;
            }
            else {
                quote = NO_QUOTE;
            }

            if ( quote == NO_QUOTE) {
                nextSpace = s.indexOf(SPACE, next);
                if ( nextSpace < 0 ) {
                    string = s.substring(i);
                    strings.add(string);
                    break;
                }
                else if ( nextSpace == next ) {
                    i++;
                }
                else {
                    string = s.substring(i, nextSpace);
                    strings.add(string);
                    i = nextSpace;
                }
            }
            else {
                nextQuote = s.indexOf(quote, next);

                if ( nextQuote < 0 ) {
                    nextSpace = s.indexOf(SPACE, next);
                    if ( nextSpace < 0 ) {
                        string = s.substring(next);
                        strings.add(string);
                        break;
                    }
                    else if ( nextSpace == next ) {
                        string = String.valueOf(quote);
                        strings.add(string);
                        i = next;
                    }
                    else {
                        string = s.substring(i, nextSpace);
                        strings.add(string);
                        i = nextSpace;
                    }
                }
                else {
                    string = s.substring(next, nextQuote);
                    strings.add(string);
                    i = nextQuote;
                }
            }
        }

        return strings;
    }
}
