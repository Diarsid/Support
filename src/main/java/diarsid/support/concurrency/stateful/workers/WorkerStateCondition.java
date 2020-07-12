package diarsid.support.concurrency.stateful.workers;

import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.function.Consumer;
import java.util.function.Function;

import diarsid.support.objects.workers.WorkerState;

import static java.lang.String.format;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import static diarsid.support.objects.workers.WorkerState.CREATED;

public class WorkerStateCondition<T> {

    private final ReadWriteLock allConditionsReadWriteLock;
    private final Consumer<WorkerState> changedStateCallback;
    private final Function<T, WorkerState> stateConditionT;
    private final AtomicReference<T> unsafeToModifyOutOfLock_currentT;
    private final AtomicReference<WorkerState> unsafeToModifyOutOfLock_currentConditionState;

    WorkerStateCondition(
            ReadWriteLock allConditionsReadWriteLock,
            Consumer<WorkerState> changedStateCallback,
            Function<T, WorkerState> stateConditionT) {
        this.allConditionsReadWriteLock = allConditionsReadWriteLock;
        this.changedStateCallback = changedStateCallback;
        this.stateConditionT = stateConditionT;
        this.unsafeToModifyOutOfLock_currentT = new AtomicReference<>();
        this.unsafeToModifyOutOfLock_currentConditionState = new AtomicReference<>(CREATED);
    }

    WorkerStateCondition(
            ReadWriteLock allConditionsReadWriteLock,
            Consumer<WorkerState> changedStateCallback,
            Function<T, WorkerState> stateConditionT,
            T initialT) {
        this.allConditionsReadWriteLock = allConditionsReadWriteLock;
        this.changedStateCallback = changedStateCallback;
        this.stateConditionT = stateConditionT;
        this.unsafeToModifyOutOfLock_currentT = new AtomicReference<>();
        this.unsafeToModifyOutOfLock_currentConditionState = new AtomicReference<>(CREATED);
        this.evaluate(initialT);
    }

    public void evaluate(T newT) {
        T oldT = unsafeToModifyOutOfLock_currentT.get();

        boolean areDifferent;
        if (nonNull(newT)) {
            areDifferent = isNull(oldT) || ! newT.equals(oldT);
        } else {
            areDifferent = nonNull(oldT);
        }

        if (areDifferent) {
            WorkerState newState = stateConditionT.apply(newT);

            allConditionsReadWriteLock.writeLock().lock();
            try {
                unsafeToModifyOutOfLock_currentT.set(newT);
                unsafeToModifyOutOfLock_currentConditionState.set(newState);
            } finally {
                allConditionsReadWriteLock.writeLock().unlock();
            }

            changedStateCallback.accept(newState);
        }
    }

    public T current() {
        return unsafeToModifyOutOfLock_currentT.get();
    }

    public boolean doesAllowWork() {
        return unsafeToModifyOutOfLock_currentConditionState.get().doesAllowWork();
    }

    @Override
    public String toString() {
        return format("%s@%s[conditionState:%s, value:%s]",
                getClass().getSimpleName(),
                hashCode(),
                unsafeToModifyOutOfLock_currentConditionState.get().name(),
                unsafeToModifyOutOfLock_currentT.get());
    }
}
