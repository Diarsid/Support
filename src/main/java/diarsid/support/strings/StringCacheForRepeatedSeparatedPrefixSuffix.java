package diarsid.support.strings;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Objects.isNull;

public class StringCacheForRepeatedSeparatedPrefixSuffix {

    private final String prefix;
    private final String suffix;
    private final String string;
    private final String separator;
    private final Map<Integer, String> cache;

    public StringCacheForRepeatedSeparatedPrefixSuffix(String prefix, String string, String separator, String suffix) {
        this.prefix = prefix;
        this.suffix = suffix;
        this.string = string;
        this.separator = separator;
        this.cache = new ConcurrentHashMap<>();

        String appendable = string;
        this.cache.put(1, this.prefix + appendable + this.suffix);
        for (int i = 2; i <= 100; i++) {
            appendable = appendable + separator + string;
            this.cache.put(i, this.prefix + appendable + this.suffix);
        }
    }

    public String getFor(Object[] arr) {
        return this.getFor(arr.length);
    }

    public String getFor(Collection collection) {
        return this.getFor(collection.size());
    }

    public String getFor(int qty) {
        String result = this.cache.get(qty);

        if ( isNull(result) ) {
            String repeatedSeparatedString = this.createRepeatedSeparatedStringFor(qty);
            result = this.prefix + repeatedSeparatedString + this.suffix;
            this.cache.put(qty, result);
        }

        return result;
    }

    private String createRepeatedSeparatedStringFor(int qty) {
        return (string + separator).repeat(qty - 1) + string;
    }
}
