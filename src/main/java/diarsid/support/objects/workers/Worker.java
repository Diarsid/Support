package diarsid.support.objects.workers;

public interface Worker {

    WorkerStateChange startWork();

    boolean isWorking();
}
