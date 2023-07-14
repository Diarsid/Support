package diarsid.support.objects.references;

import java.util.function.Function;

import diarsid.support.objects.CommonEnum;
import diarsid.support.objects.references.impl.SimpleEither;

public interface Either<P, S> extends Possible<P> {

    static enum Presence implements CommonEnum<Presence> {
        PRIMARY,
        SECONDARY
    }

    static <P, S> Either<P, S> withPrimary(P primary) {
        return new SimpleEither<>(primary, true);
    }

    static <P, S> Either<P, S> withSecondary(S secondary) {
        return new SimpleEither<>(secondary);
    }

    P primaryOrThrow(Function<S, RuntimeException> exceptionOfSecondary);

    S secondaryOrThrow(Function<P, RuntimeException> exceptionOfSecondary);

    Presence presence();

    S secondaryOrThrow();

    Possible<S> possibleSecondary();
}
