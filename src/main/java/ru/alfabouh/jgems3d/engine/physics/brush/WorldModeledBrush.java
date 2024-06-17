package ru.alfabouh.jgems3d.engine.physics.brush;

import ru.alfabouh.jgems3d.engine.physics.collision.AbstractCollision;
import ru.alfabouh.jgems3d.engine.physics.collision.ModelStaticShape;
import ru.alfabouh.jgems3d.engine.physics.jb_objects.RigidBodyObject;
import ru.alfabouh.jgems3d.engine.physics.world.IWorld;
import ru.alfabouh.jgems3d.engine.physics.world.World;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.mesh.MeshDataGroup;

public class WorldModeledBrush extends WorldBrush {
    private final MeshDataGroup meshDataGroup;

    public WorldModeledBrush(World world, MeshDataGroup meshDataGroup, RigidBodyObject.PhysProperties properties, String name) {
        super(world, properties, name);
        this.meshDataGroup = meshDataGroup;
    }

    public void onSpawn(IWorld iWorld) {
        super.onSpawn(iWorld);
    }

    @Override
    protected AbstractCollision constructCollision() {
        return new ModelStaticShape(this.meshDataGroup);
    }
}
