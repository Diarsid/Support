package diarsid.support.strings;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Objects.isNull;

public class StringCacheForRepeatedSeparated {

    private final String string;
    private final String separator;
    private final Map<Integer, String> cache;

    public StringCacheForRepeatedSeparated(String string, String separator) {
        this.string = string;
        this.separator = separator;
        this.cache = new ConcurrentHashMap<>();

        String appendable = string;
        this.cache.put(1, string);
        for (int i = 2; i <= 100; i++) {
            appendable = appendable + separator + string;
            this.cache.put(i, appendable);
        }
    }

    public String getFor(Object[] arr) {
        return this.getFor(arr.length);
    }

    public String getFor(Collection collection) {
        return this.getFor(collection.size());
    }

    public String getFor(int qty) {
        String repeatedSeparatedString = this.cache.get(qty);

        if ( isNull(repeatedSeparatedString) ) {
            repeatedSeparatedString = this.createRepeatedSeparatedStringFor(qty);
            this.cache.put(qty, repeatedSeparatedString);
        }

        return repeatedSeparatedString;
    }

    private String createRepeatedSeparatedStringFor(int qty) {
        return (string + separator).repeat(qty - 1) + string;
    }
}
