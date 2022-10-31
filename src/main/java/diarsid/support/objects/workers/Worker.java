package diarsid.support.objects.workers;

public interface Worker {

    WorkerStateChange startWork();

    boolean isWorking();

    interface Pausable extends Worker {

        WorkerStateChange pauseWork();

        boolean isPaused();
    }

    interface Destroyable extends Worker {

        WorkerStateChange destroy();

        boolean isDestroyed();
    }
}
