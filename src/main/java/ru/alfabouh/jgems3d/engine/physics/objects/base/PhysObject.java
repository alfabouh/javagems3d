package ru.alfabouh.jgems3d.engine.physics.objects.base;

import org.bytedeco.bullet.BulletCollision.btCollisionObject;
import org.joml.Vector3f;
import ru.alfabouh.jgems3d.engine.physics.jb_objects.RigidBodyObject;
import ru.alfabouh.jgems3d.engine.physics.objects.states.EntityState;
import ru.alfabouh.jgems3d.engine.physics.world.IWorld;
import ru.alfabouh.jgems3d.engine.physics.world.World;
import ru.alfabouh.jgems3d.engine.physics.world.object.CollidableWorldItem;
import ru.alfabouh.jgems3d.engine.physics.world.object.IWorldDynamic;

public abstract class PhysObject extends CollidableWorldItem implements IWorldDynamic {
    private final Vector3f velocityVector;

    public PhysObject(World world, String name, RigidBodyObject.PhysProperties properties, Vector3f pos, Vector3f rot, Vector3f scale) {
        super(world, properties, pos, rot, scale, name);
        this.velocityVector = new Vector3f(0.0f);
    }

    public PhysObject(World world, String name, RigidBodyObject.PhysProperties properties, Vector3f pos, Vector3f rot) {
        this(world, name, properties, pos, rot, new Vector3f(1.0f));
    }

    public PhysObject(World world, String name, RigidBodyObject.PhysProperties properties, Vector3f pos) {
        this(world, name, properties, pos, new Vector3f(0.0f), new Vector3f(1.0f));
    }

    public Vector3f getVelocityVector() {
        return new Vector3f(this.velocityVector);
    }

    public void setVelocityVector(Vector3f vector3f) {
        this.velocityVector.set(vector3f);
    }

    public void setDebugDrawing(boolean flag) {
        if (flag) {
            this.getBulletObject().setCollisionFlags(this.getBulletObject().getCollisionFlags() & ~btCollisionObject.CF_DISABLE_VISUALIZE_OBJECT);
        } else {
            this.getBulletObject().setCollisionFlags(this.getBulletObject().getCollisionFlags() | btCollisionObject.CF_DISABLE_VISUALIZE_OBJECT);
        }
    }

    @Override
    public void onUpdate(IWorld world) {
        if (this.isValid()) {
            this.addObjectVelocity(this.getVelocityVector());
            this.setVelocityVector(new Vector3f(0.0f));
            if (this.getPosition().y <= -50 || this.getPosition().y >= 500) {
                this.setPosition(new Vector3f(0, 5, 0));
                this.setObjectVelocity(new Vector3f(0.0f));
            }
            if (this.entityState().checkState(EntityState.StateType.IN_WATER)) {
                if (!this.getBulletObject().isStaticObject()) {
                    this.getBulletObject().setObjectLinearVelocity(this.getBulletObject().getObjectLinearVelocity().mul(0.9f));
                    this.getBulletObject().activateObject();
                }
            }
        }
    }
}
