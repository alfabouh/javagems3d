package ru.alfabouh.jgems3d.engine.physics.entities.prop;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import ru.alfabouh.jgems3d.engine.physics.collision.AbstractCollision;
import ru.alfabouh.jgems3d.engine.physics.collision.OBB;
import ru.alfabouh.jgems3d.engine.physics.jb_objects.RigidBodyObject;
import ru.alfabouh.jgems3d.engine.physics.world.IWorld;
import ru.alfabouh.jgems3d.engine.physics.world.World;

public class PhysPlank extends PhysEntityProp {
    public PhysPlank(World world, RigidBodyObject.PhysProperties properties, double scale, @NotNull Vector3d pos, @NotNull Vector3d rot) {
        super(world, "plank", properties, scale, pos, rot);
    }

    public void onUpdate(IWorld iWorld) {
        super.onUpdate(iWorld);
    }

    @Override
    protected AbstractCollision constructCollision() {
        return new OBB(new Vector3d(1.8d, 0.16d, 0.015d));
    }

    protected void afterRigidBodyCreated(RigidBodyObject rigidBodyObject) {
        this.getBulletObject().makeStatic();
        this.entityState().setCanBeSelectedByPlayer(true);
        this.getBulletObject().disableCCD();
    }
}