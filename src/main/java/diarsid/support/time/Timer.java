package diarsid.support.time;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import diarsid.support.objects.StatefulClearable;

import static java.lang.System.currentTimeMillis;

public class Timer implements StatefulClearable {

    private final AtomicInteger counter;
    private final List<Timing> timings;
    private Timing last;
    private long total;

    public Timer() {
        this.counter = new AtomicInteger();
        this.timings = new ArrayList<>();
    }

    public void start() {
        last = new Timing(String.valueOf(counter.getAndIncrement()));
    }

    public void start(String name) {
        last = new Timing(name);
    }

    public void stop() {
        last.millis = currentTimeMillis() - last.start;
        total = total + last.millis;
        timings.add(last);
    }

    public void stopAndStart() {
        last.millis = currentTimeMillis() - last.start;
        total = total + last.millis;
        timings.add(last);
        last = new Timing(String.valueOf(counter.getAndIncrement()));
    }

    public void stopAndStart(String name) {
        last.millis = currentTimeMillis() - last.start;
        total = total + last.millis;
        timings.add(last);
        last = new Timing(name);
    }

    public List<Timing> timings() {
        return this.timings;
    }

    public Timing last() {
        return this.last;
    }

    public long totalMillis() {
        return this.total;
    }

    @Override
    public void clear() {
        this.counter.set(0);
        this.timings.clear();
        this.last = null;
        this.total = 0;
    }
}
