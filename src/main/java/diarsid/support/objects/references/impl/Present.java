package diarsid.support.objects.references.impl;

import java.util.function.Supplier;

import diarsid.support.objects.references.Reference;

public interface Present<T> extends Reference<T>, Supplier<T> {

    T get();

}
