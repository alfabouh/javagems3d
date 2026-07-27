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

package javagems3d.physics.entities.bullet.wrappers;

import com.jme3.bullet.objects.PhysicsRigidBody;
import javagems3d.physics.world.IWorld;
import javagems3d.physics.world.PhysicsWorld;
import javagems3d.physics.world.basic.WorldItem;
import javagems3d.physics.world.thread.dynamics.DynamicsUtils;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class BulletBody extends WorldItem {
    private final PhysicsRigidBody physicsRigidBody;
    private boolean canBeDestroyed;

    public BulletBody(PhysicsWorld world, @NotNull PhysicsRigidBody physicsRigidBody, String itemName) {
        super(world, DynamicsUtils.getObjectBodyPos(physicsRigidBody), DynamicsUtils.getObjectBodyRot(physicsRigidBody), DynamicsUtils.getObjectBodyScaling(physicsRigidBody), itemName);
        this.physicsRigidBody = physicsRigidBody;
    }

    public BulletBody(PhysicsWorld world, @NotNull PhysicsRigidBody physicsRigidBody) {
        this(world, physicsRigidBody, "bullet_ent");
    }

    @Override
    public void onSpawn(IWorld iWorld) {
        PhysicsWorld world = (PhysicsWorld) iWorld;
        super.onSpawn(iWorld);
        world.getDynamics().addCollisionObject(this.getPhysicsRigidBody());
    }

    @Override
    public void onDestroy(IWorld iWorld) {
        PhysicsWorld world = (PhysicsWorld) iWorld;
        super.onDestroy(iWorld);
        world.getDynamics().removeCollisionObject(this.getPhysicsRigidBody());
    }

    public BulletBody setCanBeDestroyed(boolean canBeDestroyed) {
        this.canBeDestroyed = canBeDestroyed;
        return this;
    }

    @Override
    public boolean canBeDestroyed() {
        return this.canBeDestroyed;
    }

    @Override
    public Vector3f getScaling() {
        return DynamicsUtils.getObjectBodyScaling(this.getPhysicsRigidBody());
    }

    public void setScaling(Vector3f scaling) {
        DynamicsUtils.scaleRigidBody(this.getPhysicsRigidBody(), scaling);
    }

    @Override
    public Vector3f getPosition() {
        return DynamicsUtils.getObjectBodyPos(this.getPhysicsRigidBody());
    }

    public void setPosition(Vector3f vector3f) {
        DynamicsUtils.translateRigidBody(this.getPhysicsRigidBody(), vector3f);
    }

    @Override
    public Vector3f getRotation() {
        return DynamicsUtils.getObjectBodyRot(this.getPhysicsRigidBody());
    }

    public void setRotation(Vector3f vector3f) {
        DynamicsUtils.rotateRigidBody(this.getPhysicsRigidBody(), vector3f);
    }

    public PhysicsRigidBody getPhysicsRigidBody() {
        return this.physicsRigidBody;
    }
}