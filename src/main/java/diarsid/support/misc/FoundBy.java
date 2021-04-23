package diarsid.support.misc;

import java.util.List;

import static java.util.Collections.emptyList;

public class FoundBy<C, R> {

    private final List<R> results;
    private final C notMatchCriteria;

    public FoundBy(List<R> results, C notMatchCriteria) {
        this.results = results;
        this.notMatchCriteria = notMatchCriteria;
    }

    public FoundBy(List<R> results) {
        this.results = results;
        this.notMatchCriteria = null;
    }

    public FoundBy(C notMatchCriteria) {
        this.results = emptyList();
        this.notMatchCriteria = notMatchCriteria;
    }

    boolean hasAnyResults() {
        return ! this.results.isEmpty();
    }

    boolean hasNoResults() {
        return this.results.isEmpty();
    }

    public List<R> results() {
        return this.results;
    }

    public C notMatchCriteria() {
        return this.notMatchCriteria;
    }
}
