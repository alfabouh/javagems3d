/*
 * *
 *  * @author alfabouh
 *  * @since 2024
 *  * @link https://github.com/alfabouh/JavaGems3D
 *  *
 *  * This software is provided 'as-is', without any express or implied warranty.
 *  * In no event will the authors be held liable for any damages arising from the use of this software.
 *
 */

package ru.jgems3d.engine.physics.world.ai;

import org.jetbrains.annotations.NotNull;
import ru.jgems3d.engine.physics.world.basic.WorldItem;

public abstract class AbstractAI<T extends WorldItem> implements IEntityAI<T> {
    private final int priority;
    private final T owner;
    private State state;

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

    @Override
    public @NotNull State getState() {
        return this.state;
    }

    public void setState(State state) {
        this.state = state;
    }

    @Override
    public int priority() {
        return this.priority;
    }
}
