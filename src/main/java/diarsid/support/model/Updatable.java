package diarsid.support.model;

import java.time.LocalDateTime;

public interface Updatable extends Storable {

    LocalDateTime actualAt();
}
