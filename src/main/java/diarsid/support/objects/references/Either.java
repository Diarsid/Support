package diarsid.support.objects.references;

import diarsid.support.objects.CommonEnum;

public interface Either<P, S> extends Possible<P> {

    public static enum Presence implements CommonEnum<Presence> {
        PRIMARY,
        SECONDARY
    }

    Presence presence();

    S secondaryOrThrow();

    Possible<S> possibleSecondary();
}
