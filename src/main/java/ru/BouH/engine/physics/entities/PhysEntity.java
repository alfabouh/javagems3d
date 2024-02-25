package ru.BouH.engine.physics.entities;

import org.bytedeco.bullet.BulletCollision.btCollisionObject;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import ru.BouH.engine.physics.brush.WorldBrush;
import ru.BouH.engine.physics.entities.states.EntityState;
import ru.BouH.engine.physics.jb_objects.RigidBodyObject;
import ru.BouH.engine.physics.world.World;
import ru.BouH.engine.physics.world.object.CollidableWorldItem;
import ru.BouH.engine.physics.world.object.IWorldDynamic;
import ru.BouH.engine.proxy.IWorld;

public abstract class PhysEntity extends CollidableWorldItem implements IWorldDynamic {
    private final Vector3d velocityVector;

    public PhysEntity(World world, String name, RigidBodyObject.PhysProperties properties, double scale, @NotNull Vector3d pos, @NotNull Vector3d rot) {
        super(world, properties, scale, pos, rot, name);
        this.velocityVector = new Vector3d(0.0d);
    }

    public PhysEntity(World world, String name, RigidBodyObject.PhysProperties properties, @NotNull Vector3d pos, @NotNull Vector3d rot) {
        this(world, name, properties, 1.0d, pos, rot);
    }

    public PhysEntity(World world, RigidBodyObject.PhysProperties properties, double scale, @NotNull Vector3d pos, @NotNull Vector3d rot) {
        this(world, "phys_ent", properties, scale, pos, rot);
    }

    public PhysEntity(World world, RigidBodyObject.PhysProperties properties, @NotNull Vector3d pos, @NotNull Vector3d rot) {
        this(world, "phys_ent", properties, 1.0d, pos, rot);
    }

    public Vector3d getVelocityVector() {
        return new Vector3d(this.velocityVector);
    }

    public void setVelocityVector(Vector3d vector3d) {
        this.velocityVector.set(vector3d);
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
            this.setVelocityVector(new Vector3d(0.0d));
            if (this.getPosition().y <= -10 || this.getPosition().y >= 500) {
                this.setPosition(new Vector3d(0, 5, 0));
                this.setObjectVelocity(new Vector3d(0.0d));
            }
            if (this.entityState().checkState(EntityState.StateType.IN_WATER)) {
                if (!(this instanceof WorldBrush)) {
                    this.getBulletObject().setObjectLinearVelocity(this.getBulletObject().getObjectLinearVelocity().mul(0.9f));
                    this.getBulletObject().activateObject();
                }
            }
        }
    }
}
