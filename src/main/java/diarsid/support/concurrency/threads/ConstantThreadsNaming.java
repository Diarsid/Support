package diarsid.support.concurrency.threads;

import java.util.Optional;

public class ConstantThreadsNaming implements ThreadsNaming {

    private final String name;

    public ConstantThreadsNaming(String name) {
        this.name = name;
    }

    @Override
    public Optional<String> nameFormat() {
        return Optional.empty();
    }

    @Override
    public String nextName() {
        return name;
    }
}
