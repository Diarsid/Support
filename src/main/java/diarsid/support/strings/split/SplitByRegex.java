package diarsid.support.strings.split;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

public class SplitByRegex implements Split {

    private final String regex;

    public SplitByRegex(String regex) {
        this.regex = regex;
    }

    @Override
    public List<String> process(String s) {
        return new ArrayList<>(asList(s.split(regex)));
    }
}
