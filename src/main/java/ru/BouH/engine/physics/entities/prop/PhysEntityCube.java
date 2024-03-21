package ru.BouH.engine.physics.entities.prop;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import ru.BouH.engine.physics.collision.AbstractCollision;
import ru.BouH.engine.physics.collision.OBB;
import ru.BouH.engine.physics.jb_objects.RigidBodyObject;
import ru.BouH.engine.physics.world.World;
import ru.BouH.engine.physics.world.IWorld;

public class PhysEntityCube extends PhysEntityProp {

    public PhysEntityCube(World world, RigidBodyObject.PhysProperties properties, Vector3d size, double scale, @NotNull Vector3d pos, @NotNull Vector3d rot) {
        super(world, properties, size, scale, pos, rot);
    }

    public PhysEntityCube(World world, RigidBodyObject.PhysProperties properties, Vector3d size, @NotNull Vector3d pos, @NotNull Vector3d rot) {
        super(world, properties, size, pos, rot);
    }

    public void onUpdate(IWorld iWorld) {
        super.onUpdate(iWorld);
    }

    @Override
    protected AbstractCollision constructCollision() {
        return new OBB(new Vector3d(this.getSize().x, this.getSize().y, this.getSize().z));
    }
}
