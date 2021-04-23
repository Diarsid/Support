package diarsid.support.strings.replace;

import diarsid.support.objects.Pair;

public class ReplacePair extends Pair<String, String> {

    public ReplacePair(String target, String replacement) {
        super(target, replacement);
    }

    public String target() {
        return super.first();
    }

    public String replacement() {
        return super.second();
    }
}
