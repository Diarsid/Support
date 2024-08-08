package diarsid.support.concurrency;

import diarsid.support.objects.CommonEnum;

public enum ConcurrencyMode implements CommonEnum<ConcurrencyMode> {

    SEQUENTIAL,
    PARALLEL;

    public static final ConcurrencyMode DEFAULT = SEQUENTIAL;
}
