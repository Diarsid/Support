package diarsid.support.strings;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Objects.isNull;

public class StringCacheForRepeatedPrefixSuffix {

    private final String prefix;
    private final String suffix;
    private final String string;
    private final Map<Integer, String> cache;

    public StringCacheForRepeatedPrefixSuffix(String prefix, String string, String suffix) {
        this.prefix = prefix;
        this.suffix = suffix;
        this.string = string;
        this.cache = new ConcurrentHashMap<>();

        String appendable = string;
        this.cache.put(1, string);
        for (int i = 2; i <= 100; i++) {
            appendable = appendable + string;
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
            result = this.prefix + this.string.repeat(qty) + this.suffix;
            this.cache.put(qty, result);
        }

        return result;
    }
}
