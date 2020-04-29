package diarsid.support.objects.references;

public interface Listening<T> {

    Listenable<T> source();

    boolean cancel();

    boolean isListening();
}
