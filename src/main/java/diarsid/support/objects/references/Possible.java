package diarsid.support.objects.references;

import java.util.function.Function;

public interface Possible<T> extends Reference.Writable.Nullable<T> {

    <R> Possible<R> map(Function<T, R> mapper);

    T orOther(Possible<T> otherPossibleT);

    T resetTo(Possible<T> possibleT);

    boolean notEquals(Possible<T> possibleT);
}
