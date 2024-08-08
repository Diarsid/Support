package diarsid.support.objects.collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Lists {

    public static <T> T lastFrom(List<T> list) {
        if ( list.isEmpty() ) {
            return null;
        }
        else {
            return list.get(list.size() - 1);
        }
    }

    public static ListInsertion insertAfterEachElement(Object... insert) {
        return new ListInsertion(insert);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static boolean containsSameElements(Collection a, Collection b) {
        if ( a.isEmpty() || b.isEmpty() ) {
            return false;
        }

        return a.containsAll(b) && b.containsAll(a);
    }

    public static void fillNulls(List<?> list, int size) {
        for ( int i = 0; i < size; i++) {
            list.add(null);
        }
    }

    public static <T> List<T> diff(List<T> a, List<T> b) {
        List<T> diff = new ArrayList<>();

        if ( a.isEmpty() ) {
            if ( b.isEmpty() ) {
                return diff;
            }
            else {
                diff.addAll(b);
                return diff;
            }
        }
        else {
            if ( b.isEmpty() ) {
                diff.addAll(a);
                return diff;
            }
        }


        HashMap<T, AtomicInteger> occurrence = new HashMap<>();

        for ( T t : a ) {
            occurrence
                    .computeIfAbsent(t, (newT) -> new AtomicInteger(0))
                    .incrementAndGet();
        }

        for ( T t : b ) {
            occurrence
                    .computeIfAbsent(t, (newT) -> new AtomicInteger(0))
                    .incrementAndGet();
        }

        occurrence.forEach((t, count) -> {
            if ( count.get() < 2 ) {
                diff.add(t);
            }
        });

        return diff;
    }
}
