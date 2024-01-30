package ru.BouH.engine.physics.jb_objects;

import org.bytedeco.bullet.BulletCollision.btCollisionObject;
import org.bytedeco.bullet.BulletCollision.btCollisionShape;
import org.bytedeco.bullet.BulletDynamics.btRigidBody;
import org.bytedeco.bullet.LinearMath.btQuaternion;
import org.bytedeco.bullet.LinearMath.btTransform;
import org.bytedeco.bullet.LinearMath.btVector3;
import org.joml.Vector3d;
import ru.BouH.engine.math.MathHelper;
import ru.BouH.engine.physics.collision.AbstractCollision;
import ru.BouH.engine.physics.entities.BodyGroup;
import ru.BouH.engine.physics.entities.Materials;
import ru.BouH.engine.physics.world.World;

public class RigidBodyObject extends btRigidBody {
    public static final Double STIFFNESS = 1.0e+22d;
    public static final Double DAMPING = 1.0e+20d;
    private final World world;

    public RigidBodyObject(World world, btRigidBodyConstructionInfo constructionInfo) {
        super(constructionInfo);
        this.world = world;
    }

    public World getWorld() {
        return this.world;
    }

    protected void onRigidBodyCreated(PhysProperties physicsProperties) {
        this.setRigidBodyProperties(physicsProperties);
    }

    public Vector3d getRotation() {
        try (btQuaternion quaternion = this.getOrientation()) {
            double[] x = new double[1];
            double[] y = new double[1];
            double[] z = new double[1];
            quaternion.getEulerZYX(z, y, x);
            quaternion.deallocate();
            return new Vector3d(Math.toDegrees(z[0]), Math.toDegrees(y[0]), Math.toDegrees(x[0])).negate();
        }
    }

    public void setRotation(Vector3d angles) {
        try (btTransform transform = this.getWorldTransform()) {
            btQuaternion quaternion = new btQuaternion();
            quaternion.setEulerZYX(angles.z, angles.y, angles.x);
            transform.setRotation(quaternion);
            this.setWorldTransform(transform);
            this.updateCollisionObjectState();
            this.getWorld().getBulletTimer().updateRigidBodyAabb(this);
        }
        this.getWorld().getDynamicsWorld().synchronizeMotionStates();
    }

    public Vector3d getTranslation() {
        try (btTransform transform = this.getWorldTransform()) {
            return new Vector3d(transform.getOrigin().getX(), transform.getOrigin().getY(), transform.getOrigin().getZ());
        }
    }

    public void setTranslation(Vector3d pos) {
        try (btTransform transform = this.getWorldTransform()) {
            transform.setOrigin(new btVector3(pos.x, pos.y, pos.z));
            this.setWorldTransform(transform);
            this.updateCollisionObjectState();
            this.getWorld().getBulletTimer().updateRigidBodyAabb(this);
        }
        this.getWorld().getDynamicsWorld().synchronizeMotionStates();
    }

    public void updateCollisionObjectState() {
        this.activateObject();
        this.updateInertiaTensor();
    }

    public void activateObject() {
        this.activate();
    }

    protected double calcCCDRadius() {
        btVector3 v1 = new btVector3();
        btVector3 v2 = new btVector3();
        this.getAabb(v1, v2);
        double d1 = (v2.getY() - v1.getY()) / 10.0d;
        v1.deallocate();
        v2.deallocate();
        return d1;
    }

    public void applyPhysProperties(PhysProperties properties) {
        this.setRigidBodyProperties(properties);
    }

    public void applyMaterial(Materials.MaterialProperties materialProperties) {
        this.setRigidBodyProperties(materialProperties);
    }

    public void refreshObjectProperties(PhysProperties properties) {
        this.setRigidBodyProperties(properties);
    }

    protected void setRigidBodyProperties(Materials.MaterialProperties materialProperties) {
        this.setRigidBodyProperties(new PhysProperties(materialProperties, false, 1.0d));
    }

    public void setCollision(AbstractCollision abstractCollision) {
        this.setCollision(1.0d, abstractCollision);
    }

    public void setCollision(double scaling, AbstractCollision abstractCollision) {
        if (abstractCollision != null) {
            if (this.getCollisionShape() != null) {
                this.setCollisionShape(null);
                this.getCollisionShape().deallocate();
            }
            this.setCollisionShape(abstractCollision.buildCollisionShape(scaling));
            this.updateCollisionObjectState();
        }
    }

    public void setBodyIndex(BodyGroup bodyGroup) {
        this.setUserIndex(bodyGroup.getIndex());
    }

