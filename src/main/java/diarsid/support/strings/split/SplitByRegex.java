package diarsid.support.strings.split;

import java.util.List;

import diarsid.support.exceptions.UnsupportedLogicException;

public class SplitByRegex implements Split {

    public SplitByRegex() {
    }

    @Override
    public List<String> process(String s) {
        throw new UnsupportedLogicException();
    }
}
