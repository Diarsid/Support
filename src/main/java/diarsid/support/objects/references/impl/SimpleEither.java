package diarsid.support.objects.references.impl;

import java.util.Objects;
import java.util.function.Function;

import diarsid.support.objects.references.Either;
import diarsid.support.objects.references.Possible;

import static java.util.Objects.isNull;

import static diarsid.support.objects.references.Either.Presence.PRIMARY;
import static diarsid.support.objects.references.Either.Presence.SECONDARY;

public final class SimpleEither<P, S> extends SimplePossible<P> implements Either<P, S> {

    public final S secondary;
    public final Presence presence;

    public SimpleEither(P primary, boolean b) {
        super(primary);
        this.presence = PRIMARY;
        this.secondary = null;
    }

    public SimpleEither(S secondary) {
        super();
        this.presence = SECONDARY;
        this.secondary = secondary;
    }

    @Override
    public P primaryOrThrow(Function<S, RuntimeException> exceptionOfSecondary) {
        if ( this.presence.is(PRIMARY) ) {
            return super.t;
        }
        else {
            throw exceptionOfSecondary.apply(secondary);
        }
    }

    @Override
    public S secondaryOrThrow(Function<P, RuntimeException> exceptionOfSecondary) {
        if ( this.presence.is(SECONDARY) ) {
            return this.secondary;
        }
        else {
            throw exceptionOfSecondary.apply(super.t);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SimpleEither)) return false;
        if (!super.equals(o)) return false;
        SimpleEither<?, ?> simpleEither = (SimpleEither<?, ?>) o;
        return Objects.equals(secondary, simpleEither.secondary) &&
                presence == simpleEither.presence;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), secondary, presence);
    }

    @Override
    public Presence presence() {
        return this.presence;
    }

    @Override
    public S secondaryOrThrow() {
        if ( isNull(this.secondary) ) {
            throw new NullPointerException();
        }

        return this.secondary;
    }

    @Override
    public Possible<S> possibleSecondary() {
        return new SimplePossible<>(this.secondary);
    }
}
