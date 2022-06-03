package diarsid.support.strings.split;

import java.util.List;

import static diarsid.support.strings.StringUtils.splitCamelCase;

public class SplitByCamelCase implements Split {

    private final boolean allowSingleCharSeparation;

    public SplitByCamelCase(boolean allowSingleCharSeparation) {
        this.allowSingleCharSeparation = allowSingleCharSeparation;
    }

    @Override
    public List<String> process(String s) {
        return splitCamelCase(s, allowSingleCharSeparation);
    }
}
