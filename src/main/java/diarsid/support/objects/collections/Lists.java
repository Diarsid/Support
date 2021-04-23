package diarsid.support.objects.collections;

import java.util.List;

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
    public static boolean containsCommonItem(List a, List b) {
        if ( a.isEmpty() || b.isEmpty() ) {
            return false;
        }

        return a.stream().anyMatch(b::contains);
    }
}
