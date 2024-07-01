package ru.alfabouh.jgems3d.engine.physics.world.object;

import org.bytedeco.bullet.BulletCollision.btCollisionShape;
import org.bytedeco.bullet.BulletDynamics.btRigidBody;
import org.bytedeco.bullet.LinearMath.btDefaultMotionState;
import org.bytedeco.bullet.LinearMath.btMotionState;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import ru.alfabouh.jgems3d.engine.physics.collision.base.AbstractCollision;
import ru.alfabouh.jgems3d.engine.physics.objects.states.EntityState;
import ru.alfabouh.jgems3d.engine.physics.jb_objects.JBulletEntity;
import ru.alfabouh.jgems3d.engine.physics.jb_objects.RigidBodyObject;
import ru.alfabouh.jgems3d.engine.physics.world.IWorld;
import ru.alfabouh.jgems3d.engine.physics.world.World;

public abstract class CollidableWorldItem extends WorldItem implements JBulletEntity {
    private final EntityState entityState;
    private final Vector3f startTranslation;
    private final Vector3f startRotation;
    private final RigidBodyObject.PhysProperties properties;
    private RigidBodyObject rigidBodyObject;
    private RigidBodyConstructor rigidBodyConstructor;

    public CollidableWorldItem(World world, RigidBodyObject.PhysProperties properties, Vector3f startTranslation, Vector3f startRotation, Vector3f scale, String itemName) {
        super(world, startTranslation, startRotation, scale, itemName);
        this.properties = properties;
        this.startTranslation = startTranslation;
        this.startRotation = startRotation;
        this.entityState = new EntityState();
    }

    protected abstract AbstractCollision constructCollision();

    public void onSpawn(IWorld iWorld) {
        super.onSpawn(iWorld);
        this.constructRigidBody();
    }

    @Override
    public void onDestroy(IWorld iWorld) {
        super.onDestroy(iWorld);
        ((World) iWorld).getBulletTimer().removeCollisionObjectFromWorld(this.getBulletObject());
    }

    public Vector3f getPosition() {
        return new Vector3f(this.getBulletObject().getTranslation());
    }

    public void setPosition(Vector3f Vector3f) {
        if (Vector3f == null) {
            return;
        }
        this.getBulletObject().setTranslation(Vector3f);
    }

    public Vector3f getRotation() {
        return new Vector3f(this.getBulletObject().getRotation());
    }

    public void setRotation(Vector3f Vector3f) {
        if (Vector3f == null) {
            return;
        }
        this.getBulletObject().setRotation(Vector3f);
    }

    public void setScale(Vector3f scale) {
        if (scale == null) {
            return;
        }
        super.setScale(scale);
        if (this.isValid()) {
            this.getBulletObject().setScaling(scale);
        }
    }

    protected void constructRigidBody() {
        this.createRigidBody(this.getWorld(), this.startTranslation, this.startRotation, this.getScale(), this.properties);
        this.addCallBacks(this.getBulletObject());
    }

    protected RigidBodyConstructor getRigidBodyConstructor() {
        return this.rigidBodyConstructor;
    }

    private void createRigidBody(World world, @NotNull Vector3f position, @NotNull Vector3f rotation, Vector3f scaling, RigidBodyObject.PhysProperties properties) {
        this.rigidBodyConstructor = new RigidBodyConstructor(world, scaling, this.constructCollision());
        this.rigidBodyObject = this.getRigidBodyConstructor().buildRigidBody(properties);
        if (this.getBodyIndex().isStatic()) {
            this.getBulletObject().makeStatic();
        } else {
            this.getBulletObject().makeDynamic();
        }
        world.addInBulletWorld(this.getBulletObject(), this.getBodyIndex());
        this.getBulletObject().setUserIndex2(this.getItemId());
        this.getBulletObject().setTranslation(position);
        this.getBulletObject().setRotation(rotation);
        this.getBulletObject().updateCollisionObjectState();
        this.afterRigidBodyCreated(this.getBulletObject());
        world.getDynamicsWorld().updateSingleAabb(this.getBulletObject());
    }

    protected void afterRigidBodyCreated(RigidBodyObject rigidBodyObject) {
    }

    protected void addCallBacks(RigidBodyObject rigidBodyObject) {
    }

    public synchronized RigidBodyObject getBulletObject() {
        return this.rigidBodyObject;
    }

    public EntityState entityState() {
        return this.entityState;
    }

    public void applyCentralForce(Vector3f Vector3f) {
        this.getBulletObject().applyCentralForce(Vector3f);
    }

    public float getObjectSpeed() {
        return this.getObjectVelocity().length();
    }

    public Vector3f getObjectVelocity() {
        return this.getBulletObject().getObjectLinearVelocity();
    }

    public void setObjectVelocity(Vector3f Vector3f) {
        this.getBulletObject().setObjectLinearVelocity(Vector3f);
    }

    public void addObjectVelocity(Vector3f Vector3f) {
        this.getBulletObject().addObjectLinearVelocity(Vector3f);
    }

    public static class RigidBodyConstructor {
        private final btCollisionShape btCollisionShape;
        private final btMotionState motionState;
        private final World world;
        private btRigidBody.btRigidBodyConstructionInfo btRigidBodyConstructionInfo;

        public RigidBodyConstructor(World world, Vector3f scaling, AbstractCollision abstractCollision) {
            this.btCollisionShape = abstractCollision.buildCollisionShape(scaling);
            this.motionState = new btDefaultMotionState();
            this.world = world;
        }

        public RigidBodyObject buildRigidBody(RigidBodyObject.PhysProperties physicsProperties) {
            this.btRigidBodyConstructionInfo = new btRigidBody.btRigidBodyConstructionInfo(1.0d, this.getMotionState(), this.getBtCollisionShape(), null);
            RigidBodyObject rigidBodyObject1 = new RigidBodyObject(this.world, this.getBtRigidBodyConstructionInfo());
            rigidBodyObject1.setRigidBodyProperties(physicsProperties);
            return rigidBodyObject1;
        }

        public btCollisionShape getBtCollisionShape() {
            return this.btCollisionShape;
        }

        public btMotionState getMotionState() {
            return this.motionState;
        }

        public btRigidBody.btRigidBodyConstructionInfo getBtRigidBodyConstructionInfo() {
            return this.btRigidBodyConstructionInfo;
        }
    }
}