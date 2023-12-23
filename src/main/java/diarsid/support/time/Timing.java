package diarsid.support.time;

import java.util.Objects;

import static java.lang.System.currentTimeMillis;

import static diarsid.support.time.TimeSupport.millisFormat;

public class Timing implements Comparable<Timing> {

    public final String name;
    public long start;
    public long millis;

    Timing(String name) {
        this.name = name;
        this.start = currentTimeMillis();
        this.millis = 0;
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

    @Override
    public int compareTo(Timing other) {
        return Long.compare(this.start, other.start);
    }
}
