package ru.alfabouh.jgems3d.engine.physics.objects.entities.common;

import org.joml.Vector3f;
import ru.alfabouh.jgems3d.engine.physics.collision.base.AbstractCollision;
import ru.alfabouh.jgems3d.engine.physics.collision.primitive.OBBShape;
import ru.alfabouh.jgems3d.engine.physics.jb_objects.RigidBodyObject;
import ru.alfabouh.jgems3d.engine.physics.objects.base.BodyGroup;
import ru.alfabouh.jgems3d.engine.physics.objects.base.PhysObject;
import ru.alfabouh.jgems3d.engine.physics.world.IWorld;
import ru.alfabouh.jgems3d.engine.physics.world.World;

public class PhysPlank extends PhysObject {

    public PhysPlank(World world, String name, RigidBodyObject.PhysProperties properties, Vector3f pos, Vector3f rot, Vector3f scale) {
        super(world, name, properties, pos, rot, scale);
    }

    public PhysPlank(World world, String name, RigidBodyObject.PhysProperties properties, Vector3f pos, Vector3f rot) {
        super(world, name, properties, pos, rot);
    }

    public PhysPlank(World world, String name, RigidBodyObject.PhysProperties properties, Vector3f pos) {
        super(world, name, properties, pos);
    }

    public void onUpdate(IWorld iWorld) {
        super.onUpdate(iWorld);
    }

    @Override
    protected AbstractCollision constructCollision() {
        return new OBBShape(new Vector3f(1.8f, 0.16f, 0.015f));
    }

    protected void afterRigidBodyCreated(RigidBodyObject rigidBodyObject) {
        this.getBulletObject().makeStatic();
        this.entityState().setCanBeSelectedByPlayer(true);
        this.getBulletObject().disableCCD();
    }

    @Override
    public BodyGroup getBodyIndex() {
        return BodyGroup.ENTITY_DYNAMIC;
    }
}
