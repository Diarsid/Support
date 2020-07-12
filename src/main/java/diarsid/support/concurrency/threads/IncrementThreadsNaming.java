package diarsid.support.concurrency.threads;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import static java.lang.String.format;
import static java.util.Objects.nonNull;

public class IncrementThreadsNaming implements ThreadsNaming {

    private final static Map<String, AtomicLong> COUNTERS_BY_FORMAT;

    static {
        COUNTERS_BY_FORMAT = new ConcurrentHashMap<>();
    }

    private final String formatTemplate;
    private final AtomicLong counter;

    public IncrementThreadsNaming(String formatTemplate) {
        this.formatTemplate = formatTemplate;
        AtomicLong counterToUse;
        synchronized (COUNTERS_BY_FORMAT) {
            AtomicLong existedCounter = COUNTERS_BY_FORMAT.get(formatTemplate);
            if (nonNull(existedCounter)) {
                counterToUse = existedCounter;
            } else {
                counterToUse = new AtomicLong(0);
                COUNTERS_BY_FORMAT.put(formatTemplate, counterToUse);
            }
        }
        this.counter = counterToUse;
    }

    @Override
    public String nameFormat() {
        return formatTemplate;
    }

    @Override
    public String nextName() {
        return format(formatTemplate, counter.incrementAndGet());
    }
}
