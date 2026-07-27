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

package javagems3d.physics.entities.ai;

import javagems3d.physics.world.basic.WorldItem;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import javagems3d.JGems3D;
import javagems3d.physics.world.IWorld;
import javagems3d.physics.world.PhysicsWorld;
import javagems3d.physics.world.ai.navigation.MTNavigationAI;
import javagems3d.physics.world.ai.navigation.NavigationAI;
import javagems3d.physics.world.basic.AIBasedWorldItem;

public class CubeAI extends AIBasedWorldItem {
    private NavigationAI<CubeAI> ai;

    public CubeAI(PhysicsWorld world, @NotNull Vector3f pos, @NotNull Vector3f rot, @NotNull Vector3f scaling, String itemName) {
        super(world, pos, rot, scaling, itemName);
    }

    public CubeAI(PhysicsWorld world, Vector3f pos, Vector3f rot, String itemName) {
        super(world, pos, rot, itemName);
    }

    public CubeAI(PhysicsWorld world, Vector3f pos, String itemName) {
        super(world, pos, itemName);
    }

    @Override
    public void onSpawn(IWorld iWorld) {
        super.onSpawn(iWorld);
    }

    @Override
    public void onUpdate(IWorld iWorld) {
        super.onUpdate(iWorld);
        if (!this.ai.hasPath()) {
            this.ai.setDestination((WorldItem) JGems3D.get().getCurrentGameMapPlayer());
        }
    }

    @Override
    public void init(AIBasedWorldItem aiBasedWorldItem) {
        this.ai = new MTNavigationAI<>(this, 0);
        this.addNewAI(this.ai);
    }
}
