package diarsid.support.misc;

import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

public class Misc {

    private static final Function<Stream<StackWalker.StackFrame>, Optional<String>> FIND_FIRST_FRAME_NAME = frames -> {
        return frames.limit(2).skip(1).map(StackWalker.StackFrame::getMethodName).findFirst();
    };

    public static String methodName() {
        return StackWalker.getInstance().walk(FIND_FIRST_FRAME_NAME).orElseThrow();
    }

    public static String methodName(long depth) {
        Function<Stream<StackWalker.StackFrame>, Optional<String>> findFrameName = frames -> {
            return frames.limit(2 + depth).skip(1 + depth).map(StackWalker.StackFrame::getMethodName).findFirst();
        };
        return StackWalker.getInstance().walk(findFrameName).orElseThrow();
    }
}
