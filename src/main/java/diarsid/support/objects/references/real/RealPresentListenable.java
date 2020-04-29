package diarsid.support.objects.references.real;

import java.util.HashMap;
import java.util.function.BiConsumer;

import diarsid.support.objects.references.Listening;

import static java.util.Objects.nonNull;

public class RealPresentListenable<T> extends RealPresent<T> implements PresentListenable<T> {

    private final HashMap<RealListening<T>, BiConsumer<T, T>> listeners;

    RealPresentListenable(T t, String name) {
        super(t, name);
        this.listeners = new HashMap<>();
    }

    @Override
    protected T internalSet(T newT) {
        T oldT = super.internalSet(newT);

        if ( ! this.listeners.isEmpty() ) {
            this.listeners
                    .entrySet()
                    .forEach(pair -> {
                        if ( pair.getKey().isListening() ) {
                            try {
                                pair.getValue().accept(oldT, newT);
                            }
                            catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
        }
        return oldT;
    }

    @Override
    public Listening<T> listen(BiConsumer<T, T> listener) {
        RealListening<T> listening = new RealListening<>(this);
        this.listeners.put(listening, listener);
        return listening;
    }

    @Override
    public void clearListeners() {
        this.listeners.keySet().forEach(RealListening::cancelInternally);
    }

    @Override
    public Present<T> asSimple() {
        return this;
    }

    @Override
    public boolean remove(Listening<T> listening) {
        if (listening instanceof RealListening) {
            BiConsumer<T, T> removed = this.listeners.remove(listening);
            return nonNull(removed);
        }
        else {
            return false;
        }
    }
}
