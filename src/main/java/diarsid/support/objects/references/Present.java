package diarsid.support.objects.references;

import java.util.function.Supplier;

public interface Present<T> extends Reference.Writable.NonNull<T>, Supplier<T> {

}
