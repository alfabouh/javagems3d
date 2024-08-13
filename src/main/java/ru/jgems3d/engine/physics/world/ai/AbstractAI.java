package ru.jgems3d.engine.physics.world.ai;

import org.jetbrains.annotations.NotNull;
import ru.jgems3d.engine.physics.world.basic.WorldItem;

public abstract class AbstractAI<T extends WorldItem> implements IEntityAI<T> {
    private final int priority;
    private State state;
    private final T owner;

    public AbstractAI(T owner, int priority) {
        this.priority = priority;
        this.owner = owner;
        this.state = State.ENABLED;
    }

    public void enableAI() {
        if (this.getState() == State.DISABLED) {
            this.setState(State.ENABLED);
            this.onStartAI(this.getAIOwner());
        }
    }

    public void disableAI() {
        if (this.getState() == State.ENABLED) {
            this.setState(State.DISABLED);
            this.onEndAI(this.getAIOwner());
        }
    }

    public T getAIOwner() {
        return this.owner;
    }

    public void setState(State state) {
        this.state = state;
    }

    @Override
    public @NotNull State getState() {
        return this.state;
    }

    @Override
    public int priority() {
        return this.priority;
    }
}