package diarsid.support.concurrency.signals;

import java.io.Closeable;

import diarsid.support.concurrency.signals.impl.SignalsRuntimeImpl;
import diarsid.support.objects.workers.Worker;

public interface Signals {

    Signal signal(String name);

    interface Runtime extends Signals, Worker, Worker.Destroyable, Closeable {

        public static Signals.Runtime create() {
            return new SignalsRuntimeImpl();
        }

        @Override
        void close();
    }
}
