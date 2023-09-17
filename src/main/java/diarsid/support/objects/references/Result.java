package diarsid.support.objects.references;

import java.util.function.Consumer;
import java.util.function.Function;

import diarsid.support.objects.references.impl.ResultCompleted;
import diarsid.support.objects.references.impl.ResultEmpty;
import diarsid.support.objects.references.impl.ResultVoid;
import diarsid.support.objects.references.impl.SimpleEither;
import diarsid.support.objects.references.impl.SimplePossible;

public interface Result<T> extends Reference.Readable.Nullable<T> {

    static <T> Result<T> empty(Object reason) {
        return new ResultEmpty<>(reason);
    }

    static <T> Result<T> empty(Throwable reason) {
        return new ResultEmpty<>(reason);
    }

    static <T> Result<T> completed(T t) {
        return new ResultCompleted<>(t);
    }

    interface Reason {

        Object subject();

        default boolean isInstanceOf(Class<?> type) {
            return type.isInstance(this.subject());
        }

        default boolean isExceptional() {
            return this.isInstanceOf(Throwable.class);
        }

        default boolean isNotExceptional() {
            return ! this.isInstanceOf(Throwable.class);
        }

        default boolean is(Class<?> type) {
            return type.equals(this.subject().getClass());
        }

        default <R> R subjectAs(Class<R> type) {
            return type.cast(this.subject());
        }

    }

    interface Void {

        static Result.Void success() {
            return ResultVoid.Success.SINGLETON;
        }

        static Result.Void fail(Object reason) {
            return new ResultVoid.Fail(reason);
        }

        static Result.Void fail(Throwable reason) {
            return new ResultVoid.Fail(reason);
        }

        boolean isSuccess();

        boolean isFail();

        Reason reason();

    }

    Reason reason();

    T orThrow(Function<Reason, ? extends RuntimeException> exceptionCreator);

    default void doWhenEmpty(Consumer<Reason> doWithReason) {
        if ( this.isEmpty() ) {
            doWithReason.accept(this.reason());
        }
    }

    default void doWhenCompleted(Consumer<T> doWithResult) {
        if ( this.isPresent() ) {
            doWithResult.accept(this.get());
        }
    }

    default Possible<T> asPossible() {
        if ( this.isPresent() ) {
            return new SimplePossible<>(this.get());
        }
        else {
            return new SimplePossible<>();
        }
    }

    default Either<T, Object> asEither() {
        if ( this.isPresent() ) {
            return new SimpleEither<>(this.get(), true);
        }
        else {
            return new SimpleEither<>(this.reason());
        }
    }
}
