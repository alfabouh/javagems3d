package ru.alfabouh.engine.physics.world.object;

import org.bytedeco.bullet.BulletCollision.btCollisionShape;
import org.bytedeco.bullet.BulletDynamics.btRigidBody;
import org.bytedeco.bullet.LinearMath.btDefaultMotionState;
import org.bytedeco.bullet.LinearMath.btMotionState;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import ru.alfabouh.engine.physics.collision.AbstractCollision;
import ru.alfabouh.engine.physics.entities.BodyGroup;
import ru.alfabouh.engine.physics.entities.states.EntityState;
import ru.alfabouh.engine.physics.jb_objects.JBulletEntity;
import ru.alfabouh.engine.physics.jb_objects.RigidBodyObject;
import ru.alfabouh.engine.physics.world.IWorld;
import ru.alfabouh.engine.physics.world.World;

public abstract class CollidableWorldItem extends WorldItem implements JBulletEntity {
    private final EntityState entityState;
    private final Vector3d startTranslation;
    private final Vector3d startRotation;
    private final RigidBodyObject.PhysProperties properties;
    private RigidBodyObject rigidBodyObject;
    private RigidBodyConstructor rigidBodyConstructor;

    public CollidableWorldItem(World world, RigidBodyObject.PhysProperties properties, double scale, @NotNull Vector3d startTranslation, @NotNull Vector3d startRotation, String itemName) {
        super(world, scale, startTranslation, startRotation, itemName);
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

    public Vector3d getPosition() {
        return new Vector3d(this.getBulletObject().getTranslation());
    }

    public void setPosition(Vector3d vector3d) {
        this.getBulletObject().setTranslation(vector3d);
    }

    public Vector3d getRotation() {
        return new Vector3d(this.getBulletObject().getRotation());
    }

    public void setRotation(Vector3d vector3d) {
        this.getBulletObject().setRotation(vector3d);
    }

    public void setScale(double scale) {
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

    private void createRigidBody(World world, @NotNull Vector3d position, @NotNull Vector3d rotation, double scaling, RigidBodyObject.PhysProperties properties) {
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

    @Override
    public BodyGroup getBodyIndex() {
        return BodyGroup.RIGID_BODY;
    }

    public EntityState entityState() {
        return this.entityState;
    }

    public void applyCentralForce(Vector3d vector3d) {
        this.getBulletObject().applyCentralForce(vector3d);
    }

    public double getObjectSpeed() {
        return this.getObjectVelocity().length();
    }

    public Vector3d getObjectVelocity() {
        return this.getBulletObject().getObjectLinearVelocity();
    }

    public void setObjectVelocity(Vector3d vector3d) {
        this.getBulletObject().setObjectLinearVelocity(vector3d);
    }

    public void addObjectVelocity(Vector3d vector3d) {
        this.getBulletObject().addObjectLinearVelocity(vector3d);
    }

    public static class RigidBodyConstructor {
        private final btCollisionShape btCollisionShape;
        private final btMotionState motionState;
        private final World world;
        private btRigidBody.btRigidBodyConstructionInfo btRigidBodyConstructionInfo;

        public RigidBodyConstructor(World world, double scaling, AbstractCollision abstractCollision) {
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