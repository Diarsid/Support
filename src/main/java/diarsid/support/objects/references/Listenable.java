package diarsid.support.objects.references;

import java.util.function.BiConsumer;

public interface Listenable<T> extends Reference<T> {

    Listening<T> listen(BiConsumer<T, T> listener);

    void clearListeners();
}
