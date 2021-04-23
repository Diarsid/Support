package diarsid.support.objects.references.impl;

import diarsid.support.objects.references.Listenable;
import diarsid.support.objects.references.Listening;
import diarsid.support.objects.references.Reference;

interface ListenableRemovable<T> extends Reference<T>, Listenable<T> {

    boolean remove(Listening<T> listening);
}
