package ru.BouH.engine.physics.brush;

import ru.BouH.engine.game.resources.assets.models.mesh.MeshDataGroup;
import ru.BouH.engine.physics.collision.AbstractCollision;
import ru.BouH.engine.physics.collision.ModelShape;
import ru.BouH.engine.physics.jb_objects.RigidBodyObject;
import ru.BouH.engine.physics.world.World;
import ru.BouH.engine.proxy.IWorld;

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
