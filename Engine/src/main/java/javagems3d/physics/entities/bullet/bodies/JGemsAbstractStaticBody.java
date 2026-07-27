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

package javagems3d.physics.entities.bullet.bodies;

import javagems3d.physics.entities.bullet.JGemsBody;
import javagems3d.physics.entities.properties.collision.CollisionType;
import javagems3d.physics.world.PhysicsWorld;
import javagems3d.physics.world.thread.dynamics.DynamicsSystem;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public abstract class JGemsAbstractStaticBody extends JGemsBody {
    public JGemsAbstractStaticBody(PhysicsWorld world, @NotNull Vector3f pos, @NotNull Vector3f rot, @NotNull Vector3f scale, String itemName) {
        super(world, pos, rot, scale, itemName);
    }

    public JGemsAbstractStaticBody(PhysicsWorld world, @NotNull Vector3f pos, @NotNull Vector3f rot, String itemName) {
        this(world, pos, rot, new Vector3f(1.0f), itemName);
    }

    public JGemsAbstractStaticBody(PhysicsWorld world, @NotNull Vector3f pos, String itemName) {
        this(world, pos, new Vector3f(0.0f), new Vector3f(1.0f), itemName);
    }

    @Override
    protected void postInit(DynamicsSystem dynamicsSystem, JGemsPhysicsRigidBody jGemsPhysicsRigidBody) {
        this.makeStatic();
        this.setCollisionGroup(CollisionType.ST_BODY);
        this.setCollisionFilterNegative(CollisionType.LIQUID, CollisionType.GHOST, CollisionType.ST_BODY);
    }
}
