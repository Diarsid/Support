package diarsid.support.concurrency;

import java.util.function.Consumer;

public class ThreadUtils {

    private ThreadUtils() {}

    public static void currentThreadTrack(String classNamePrefix, Consumer<StackTraceElement> elementConsumer) {
        for (StackTraceElement element : Thread.currentThread().getStackTrace()) {
            if (element.getClassName().startsWith(classNamePrefix)) {
                elementConsumer.accept(element);
            }
        }
    }
}
