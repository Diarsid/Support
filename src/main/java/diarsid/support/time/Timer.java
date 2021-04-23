package diarsid.support.time;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import diarsid.support.objects.StatefulClearable;

import static java.lang.System.currentTimeMillis;

import static diarsid.support.time.TimeSupport.millisFormat;

public class Timer implements StatefulClearable {

    public static class Timing {

        private final String name;
        private long start;
        private long millis;

        private Timing(String name) {
            this.name = name;
            this.millis = currentTimeMillis();
        }

        public String name() {
            return name;
        }

        public long millis() {
            return millis;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Timing)) return false;
            Timing timing = (Timing) o;
            return start == timing.start &&
                    millis == timing.millis &&
                    name.equals(timing.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, start, millis);
        }

        @Override
        public String toString() {
            return "Timing[" +
                    "'" + name + '\'' +
                    " " + millisFormat(millis) +
                    "]";
        }


    }

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
        last.start = currentTimeMillis();
    }

    public void start(String name) {
        last = new Timing(name);
        last.start = currentTimeMillis();
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
        last.start = currentTimeMillis();
    }

    public void stopAndStart(String name) {
        last.millis = currentTimeMillis() - last.start;
        total = total + last.millis;
        timings.add(last);
        last = new Timing(name);
        last.start = currentTimeMillis();
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
