package diarsid.support.objects;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

import static java.util.Objects.isNull;

import static diarsid.support.objects.Either.Side.LEFT;
import static diarsid.support.objects.Either.Side.RIGHT;

public final class Either<L, R> {

    public static enum Side implements CommonEnum<Side> {
        LEFT,
        RIGHT
    }

    public final L left;
    public final R right;
    public final Side side;

    private Either(L left, R right) {
        if ( isNull(left) == isNull(right) ) {
            throw new IllegalArgumentException();
        }

        if ( isNull(left) ) {
            this.side = RIGHT;
        }
        else {
            this.side = LEFT;
        }

        this.left = left;
        this.right = right;
    }

    public static <L, R> Either<L, R> leftOfEither(L left) {
        return new Either<>(left, null);
    }

    public static <L, R> Either<L, R> rightOfEither(R right) {
        return new Either<>(null, right);
    }

    public static <L, R> Either<L, R> anyOfEither(L left, R right) {
        return new Either<>(left, right);
    }

    public L leftOrThrow(Function<R, RuntimeException> exceptionOfRight) {
        if ( side == LEFT ) {
            return left;
        }
        else {
            throw exceptionOfRight.apply(right);
        }
    }

    public R rightOrThrow(Function<L, RuntimeException> exceptionOfLeft) {
        if ( side == RIGHT ) {
            return right;
        }
        else {
            throw exceptionOfLeft.apply(left);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Either)) return false;
        Either<?, ?> either = (Either<?, ?>) o;
        return Objects.equals(left, either.left) &&
                Objects.equals(right, either.right) &&
                side == either.side;
    }

    @Override
    public int hashCode() {
        return Objects.hash(left, right, side);
    }

}
