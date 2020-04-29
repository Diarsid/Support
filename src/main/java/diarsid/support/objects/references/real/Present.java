package diarsid.support.objects.references.real;

import diarsid.support.objects.references.Reference;

public interface Present<T> extends Reference<T> {

    T get();

}
