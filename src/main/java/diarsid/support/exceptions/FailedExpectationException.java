package diarsid.support.exceptions;

import static java.lang.String.format;

public final class FailedExpectationException extends RuntimeException {

    public final Object expectation;
    public final Object actual;

    public FailedExpectationException(Object expected, Object actual) {
        super(format("Expected %s[%s] but was %s[%s]",
                expected.getClass().getSimpleName(), expected.toString(),
                actual.getClass().getSimpleName(), actual.toString()));
        this.expectation = expected;
        this.actual = actual;
    }
}
