package diarsid.support.objects.references.real;

public class Presents {

    private Presents() {};

    public static <T> Present<T> presentOf(T t, String name) {
        return new RealPresent<>(t, name);
    }

    public static <T> PresentListenable<T> listenablePresent(T t, String name) {
        return new RealPresentListenable<>(t, name);
    }

    public static <T> PresentListenable<T> listenable(Present<T> presentT, String name) {
        return new RealPresentListenable<>(presentT.get(), name);
    }
}
