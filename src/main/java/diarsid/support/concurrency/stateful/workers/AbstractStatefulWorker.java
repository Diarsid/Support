package diarsid.support.concurrency.stateful.workers;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiConsumer;
import java.util.function.Function;

import diarsid.support.concurrency.AbstractStatefulObject;
import diarsid.support.objects.workers.ModifiableWorkerState;
import diarsid.support.objects.workers.ModifiableWorkerStateTransition;
import diarsid.support.objects.workers.Worker;
import diarsid.support.objects.workers.WorkerState;
import diarsid.support.objects.workers.WorkerStateChange;
import diarsid.support.objects.workers.WorkerStateTransition;

import static java.lang.String.format;
import static java.lang.String.valueOf;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import static diarsid.support.objects.workers.WorkerState.WORKING;
import static diarsid.support.objects.workers.WorkerStateChange.CHANGE_DONE;
import static diarsid.support.objects.workers.WorkerStateChange.CHANGE_FAILED;
import static diarsid.support.objects.workers.WorkerStateChange.CHANGE_NOT_NEEDED;
import static diarsid.support.objects.workers.WorkerStateChange.CHANGE_REJECTED;
import static diarsid.support.objects.workers.WorkerStateTransition.TO_DESTROYED;
import static diarsid.support.objects.workers.WorkerStateTransition.TO_PAUSED;
import static diarsid.support.objects.workers.WorkerStateTransition.TO_WORKING;

public abstract class AbstractStatefulWorker extends AbstractStatefulObject implements Worker {

    private static final ConcurrentHashMap<Class, AtomicLong> UNNAMED_INSTANCE_COUNTER = new ConcurrentHashMap<>();

    private final String name;
    private final ModifiableWorkerState state;
    private final ModifiableWorkerStateTransition stateTransition;
    private final WorkerStateConditions conditions;

    public AbstractStatefulWorker() {
        super();
        AtomicLong instanceCounter = UNNAMED_INSTANCE_COUNTER.computeIfAbsent(this.getClass(), (clazz) -> new AtomicLong(0));
        this.name = valueOf(instanceCounter.getAndIncrement());
        String className = this.getClass().getSimpleName();
        this.state = new ModifiableWorkerState(className, name);
        this.stateTransition = new ModifiableWorkerStateTransition(className, name);
        this.conditions = new WorkerStateConditions(this);
    }

    public AbstractStatefulWorker(BiConsumer<String, Throwable> unexpectedExceptionsConsumer) {
        super(unexpectedExceptionsConsumer);
        AtomicLong instanceCounter = UNNAMED_INSTANCE_COUNTER.computeIfAbsent(this.getClass(), (clazz) -> new AtomicLong(0));
        this.name = valueOf(instanceCounter.getAndIncrement());
        String className = this.getClass().getSimpleName();
        this.state = new ModifiableWorkerState(className, name);
        this.stateTransition = new ModifiableWorkerStateTransition(className, name);
        this.conditions = new WorkerStateConditions(this);
    }

    public AbstractStatefulWorker(String name) {
        super();
        if ( name == null ) {
            AtomicLong instanceCounter = UNNAMED_INSTANCE_COUNTER.computeIfAbsent(this.getClass(), (clazz) -> new AtomicLong(0));
            this.name = valueOf(instanceCounter.getAndIncrement());
        }
        else {
            this.name = name;
        }
        String className = this.getClass().getSimpleName();
        this.state = new ModifiableWorkerState(className, this.name);
        this.stateTransition = new ModifiableWorkerStateTransition(className, this.name);
        this.conditions = new WorkerStateConditions(this);
    }

    public AbstractStatefulWorker(String name, BiConsumer<String, Throwable> unexpectedExceptionsConsumer) {
        super(unexpectedExceptionsConsumer);
        if ( name == null ) {
            AtomicLong instanceCounter = UNNAMED_INSTANCE_COUNTER.computeIfAbsent(this.getClass(), (clazz) -> new AtomicLong(0));
            this.name = valueOf(instanceCounter.getAndIncrement());
        }
        else {
            this.name = name;
        }
        String className = this.getClass().getSimpleName();
        this.state = new ModifiableWorkerState(className, this.name);
        this.stateTransition = new ModifiableWorkerStateTransition(className, this.name);
        this.conditions = new WorkerStateConditions(this);
    }

    public final String name() {
        return this.name;
    }

    public final void mustBeInState(WorkerState someState) {
        WorkerState currentState = this.state.current();
        if (currentState.notEquals(someState)) {
            String message = format("%s %s must be in %s state, but is in %s state!",
                    this.getClass().getSimpleName(),
                    this.name,
                    someState.name(),
                    currentState.name());
            throw new IllegalStateException(message);
        }
    }

    protected abstract boolean doSynchronizedStartWork();

    protected final ModifiableWorkerState state() {
        return this.state;
    }

    protected final ModifiableWorkerStateTransition stateTransition() {
        return this.stateTransition;
    }

    final WorkerStateConditions conditions() {
        return this.conditions;
    }

    protected final boolean isWorkingOrTransitingToWorking() {
        return this.isInStateOrTransitingToState(WORKING, TO_WORKING);
    }

    protected final boolean isInStateOrTransitingToState(WorkerState someState, WorkerStateTransition someTransition) {
        WorkerStateTransition transition = this.stateTransition.current();

        if ( this.state.current().equals(someState) ) {
            return isNull(transition) || transition.equals(someTransition);
        }
        else {
            return nonNull(transition) && transition.equals(someTransition);
        }
    }

    protected final <T> WorkerStateCondition<T> addWorkerStateChangeConditionOn(
            Function<T, WorkerState> stateConditionT) {
        return conditions.addCondition(stateConditionT);
    }

    protected final <T> WorkerStateCondition<T> addWorkerStateChangeConditionOn(
            T initialT, Function<T, WorkerState> stateConditionT) {
        return conditions.addCondition(initialT, stateConditionT);
    }

    @Override
    public final WorkerStateChange startWork() {
        return super.doSynchronizedReturnChange(this::startWorkSynchronously);
    }

    private WorkerStateChange startWorkSynchronously() {
        if (this.state.current().equals(WORKING)) {
            return CHANGE_NOT_NEEDED;
        }

        this.state.throwIfCannotChangeTo(WORKING);

        this.conditions.readLock().lock();
        try {

            this.stateTransition.beginTransitionTo(TO_WORKING);
            try {
                WorkerStateChange change;

                if (this.conditions.areAllConditionsAllowWork()) {
                    if (doSynchronizedStartWork()) {
                        this.state.changeTo(WORKING);
                        change =  CHANGE_DONE;
                    } else {
                        change =  CHANGE_FAILED;
                    }
                } else {
                    change = CHANGE_REJECTED;
                }

                return change;
            } finally {
                this.stateTransition.stopCurrentTransition();
            }

        } finally {
            this.conditions.readLock().unlock();
        }
    }

    @Override
    public final boolean isWorking() {
        return state.current().equals(WORKING);
    }

}
