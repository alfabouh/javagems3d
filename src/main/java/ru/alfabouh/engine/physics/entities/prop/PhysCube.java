package ru.alfabouh.engine.physics.entities.prop;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import ru.alfabouh.engine.physics.collision.AbstractCollision;
import ru.alfabouh.engine.physics.collision.OBB;
import ru.alfabouh.engine.physics.jb_objects.RigidBodyObject;
import ru.alfabouh.engine.physics.world.IWorld;
import ru.alfabouh.engine.physics.world.World;

public class PhysCube extends PhysEntityProp {
    private final Vector3d size;

    public PhysCube(World world, RigidBodyObject.PhysProperties properties, Vector3d size, double scale, @NotNull Vector3d pos, @NotNull Vector3d rot) {
        super(world, "cube", properties, scale, pos, rot);
        this.size = size;
    }

    public void onUpdate(IWorld iWorld) {
        super.onUpdate(iWorld);
    }

    public Vector3d getSize() {
        return this.size;
    }

    @Override
    protected AbstractCollision constructCollision() {
        return new OBB(new Vector3d(this.getSize().x, this.getSize().y, this.getSize().z));
    }
}
