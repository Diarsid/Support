package diarsid.support.tests.concurrency;

import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.joining;


public class Steps {

    public static class ConsistencyException extends RuntimeException {

        private final List<Step> allSteps;
        private final List<Step> inconsistenSteps;

        public ConsistencyException(List<Step> allSteps, List<Step> inconsistenSteps) {
            super(format("Following steps are inconsistent: \n%s",
                    inconsistenSteps
                            .stream()
                            .sorted()
                            .map(step -> "   " + step.toString() + " \n")
                            .collect(joining())));
            this.allSteps = allSteps;
            this.inconsistenSteps = inconsistenSteps;
        }
    }

    public static class Step implements Comparable<Step> {

        public final int declared;
        public final int actual;

        public Step(int declared, int actual) {
            this.declared = declared;
            this.actual = actual;
        }

        @Override
        public int compareTo(Step other) {
            return Integer.compare(this.actual, other.actual);
        }

        @Override
        public String toString() {
            return "Step{" +
                    "actual=" + actual +
                    ", declared=" + declared +
                    '}';
        }
    }

    private final List<Step> steps;

    public Steps() {
        this.steps = new ArrayList<>();
    }

    public void step(int declared) {
        if ( declared < 0 ) {
            throw new IllegalArgumentException();
        }
        synchronized ( this.steps ) {
            Step step;

            if ( this.steps.isEmpty() ) {
                step = new Step(declared, 0);
            }
            else {
                int actual = this.steps.size();
                step = new Step(declared, actual);
            }

            this.steps.add(step);
        }
    }

    public void mustBeConsistent() {
        Step step;
        Step prevStep = null;
        int lastDeclared = -1;
        List<Step> inconsistentSteps = new ArrayList<>();
        for (int i = 0; i < this.steps.size(); i++) {
            step = this.steps.get(i);
            if ( step.declared <= lastDeclared ) {
                if ( nonNull(prevStep) && !inconsistentSteps.contains(prevStep) ) {
                    inconsistentSteps.add(prevStep);
                }
                inconsistentSteps.add(step);
            }
            lastDeclared = step.declared;
            prevStep = step;
        }

        if ( inconsistentSteps.size() > 0 ) {
            throw new ConsistencyException(this.steps, inconsistentSteps);
        }
    }
}
