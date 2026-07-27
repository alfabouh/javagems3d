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

package javagems3d.physics.world.basic;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import javagems3d.physics.world.IWorld;
import javagems3d.physics.world.PhysicsWorld;
import javagems3d.physics.world.ai.IEntityAI;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public abstract class AIBasedWorldItem extends WorldItem implements IWorldTicked {
    private final List<IEntityAI<? extends WorldItem>> entityAIs;

    public AIBasedWorldItem(PhysicsWorld world, @NotNull Vector3f pos, @NotNull Vector3f rot, @NotNull Vector3f scaling, String itemName) {
        super(world, pos, rot, scaling, itemName);
        this.entityAIs = new ArrayList<>();
    }

    public AIBasedWorldItem(PhysicsWorld world, Vector3f pos, Vector3f rot, String itemName) {
        this(world, pos, rot, new Vector3f(1.0f), itemName);
    }

    public AIBasedWorldItem(PhysicsWorld world, Vector3f pos, String itemName) {
        this(world, pos, new Vector3f(0.0f), new Vector3f(1.0f), itemName);
    }

    public abstract void init(AIBasedWorldItem aiBasedWorldItem);

    @Override
    public void onSpawn(IWorld iWorld) {
        super.onSpawn(iWorld);
        this.init(this);
        this.getEntityAIList().forEach(e -> e.onStartAI(this));
    }

    @Override
    public void onDestroy(IWorld iWorld) {
        super.onSpawn(iWorld);
        this.getEntityAIList().forEach(e -> e.onEndAI(this));
    }

    @Override
    public void onUpdate(IWorld iWorld) {
        for (IEntityAI<? extends WorldItem> entityAI : this.getEntityAIList()) {
            if (entityAI.getState() == IEntityAI.State.ENABLED) {
                entityAI.onUpdateAI(this);
            }
        }
    }

    public void addNewAI(IEntityAI<? extends WorldItem> ai) {
        this.getEntityAIList().add(ai);
        this.getEntityAIList().sort(Comparator.comparingInt(e -> ((IEntityAI<?>) e).priority()).thenComparingInt(System::identityHashCode));
    }

    public List<IEntityAI<? extends WorldItem>> getEntityAIList() {
        return this.entityAIs;
    }
}
