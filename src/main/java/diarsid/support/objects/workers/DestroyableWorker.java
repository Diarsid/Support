package diarsid.support.objects.workers;

public interface DestroyableWorker extends Worker {

    WorkerStateChange destroy();

    boolean isDestroyed();
}
