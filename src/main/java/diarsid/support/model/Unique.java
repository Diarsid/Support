package diarsid.support.model;

import java.util.List;
import java.util.UUID;

import static java.lang.String.format;
import static java.util.stream.Collectors.toList;

public interface Unique extends Identity<UUID> {

    UUID uuid();

    @Override
    default UUID id() {
        return this.uuid();
    }

    default boolean has(UUID uuid) {
        return this.uuid().equals(uuid);
    }

    default boolean hasNot(UUID uuid) {
        return ! this.uuid().equals(uuid);
    }

    default void mustHave(UUID uuid) {
        if ( ! this.uuid().equals(uuid) ) {
            throw new IllegalStateException(format("%s has uuid %s but expected %s",
                    this.getClass().getSimpleName(),
                    this.uuid(),
                    uuid));
        }
    }

    static List<UUID> uuidsOf(List<? extends Unique> uniques) {
        return uniques.stream().map(Unique::uuid).distinct().collect(toList());
    }
}
