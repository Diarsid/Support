package diarsid.support.concurrency.threads;

import java.util.concurrent.ThreadFactory;

public class IncrementNamedThreadFactory extends NamedThreadFactory {

    public IncrementNamedThreadFactory(String format) {
        super(new IncrementThreadsNaming(format));
    }

    public IncrementNamedThreadFactory(String format, ThreadFactory threadsWrappedFactory) {
        super(new IncrementThreadsNaming(format), threadsWrappedFactory);
    }
}
