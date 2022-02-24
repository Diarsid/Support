package diarsid.support.model;

import java.io.Serializable;

public interface Identity<T extends Serializable> extends Serializable {

    T id();
}
