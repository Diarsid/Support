package diarsid.support.objects.collections;

import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.Arrays.stream;

public class StreamsSupport {

    @SuppressWarnings("unchecked")
    private static final Function<Object, Stream<? extends Object>> UNWRAPPING = obj -> {
        if ( obj instanceof Collection) {
            return ( (Collection) obj ).stream();
        } else if ( obj.getClass().isArray()
                && ! (obj instanceof byte[] || obj instanceof Byte[]) ) {
            return stream( (Object[]) obj );
        } else {
            return Stream.of(obj);
        }
    };

    private StreamsSupport() {}

    public static Stream<? extends Object> unwrap(Stream<? extends Object> stream) {
        return stream.flatMap(UNWRAPPING);
    }

    public static Function<Object, Stream<? extends Object>> unwrapping() {
        return UNWRAPPING;
    }
}
