package ru.jgems3d.engine.physics.entities;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import ru.jgems3d.engine.physics.colliders.IColliderConstructor;
import ru.jgems3d.engine.physics.colliders.StaticMeshCollider;
import ru.jgems3d.engine.physics.entities.properties.collision.CollisionFilter;
import ru.jgems3d.engine.physics.world.PhysicsWorld;
import ru.jgems3d.engine.physics.world.thread.dynamics.DynamicsSystem;
import ru.jgems3d.engine.system.resources.assets.models.mesh.MeshDataGroup;

public class BtStaticMeshBody extends BtBody {
    private final MeshDataGroup meshDataGroup;

    public BtStaticMeshBody(MeshDataGroup meshDataGroup, PhysicsWorld world, @NotNull Vector3f pos, @NotNull Vector3f rot, @NotNull Vector3f scale, String itemName) {
        super(world, pos, rot, scale, itemName);
        this.meshDataGroup = meshDataGroup;
    }

    public BtStaticMeshBody(MeshDataGroup meshDataGroup, PhysicsWorld world, @NotNull Vector3f pos, @NotNull Vector3f rot, String itemName) {
        this(meshDataGroup, world, pos, rot, new Vector3f(1.0f), itemName);
    }

    public BtStaticMeshBody(MeshDataGroup meshDataGroup, PhysicsWorld world, @NotNull Vector3f pos, String itemName) {
        this(meshDataGroup, world, pos, new Vector3f(0.0f), new Vector3f(1.0f), itemName);
    }

    @Override
    protected void postInit(DynamicsSystem dynamicsSystem, JGemsPhysicsRigidBody jGemsPhysicsRigidBody) {
        this.makeStatic();
        this.setCollisionGroup(CollisionFilter.ST_BODY);
    }

    @Override
    protected IColliderConstructor constructCollision() {
        return new StaticMeshCollider(this.meshDataGroup);
    }
}
