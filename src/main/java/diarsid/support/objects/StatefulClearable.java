package diarsid.support.objects;

public interface StatefulClearable extends AutoCloseable {
    
    void clear();

    @Override
    default void close() {
        this.clear();
    }
}
