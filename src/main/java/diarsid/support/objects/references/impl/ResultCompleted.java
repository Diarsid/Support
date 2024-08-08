package diarsid.support.objects.references.impl;

import java.util.function.Function;

import diarsid.support.objects.references.Result;

import static java.lang.String.format;
import static java.util.Objects.isNull;

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

    @Override
    public Result<T> orDefault(T defaultT) {
        return this;
    }

    @Override
    public <R> Result<R> map(Function<T, R> mapper) {
        try {
            R r = mapper.apply(super.t);
            if ( isNull(r) ) {
                String tString;
                try {
                    tString = super.t.toString();
                }
                catch (Exception e) {
                    tString = format("%s@%s",
                            super.t.getClass().getSimpleName(),
                            super.t.hashCode());
                }
                String message = format("%s.map() applied to %s returns null!",
                        Result.class.getSimpleName(),
                        tString);
                return new ResultEmpty<>(message);
            }
            else {
                return new ResultCompleted<>(r);
            }
        }
        catch (Exception e) {
            return new ResultEmpty<>(e);
        }
    }

    @Override
    public <R> Result<R> flatMap(ResultFunction<T, R> mapper) {
        try {
            Result<R> result = mapper.apply(super.t);
            if ( isNull(result) ) {
                String tString;
                try {
                    tString = super.t.toString();
                }
                catch (Exception e) {
                    tString = format("%s@%s",
                            super.t.getClass().getSimpleName(),
                            super.t.hashCode());
                }
                String message = format("%s.map() applied to %s returns null!",
                        Result.class.getSimpleName(),
                        tString);
                return new ResultEmpty<>(message);
            }
            else {
                return result;
            }
        }
        catch (Exception e) {
            return new ResultEmpty<>(e);
        }
    }
}
