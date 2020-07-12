package diarsid.support.objects.workers;

public interface PausableWorker extends Worker {

    WorkerStateChange pauseWork();

    boolean isPaused();
}
