package diarsid.support.objects.references.impl;

import diarsid.support.objects.references.Listening;
import diarsid.support.objects.references.ReferenceListenable;

interface ListenableRemovable<T> extends ReferenceListenable<T> {

    boolean remove(Listening<T> listening);
}
