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

import com.jme3.bullet.collision.ContactListener;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.objects.PhysicsRigidBody;
import javagems3d.physics.entities.bullet.JGemsBody;
import javagems3d.physics.entities.properties.collision.CollisionType;
import javagems3d.physics.world.IWorld;
import javagems3d.physics.world.PhysicsWorld;
import javagems3d.physics.world.thread.dynamics.DynamicsSystem;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public abstract class JGemsAbstractDynamicBody extends JGemsBody {
    public JGemsAbstractDynamicBody(PhysicsWorld world, @NotNull Vector3f pos, @NotNull Vector3f rot, @NotNull Vector3f scale, String itemName) {
        super(world, pos, rot, scale, itemName);
    }

    public JGemsAbstractDynamicBody(PhysicsWorld world, @NotNull Vector3f pos, @NotNull Vector3f rot, String itemName) {
        this(world, pos, rot, new Vector3f(1.0f), itemName);
    }

    public JGemsAbstractDynamicBody(PhysicsWorld world, @NotNull Vector3f pos, String itemName) {
        this(world, pos, new Vector3f(0.0f), new Vector3f(1.0f), itemName);
    }

    @Override
    protected void onTick(IWorld iWorld) {
        super.onTick(iWorld);
    }

    @Override
    protected void postInit(DynamicsSystem dynamicsSystem, JGemsPhysicsRigidBody jGemsPhysicsRigidBody) {
        this.resetCCD(jGemsPhysicsRigidBody);
        this.makeDynamic();
        this.setCollisionGroup(CollisionType.DN_BODY);
    }

    protected void disableCcd(PhysicsRigidBody physicsRigidBody) {
        physicsRigidBody.setCcdMotionThreshold(Float.POSITIVE_INFINITY);
        physicsRigidBody.setCcdSweptSphereRadius(0);
    }

    protected void resetCCD(PhysicsRigidBody physicsRigidBody) {
        physicsRigidBody.setCcdMotionThreshold(0.05f);
        physicsRigidBody.setCcdSweptSphereRadius(physicsRigidBody.getCollisionShape().maxRadius() * 0.25f);
    }

    @Override
    public void setScaling(Vector3f vector3f) {
        super.setScaling(vector3f);
        if (this.getPhysicsRigidBody() != null) {
            this.resetCCD(this.getPhysicsRigidBody());
        }
    }
}
