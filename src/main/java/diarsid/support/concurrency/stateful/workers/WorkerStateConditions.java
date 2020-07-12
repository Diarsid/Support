package diarsid.support.concurrency.stateful.workers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Function;

import diarsid.support.objects.workers.DestroyableWorker;
import diarsid.support.objects.workers.PausableWorker;
import diarsid.support.objects.workers.WorkerState;

import static java.lang.String.format;
import static java.util.Objects.nonNull;

import static diarsid.support.objects.workers.WorkerState.DESTROYED;
import static diarsid.support.objects.workers.WorkerState.PAUSED;
import static diarsid.support.objects.workers.WorkerState.WORKING;

class WorkerStateConditions {

    private final AbstractStatefulWorker worker;
    private final ReadWriteLock readWriteLock;
    private final List<WorkerStateCondition> conditions;
    private final Map<WorkerState, Runnable> workerStateChangeActions;

    public WorkerStateConditions(AbstractStatefulWorker worker) {
        this.worker = worker;
        this.workerStateChangeActions = new HashMap<>();

        this.workerStateChangeActions.put(WORKING, worker::startWork);

        if (worker instanceof PausableWorker) {
            this.workerStateChangeActions.put(PAUSED, ((PausableWorker) worker)::pauseWork);
        }
        if (worker instanceof DestroyableWorker) {
            this.workerStateChangeActions.put(DESTROYED, ((DestroyableWorker) worker)::destroy);
        }

        this.readWriteLock = new ReentrantReadWriteLock(true);
        this.conditions = new ArrayList<>();
    }

    private WorkerState defineGlobalState(WorkerState currentState, WorkerState offeredState) {
        if (currentState.canNotBeChangedTo(offeredState)) {
            return currentState;
        }

        if (offeredState.doesNotAllowWork()) {
            return offeredState;
        }

        if (offeredState.doesAllowWork() && currentState.doesNotAllowWork()) {
            boolean allConditionsAllowWork = areAllConditionsAllowWork();
            if (allConditionsAllowWork) {
                return offeredState;
            }
        }

        return currentState;
    }

    private void tryChangeState(WorkerState offeredState) {
        WorkerState oldState = worker.state().current();
        WorkerState newState = defineGlobalState(oldState, offeredState);

        if (oldState.notEquals(newState)) {

            Runnable workerStateChangeAction = workerStateChangeActions.get(newState);
            if (nonNull(workerStateChangeAction)) {

                readWriteLock.readLock().lock();
                try {
                    workerStateChangeAction.run();
                } finally {
                    readWriteLock.readLock().unlock();
                }

            } else {
                throw new UnsupportedOperationException(format(
                        "Offered state %s is not supported by %s '%s'",
                        offeredState, this.getClass().getSimpleName(), this.worker.name()));
            }
        }
    }

    protected final boolean areAllConditionsAllowWork() {
        boolean allConditionsAllowWork;

        readWriteLock.readLock().lock();
        try {
            if (conditions.isEmpty()) {
                allConditionsAllowWork = true;
            } else {
                allConditionsAllowWork = conditions
                        .stream()
                        .allMatch(WorkerStateCondition::doesAllowWork);
            }
        } finally {
            readWriteLock.readLock().unlock();
        }

        return allConditionsAllowWork;
    }

    protected final Lock readLock() {
        return readWriteLock.readLock();
    }

    <T> WorkerStateCondition<T> addCondition(Function<T, WorkerState> stateConditionT) {
        WorkerStateCondition<T> condition;

        readWriteLock.writeLock().lock();
        try {
            condition = new WorkerStateCondition<>(readWriteLock, this::tryChangeState, stateConditionT);
            conditions.add(condition);
        } finally {
            readWriteLock.writeLock().unlock();
        }

        return condition;
    }

    <T> WorkerStateCondition<T> addCondition(T initialT, Function<T, WorkerState> stateConditionT) {
        WorkerStateCondition<T> condition;

        readWriteLock.writeLock().lock();
        try {
            condition = new WorkerStateCondition<>(readWriteLock, this::tryChangeState, stateConditionT, initialT);
            conditions.add(condition);
        } finally {
            readWriteLock.writeLock().unlock();
        }

        return condition;
    }

}
