package diarsid.support.strings.split;

import java.util.ArrayList;
import java.util.List;

import diarsid.support.exceptions.UnsupportedLogicException;

public class SplitByChar implements Split {

    private final char c;

    public SplitByChar(char c) {
        this.c = c;
    }

    @Override
    public List<String> process(String s) {
        throw new UnsupportedLogicException();
    }
}
