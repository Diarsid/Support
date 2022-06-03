package diarsid.support.strings.split;

import java.util.List;
import java.util.stream.Collectors;

public class SplitGroup {

    private final List<Split> splits;

    public SplitGroup(List<Split> splits) {
        this.splits = splits;
    }

    public List<String> process(String s) {
        return splits
                .stream()
                .flatMap(split -> split.process(s).stream())
                .collect(Collectors.toList());
    }
}
