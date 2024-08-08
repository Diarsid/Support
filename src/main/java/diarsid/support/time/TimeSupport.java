package diarsid.support.time;

import java.time.Duration;

import static java.lang.System.currentTimeMillis;
import static java.util.Objects.nonNull;

public class TimeSupport {

    private static final ThreadLocal<Long> UNNAMED_MILLIS;

    static {
        UNNAMED_MILLIS = new ThreadLocal<>();
    }

    private TimeSupport() {}

    public static long timeMillisAfter(long start) {
        return currentTimeMillis() - start;
    }

    public static void start() {
        UNNAMED_MILLIS.set(currentTimeMillis());
    }

    public static long stop() {
        long now = currentTimeMillis();
        Long start = UNNAMED_MILLIS.get();

        if ( nonNull(start) ) {
            return now - start;
        }
        else {
            throw new IllegalStateException(
                    TimeSupport.class.getSimpleName() + ".start() must be called prior to .stop()!");
        }
    }

    public static final int MILLIS_IN_MINUTE = 60_000;
    public static final int MILLIS_IN_HOUR = MILLIS_IN_MINUTE * 60;
    public static final int MILLIS_IN_DAY = MILLIS_IN_HOUR * 24;

    public static String millisFormat(long millis) {
        if ( millis < 1_000 ) {
            return "" + millis + "ms";
        } else if ( millis < MILLIS_IN_MINUTE ) {
            long seconds = millis / 1_000;
            long millisRemnant = millis % 1_000;
            return "" + seconds + "s " + millisRemnant + "ms";
        } else if ( millis < MILLIS_IN_DAY ) {
            long minutes = millis / MILLIS_IN_MINUTE;
            long millisRemnantAfterMinutes = millis % MILLIS_IN_MINUTE;
            long seconds = millisRemnantAfterMinutes / 1_000;
            long millisRemnant = millisRemnantAfterMinutes % 1_000;
            return "" + minutes + "m " + seconds + "s " + millisRemnant + "ms";
        }
        else {
            long days = millis / MILLIS_IN_DAY;
            long millisRemnantAfterDays = millis % MILLIS_IN_DAY;
            long minutes = millisRemnantAfterDays / MILLIS_IN_MINUTE;
            long millisRemnantAfterMinutes = millisRemnantAfterDays % MILLIS_IN_MINUTE;
            long seconds = millisRemnantAfterMinutes / 1_000;
            long millisRemnant = millisRemnantAfterMinutes % 1_000;
            return "" + days + "d " + minutes + "m " + seconds + "s " + millisRemnant + "ms";
        }
    }

    public static String durationFormat(Duration duration) {
        return millisFormat(duration.toMillis());
    }

    public static void main(String[] args) {
        System.out.println(millisFormat(123_555));
    }
}
