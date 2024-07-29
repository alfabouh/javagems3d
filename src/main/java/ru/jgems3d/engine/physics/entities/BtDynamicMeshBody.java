package ru.jgems3d.engine.physics.entities;

import com.jme3.bullet.objects.PhysicsRigidBody;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import ru.jgems3d.engine.physics.colliders.IColliderConstructor;
import ru.jgems3d.engine.physics.colliders.DynamicMeshCollider;
import ru.jgems3d.engine.physics.entities.properties.collision.CollisionFilter;
import ru.jgems3d.engine.physics.world.PhysicsWorld;
import ru.jgems3d.engine.physics.world.thread.dynamics.DynamicsSystem;
import ru.jgems3d.engine.system.resources.assets.models.mesh.MeshDataGroup;

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

   // @Override
   // public ITriggerAction onEndColliding() {
   //     return e -> System.out.println("F");
   // }

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
