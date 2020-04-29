package diarsid.support.objects.references.real;

public interface PresentListenable<T> extends Present<T>, ListenableRemovable<T> {

    Present<T> asSimple();

}
