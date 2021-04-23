package diarsid.support.lang;

import static java.lang.Boolean.FALSE;

public class Booleans {

    public static boolean not(boolean b) {
        return ! b;
    }

    public static boolean not(Boolean b) {
        return FALSE.equals(b);
    }

    public static boolean isNot(boolean b) {
        return ! b;
    }

    public static boolean isNot(Boolean b) {
        return FALSE.equals(b);
    }
}
