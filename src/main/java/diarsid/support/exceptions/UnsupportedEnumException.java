package diarsid.support.exceptions;

import static java.lang.String.format;

public class UnsupportedEnumException extends UnsupportedLogicException {

    public UnsupportedEnumException(Enum<?> e) {
        super(format("Enum %s element %s is not supported!", e.getClass().getCanonicalName(), e.name()));
    }

    public UnsupportedEnumException(Enum<?> e, String clause) {
        super(format("Enum %s element %s is not supported in %s!", e.getClass().getCanonicalName(), e.name(), clause));
    }
}
