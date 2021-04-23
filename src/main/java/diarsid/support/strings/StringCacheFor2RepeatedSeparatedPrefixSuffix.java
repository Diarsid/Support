package diarsid.support.strings;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.isNull;

public class StringCacheFor2RepeatedSeparatedPrefixSuffix {

    private final String prefix;
    private final String string1;
    private final String separator1;
    private final String between1and2;
    private final String string2;
    private final String separator2;
    private final String suffix;
    private final Map<Integer, Map<Integer, String>> cache;

    public StringCacheFor2RepeatedSeparatedPrefixSuffix(
            String prefix,
            String string1, String separator1,
            String between1and2,
            String string2, String separator2,
            String suffix) {
        this.prefix = prefix;
        this.string1 = string1;
        this.separator1 = separator1;
        this.between1and2 = between1and2;
        this.string2 = string2;
        this.separator2 = separator2;
        this.suffix = suffix;
        this.cache = new HashMap<>();
    }

    public String getFor(Object[] arr1, Object[] arr2) {
        return this.getFor(arr1.length, arr2.length);
    }

    public String getFor(Collection col1, Collection col2) {
        return this.getFor(col1.size(), col2.size());
    }

    public String getFor(int qty1, int qty2) {
        Map<Integer, String> repeatedSeparatedStringsMap = this.cache.get(qty1);

        String target;
        if ( isNull(repeatedSeparatedStringsMap) ) {
            repeatedSeparatedStringsMap = new HashMap<>();
            target = this.createRepeatedSeparatedStringFor(qty1, qty2);
            repeatedSeparatedStringsMap.put(qty2, target);
            this.cache.put(qty1, repeatedSeparatedStringsMap);
        }
        else {
            target = repeatedSeparatedStringsMap.get(qty2);

            if ( isNull(target) ) {
                target = this.createRepeatedSeparatedStringFor(qty1, qty2);
                repeatedSeparatedStringsMap.put(qty2, target);
            }
        }

        return target;
    }

    private String createRepeatedSeparatedStringFor(int qty1, int qty2) {
        return
                prefix +
                (string1 + separator1).repeat(qty1 - 1) + string1 +
                between1and2 +
                (string2 + separator2).repeat(qty2 - 1) + string2 +
                suffix;
    }
}
