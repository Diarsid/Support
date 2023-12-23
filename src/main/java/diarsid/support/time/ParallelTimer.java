package diarsid.support.time;

import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.String.valueOf;
import static java.lang.System.currentTimeMillis;
import static java.lang.Thread.currentThread;
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toList;

public class ParallelTimer {

    private final ConcurrentHashMap<Thread, ArrayList<Timing>> timingsByThreads;

    public ParallelTimer() {
        this.timingsByThreads = new ConcurrentHashMap<>();
    }

    public void start() {
        Thread thread = currentThread();

        var threadTimings = this.timingsByThreads.computeIfAbsent(thread, t -> new ArrayList<>());

        this.start(thread.getName() + "." + valueOf(threadTimings.size()));
    }

    public void start(String name) {
        Timing timing = new Timing(name);

        Thread thread = currentThread();

        this.timingsByThreads
                .computeIfAbsent(thread, t -> new ArrayList<>())
                .add(timing);

        timing.start = currentTimeMillis();
    }

    public void stop(String name) {
        long stop = currentTimeMillis();
        Thread thread = currentThread();

        var threadTimings = this.timingsByThreads.computeIfAbsent(thread, t -> new ArrayList<>());

        if ( threadTimings.isEmpty() ) {
            return;
        }

        Timing timing = threadTimings
                .stream()
                .filter(t -> t.name.equals(name))
                .findFirst()
                .orElse(null);

        if ( isNull(timing) ) {
            return;
        }

        timing.millis = stop - timing.start;
    }

    public void stopLast() {
        long stop = currentTimeMillis();
        Thread thread = currentThread();

        var threadTimings = this.timingsByThreads.get(thread);

        if ( isNull(threadTimings) ) {
            return;
        }

        Timing last = threadTimings.getLast();
        last.millis = stop - last.start;
    }

    public List<Timing> timings() {
        return this.timingsByThreads
                .values()
                .stream()
                .flatMap(Collection::stream)
                .sorted()
                .collect(toList());
    }
}
