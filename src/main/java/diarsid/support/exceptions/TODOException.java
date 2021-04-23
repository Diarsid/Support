package diarsid.support.exceptions;

public class TODOException extends UnsupportedLogicException {

    public static void throwIt() {
        throw new TODOException();
    }
}
