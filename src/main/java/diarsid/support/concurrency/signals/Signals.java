package diarsid.support.concurrency.signals;

import java.io.Closeable;

import diarsid.support.objects.workers.Worker;

public interface Signals {

    Signal signal(String name);

    interface Runtime extends Signals, Worker, Worker.Destroyable, Closeable {

        @Override
        void close();
    }
}
