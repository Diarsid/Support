package diarsid.support.exceptions;

public class UnsupportedLogicException extends RuntimeException {

    public UnsupportedLogicException() {
        super();
    }

    public UnsupportedLogicException(String message) {
        super(message);
    }

    public static void throwIt() {
        throw new UnsupportedLogicException();
    }
}
