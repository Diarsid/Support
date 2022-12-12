package diarsid.support.objects.references;

public interface Bindable<T> extends Reference.Writable<T> {

    void bindTo(Listenable<T> listenable);

    void unbindFrom(Listenable<T> listenable);

    void unbindAll();
}
