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

package javagems3d.physics.entities;

import com.jme3.bullet.objects.PhysicsRigidBody;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import javagems3d.physics.colliders.DynamicMeshCollider;
import javagems3d.physics.colliders.IColliderConstructor;
import javagems3d.physics.entities.properties.collision.CollisionFilter;
import javagems3d.physics.world.IWorld;
import javagems3d.physics.world.PhysicsWorld;
import javagems3d.physics.world.thread.dynamics.DynamicsSystem;
import javagems3d.system.resources.assets.models.mesh.MeshDataGroup;

public class BtDynamicMeshBody extends BtBody {
    private final MeshDataGroup meshDataGroup;

    public BtDynamicMeshBody(MeshDataGroup meshDataGroup, PhysicsWorld world, @NotNull Vector3f pos, @NotNull Vector3f rot, @NotNull Vector3f scale, String itemName) {
        super(world, pos, rot, scale, itemName);
        this.meshDataGroup = meshDataGroup;
    }

    public BtDynamicMeshBody(MeshDataGroup meshDataGroup, PhysicsWorld world, @NotNull Vector3f pos, @NotNull Vector3f rot, String itemName) {
        this(meshDataGroup, world, pos, rot, new Vector3f(1.0f), itemName);
    }

    public BtDynamicMeshBody(MeshDataGroup meshDataGroup, PhysicsWorld world, @NotNull Vector3f pos, String itemName) {
        this(meshDataGroup, world, pos, new Vector3f(0.0f), new Vector3f(1.0f), itemName);
    }

    @Override
    protected void onTick(IWorld iWorld) {
        super.onTick(iWorld);
    }

    @Override
    protected void postInit(DynamicsSystem dynamicsSystem, JGemsPhysicsRigidBody jGemsPhysicsRigidBody) {
        jGemsPhysicsRigidBody.setCcdMotionThreshold(1.0e-4f);
        this.resetCCD(jGemsPhysicsRigidBody);

        this.makeDynamic();
        this.setCollisionGroup(CollisionFilter.DN_BODY);
    }

    protected void resetCCD(PhysicsRigidBody physicsRigidBody) {
        physicsRigidBody.setCcdSweptSphereRadius(physicsRigidBody.getCollisionShape().maxRadius() / 10.0f);
    }

    @Override
    public void setScaling(Vector3f vector3f) {
        super.setScaling(vector3f);
        this.resetCCD(this.getPhysicsRigidBody());
    }

    @Override
    protected IColliderConstructor constructCollision() {
        return new DynamicMeshCollider(this.meshDataGroup);
    }
}
