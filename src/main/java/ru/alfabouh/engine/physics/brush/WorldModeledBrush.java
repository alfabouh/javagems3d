package ru.alfabouh.engine.physics.brush;

import ru.alfabouh.engine.system.resources.assets.models.mesh.MeshDataGroup;
import ru.alfabouh.engine.physics.collision.AbstractCollision;
import ru.alfabouh.engine.physics.collision.ModelShape;
import ru.alfabouh.engine.physics.jb_objects.RigidBodyObject;
import ru.alfabouh.engine.physics.world.IWorld;
import ru.alfabouh.engine.physics.world.World;

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
        return new ModelShape(this.meshDataGroup);
    }
}
