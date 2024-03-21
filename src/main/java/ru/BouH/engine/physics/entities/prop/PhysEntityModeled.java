package ru.BouH.engine.physics.entities.prop;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import ru.BouH.engine.game.resources.assets.models.mesh.MeshDataGroup;
import ru.BouH.engine.physics.collision.AbstractCollision;
import ru.BouH.engine.physics.collision.ModelShape;
import ru.BouH.engine.physics.jb_objects.RigidBodyObject;
import ru.BouH.engine.physics.world.World;
import ru.BouH.engine.physics.world.IWorld;

public class PhysEntityModeled extends PhysEntityProp {
    private final MeshDataGroup meshDataGroup;

    public PhysEntityModeled(World world, RigidBodyObject.PhysProperties properties, MeshDataGroup meshDataGroup, Vector3d size, double scale, @NotNull Vector3d pos, @NotNull Vector3d rot) {
        super(world, properties, size, scale, pos, rot);
        this.meshDataGroup = meshDataGroup;
    }

    public void onUpdate(IWorld iWorld) {
        super.onUpdate(iWorld);
    }

    @Override
    protected AbstractCollision constructCollision() {
        return new ModelShape(this.meshDataGroup);
    }
}
