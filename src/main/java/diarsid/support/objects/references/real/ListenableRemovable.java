package diarsid.support.objects.references.real;

import diarsid.support.objects.references.Listening;
import diarsid.support.objects.references.ReferenceListenable;

interface ListenableRemovable<T> extends ReferenceListenable<T> {

    boolean remove(Listening<T> listening);
}
