package diarsid.support.objects.references.impl;

import diarsid.support.objects.references.Reference;

public interface Present<T> extends Reference<T> {

    T get();

}
