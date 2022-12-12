package diarsid.support.objects.references.impl;

import diarsid.support.objects.references.Result;

public class ResultCompleted<T> extends AbstractReadableNullable<T> implements Result<T> {

    public ResultCompleted(T t) {
        super(t);
    }

    @Override
    public Reason reason() {
        throw new IllegalStateException();
    }
}
