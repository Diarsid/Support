package diarsid.support.callbacks;

public interface VoidCallback extends Runnable, Callback {

    void call();

    @Override
    default void run() {
        this.call();
    }
}
