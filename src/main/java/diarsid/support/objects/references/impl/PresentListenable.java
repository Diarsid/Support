package diarsid.support.objects.references.impl;

public interface PresentListenable<T> extends Present<T>, ListenableRemovable<T> {

    Present<T> asSimple();

}
