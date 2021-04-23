package diarsid.support.objects.collections;

import java.util.Collection;

import static java.util.Objects.nonNull;

public class CollectionUtils {

    public static boolean isNotEmpty(Collection c) {
        return nonNull(c) && ! c.isEmpty();
    }

    public static boolean isNonEmpty(Collection c) {
        return nonNull(c) && ! c.isEmpty();
    }

    public static boolean notEmpty(Collection c) {
        return nonNull(c) && ! c.isEmpty();
    }

    public static boolean nonEmpty(Collection c) {
        return nonNull(c) && ! c.isEmpty();
    }
}
