package diarsid.support.objects.references.impl;

import java.util.Map;

import diarsid.support.objects.references.Bindable;
import diarsid.support.objects.references.Listenable;
import diarsid.support.objects.references.Listening;
import diarsid.support.objects.references.Reference;

import static java.lang.String.format;
import static java.util.Objects.nonNull;

interface RealBindable<T> extends Bindable<T>, Reference<T> {

    Map<Listenable<T>, Listening<T>> bindings();

    void createBindingsMap();

    boolean isBindingsMapNull();

    @SuppressWarnings("unchecked")
    default void bindTo(Listenable<T> listenable) {
        if ( this.isBindingsMapNull() ) {
            this.createBindingsMap();
        }

        Map<Listenable<T>, Listening<T>> bindings = this.bindings();

        if ( bindings.containsKey(listenable) ) {
            return;
        }

        Listening<T> listening = listenable.listen(this::resetReferenceOnChange);
        bindings.put(listenable, listening);

        T valueOnBind;
        if ( listenable instanceof Present ) {
            Present<T> present = (Present<T>) listenable;
            valueOnBind = present.get();
        }
        else if ( listenable instanceof Possible ) {
            Possible<T> possible = (Possible<T>) listenable;
            valueOnBind = possible.or(null);
        }
        else {
            throw new IllegalArgumentException(format("Unknown type of %s : %s",
                    Listenable.class.getSimpleName(),
                    listenable.getClass().getSimpleName()));
        }

        this.resetTo(valueOnBind);

    }

    private void resetReferenceOnChange(T oldValue, T newValue) {
        this.resetTo(newValue);
    }

    @Override
    default void unbindFrom(Listenable<T> listenable) {
        if ( this.isBindingsMapNull() ) {
            return;
        }

        Map<Listenable<T>, Listening<T>> bindings = this.bindings();

        Listening<T> listening = bindings.remove(listenable);
        if ( nonNull(listening) ) {
            listening.cancel();
        }
    }

    @Override
    default void unbindAll() {
        if ( this.isBindingsMapNull() ) {
            return;
        }

        this.bindings().values().forEach(this::cancelListening);
        this.bindings().clear();
    }

    private void cancelListening(Listening<T> listening) {
        listening.cancel();
    }
}
