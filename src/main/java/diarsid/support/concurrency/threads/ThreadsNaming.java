package diarsid.support.concurrency.threads;

import java.util.Optional;

public interface ThreadsNaming {

    Optional<String> nameFormat();

    String nextName();
}
