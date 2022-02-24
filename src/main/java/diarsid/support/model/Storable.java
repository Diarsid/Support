package diarsid.support.model;

import java.io.Serializable;

import diarsid.support.objects.CommonEnum;

public interface Storable extends Serializable {

    enum State implements CommonEnum<State> {
        STORED,
        NON_STORED
    }

    State state();

    State setState(State newState);

    default boolean hasState(State state) {
        return this.state().equals(state);
    }

    static void checkMustBeStored(Storable storable) {
        if ( storable.state().notEqualTo(State.STORED) ) {
            throw new NotStoredException();
        }
    }

    static void checkMustBeStored(Iterable<? extends Storable> storables) {
        for ( Storable storable : storables ) {
            checkMustBeStored(storable);
        }
    }

    static void checkMustBeStored(Storable[] storables) {
        for ( Storable storable : storables ) {
            checkMustBeStored(storable);
        }
    }
}
