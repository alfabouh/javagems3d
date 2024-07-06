package ru.alfabouh.jgems3d.engine.physics.objects.entities.common;

import org.joml.Vector3f;
import ru.alfabouh.jgems3d.engine.physics.collision.base.AbstractCollision;
import ru.alfabouh.jgems3d.engine.physics.collision.complex.ModelDynamicMeshShape;
import ru.alfabouh.jgems3d.engine.physics.collision.complex.ModelStaticMeshShape;
import ru.alfabouh.jgems3d.engine.physics.jb_objects.RigidBodyObject;
import ru.alfabouh.jgems3d.engine.physics.objects.base.PhysObject;
import ru.alfabouh.jgems3d.engine.physics.world.IWorld;
import ru.alfabouh.jgems3d.engine.physics.world.World;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.mesh.MeshDataGroup;

public abstract class PhysEntity extends PhysObject {
    private final MeshDataGroup meshDataGroup;
    private boolean canBeDestroyed;

    public PhysEntity(World world, String name, RigidBodyObject.PhysProperties properties, Vector3f pos, Vector3f rot, Vector3f scale, MeshDataGroup meshDataGroup) {
        super(world, name, properties, pos, rot, scale);
        this.meshDataGroup = meshDataGroup;
        this.canBeDestroyed = true;
    }

    public PhysEntity(World world, String name, RigidBodyObject.PhysProperties properties, Vector3f pos, Vector3f rot, MeshDataGroup meshDataGroup) {
        this(world, name, properties, pos, rot, new Vector3f(1.0f), meshDataGroup);
    }

    public PhysEntity(World world, String name, RigidBodyObject.PhysProperties properties, Vector3f pos, MeshDataGroup meshDataGroup) {
        this(world, name, properties, pos, new Vector3f(0.0f), new Vector3f(1.0f), meshDataGroup);
    }

    public PhysEntity setCanBeDestroyed(boolean flag) {
        this.canBeDestroyed = flag;
        return this;
    }

    @Override
    public boolean canBeDestroyed() {
        return this.canBeDestroyed;
    }

    @Override
    protected AbstractCollision constructCollision() {
        return this.getBodyIndex().isStatic() ? new ModelStaticMeshShape(this.meshDataGroup) : new ModelDynamicMeshShape(this.meshDataGroup);
    }

    public void onSpawn(IWorld iWorld) {
        super.onSpawn(iWorld);
    }
}
