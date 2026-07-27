/*
 *
 *  * javagems3d
 *  * Copyright (C) 2026 gltexture
 *  *
 *  * This program is free software: you can redistribute it and/or modify
 *  * it under the terms of the GNU General Public License as published by
 *  * the Free Software Foundation, either version 3 of the License, or
 *  * (at your option) any later version.
 *  *
 *  * This program is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  * GNU General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU General Public License
 *  * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 */

package javagems3d.physics.world.ai;

import org.jetbrains.annotations.NotNull;
import javagems3d.physics.world.basic.WorldItem;

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
