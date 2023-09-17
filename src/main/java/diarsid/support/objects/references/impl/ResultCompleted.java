package diarsid.support.objects.references.impl;

import java.util.function.Function;

import diarsid.support.objects.references.Result;

public class ResultCompleted<T> extends AbstractReadableNullable<T> implements Result<T> {

    public ResultCompleted(T t) {
        super(t);
    }

    @Override
    public Reason reason() {
        throw new IllegalStateException();
    }

    @Override
    public T orThrow(Function<Reason, ? extends RuntimeException> exceptionCreator) {
        return super.t;
    }
}