    public void setRigidBodyProperties(PhysProperties properties) {
        this.setCollisionFlags((this.getCollisionFlags() | btCollisionObject.CF_CUSTOM_MATERIAL_CALLBACK | btCollisionObject.CF_HAS_CONTACT_STIFFNESS_DAMPING) & ~btCollisionObject.CF_DISABLE_SPU_COLLISION_PROCESSING);
        this.setContactStiffnessAndDamping(RigidBodyObject.STIFFNESS, RigidBodyObject.DAMPING);
        this.makeDynamic();
        this.setFrictionAxes(properties.getMaterialProperties().getFrictionAxes());
        this.setFriction(properties.getMaterialProperties().getFriction());
        this.setLinearAngularDamping(properties.getMaterialProperties().getL_damping(), properties.getMaterialProperties().getA_damping());
        this.setRestitution(properties.getMaterialProperties().getRestitution());
        if (properties.isRealisticInertia() && !this.isStaticObject()) {
            this.setRealisticInertia();
        } else {
            this.setMass(properties.getMass());
            this.setInertia(new Vector3d(0.0d));
        }
        this.enableCCD(1.0e-3d, this.calcCCDRadius());
        this.updateCollisionObjectState();
    }

    public btVector3 calcRealisticInertia() {
        btCollisionShape collisionShape = RigidBodyObject.this.getCollisionShape();
        btVector3 bpVector3f = new btVector3();
        collisionShape.calculateLocalInertia(this.getMass(), bpVector3f);
        return bpVector3f;
    }

    public btVector3 getInertia() {
        return this.getLocalInertia();
    }

    public void setInertia(Vector3d vector3d) {
        this.setMassProps(this.getMass(), MathHelper.convert(vector3d));
    }

    public Vector3d getFrictionAxes() {
        btVector3 b1 = this.getAnisotropicFriction();
        return MathHelper.convert(b1);
    }

    public void setFrictionAxes(Vector3d axes) {
        this.setAnisotropicFriction(MathHelper.convert(axes), btCollisionObject.CF_ANISOTROPIC_FRICTION);
    }

    public void addObjectLinearVelocity(Vector3d vector3d) {
        Vector3d vector3d1 = this.getObjectLinearVelocity();
        vector3d1.add(vector3d);
        this.setLinearVelocity(MathHelper.convert(vector3d1));
    }

    public Vector3d getObjectLinearVelocity() {
        btVector3 btVector3 = this.getLinearVelocity();
        return MathHelper.convert(btVector3);
    }

    public void setObjectLinearVelocity(Vector3d vector3d) {
        this.setLinearVelocity(MathHelper.convert(vector3d));
    }

    public void makeStatic() {
        this.setCollisionFlags((this.getCollisionFlags() | btCollisionObject.CF_STATIC_OBJECT) & ~btCollisionObject.CF_DYNAMIC_OBJECT);
    }

    public void makeDynamic() {
        this.setCollisionFlags((this.getCollisionFlags() | btCollisionObject.CF_DYNAMIC_OBJECT) & ~btCollisionObject.CF_STATIC_OBJECT);
    }

    public void applyCentralForce(Vector3d vector3d) {
        this.applyCentralForce(MathHelper.convert(vector3d));
    }

    public void setMass(double mass) {
        this.setMassProps(mass, this.getInertia());
    }

    public void setRealisticInertia() {
        this.setMassProps(this.getMass(), this.calcRealisticInertia());
    }

    public void setLinearAngularDamping(double v1, double v2) {
        this.setDamping(v1, v2);
    }

    protected void enableCCD(double d1, double d2) {
        this.setCcdMotionThreshold((float) d1);
        this.setCcdSweptSphereRadius((float) d2);
    }

    protected void disableCCD() {
        this.setCcdMotionThreshold(0.0d);
        this.setCcdSweptSphereRadius(0.0d);
    }

    public Vector3d getScaling() {
        btVector3 btVector3 = this.getCollisionShape().getLocalScaling();
        return MathHelper.convert(btVector3);
    }

    public void setScaling(double scaling) {
        this.getCollisionShape().setLocalScaling(new btVector3(scaling, scaling, scaling));
        this.updateCollisionObjectState();
    }

    public static final class PhysProperties {
        private final Materials.MaterialProperties materialProperties;
        private final double mass;
        private final boolean realisticInertia;

        private PhysProperties(Materials.MaterialProperties materialProperties, boolean realisticInertia, double mass) {
            this.materialProperties = materialProperties;
            this.mass = mass;
            this.realisticInertia = realisticInertia;
        }

        public static PhysProperties createProperties(Materials.MaterialProperties materialProperties, boolean realisticInertia, double mass) {
            return new PhysProperties(materialProperties, realisticInertia, mass);
        }

        public static PhysProperties createProperties(Materials.MaterialProperties materialProperties) {
            return new PhysProperties(materialProperties, false, 1.0d);
        }

        public static PhysProperties createProperties() {
            return PhysProperties.createProperties(Materials.defaultMaterial);
        }

        public boolean isRealisticInertia() {
            return this.realisticInertia;
        }

        public Materials.MaterialProperties getMaterialProperties() {
            return this.materialProperties;
        }

        public double getMass() {
            return this.mass;
        }
    }
}