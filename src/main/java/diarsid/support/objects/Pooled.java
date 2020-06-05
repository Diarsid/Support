package diarsid.support.objects;

import java.io.Closeable;
import java.io.IOException;

import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;

public class Pooled<T> extends PooledReusable {

    private final T t;

    public Pooled(T t) {
        super();
        requireNonNull(t);
        this.t = t;
    }

    public T value() {
        return this.t;
    }

    @Override
    protected void clearForReuse() {
        if ( isNull(this.t) ) {
            return;
        }

        if ( this.t instanceof Closeable ) {
            try {
                ((Closeable) this.t).close();
            }
            catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }
        else if ( this.t instanceof StatefulClearable ) {
            ((StatefulClearable) this.t).clear();
        }
    }
}
