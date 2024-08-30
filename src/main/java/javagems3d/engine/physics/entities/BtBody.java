/*
 * *
 *  * @author alfabouh
 *  * @since 2024
 *  * @link https://github.com/alfabouh/JavaGems3D
 *  *
 *  * This software is provided 'as-is', without any express or implied warranty.
 *  * In no event will the authors be held liable for any damages arising from the use of this software.
 *
 */

package javagems3d.engine.physics.entities;

import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.objects.PhysicsRigidBody;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import javagems3d.engine.physics.colliders.IColliderConstructor;
import javagems3d.engine.physics.entities.properties.collision.CollisionFilter;
import javagems3d.engine.physics.entities.properties.material.PhysMaterial;
import javagems3d.engine.physics.entities.properties.state.EntityState;
import javagems3d.engine.physics.world.IWorld;
import javagems3d.engine.physics.world.PhysicsWorld;
import javagems3d.engine.physics.world.basic.IWorldTicked;
import javagems3d.engine.physics.world.basic.WorldItem;
import javagems3d.engine.physics.world.thread.dynamics.DynamicsSystem;
import javagems3d.engine.physics.world.thread.dynamics.DynamicsUtils;
import javagems3d.engine.physics.world.triggers.ITriggerAction;

public abstract class BtBody extends WorldItem implements IBtEntity, IWorldTicked {
    private EntityState entityState;
    private boolean canBeDestroyed;
    private JGemsPhysicsRigidBody physicsRigidBody;

    public BtBody(PhysicsWorld world, @NotNull Vector3f pos, @NotNull Vector3f rot, @NotNull Vector3f scaling, String itemName) {
        super(world, pos, rot, scaling, itemName);
        this.entityState = new EntityState();
        this.canBeDestroyed = true;
    }

    public void setMaterial(PhysMaterial material) {
        this.getPhysicsRigidBody().setMaterial(material);
    }

    public void makeDynamic() {
        this.getPhysicsRigidBody().makeDynamic(PhysMaterial.createDefaultMaterial());
    }

    public void makeDynamic(PhysMaterial material) {
        this.getPhysicsRigidBody().makeDynamic(material);
    }

    public void makeStatic() {
        this.getPhysicsRigidBody().makeStatic(PhysMaterial.createDefaultMaterial());
    }

    public void makeStatic(PhysMaterial material) {
        this.getPhysicsRigidBody().makeStatic(material);
    }

    public void setCollisionFilterNegative(CollisionFilter... noCheckCollisionFilters) {
        int i = 0;
        for (CollisionFilter collisionFilter : noCheckCollisionFilters) {
            i |= collisionFilter.getMask();
        }
        this.getPhysicsRigidBody().setCollideWithGroups(CollisionFilter.ALL.getMask() & ~i);
    }

    public BtBody setCanBeDestroyed(boolean flag) {
        this.canBeDestroyed = flag;
        return this;
    }

    @Override
    public ITriggerAction onColliding() {
        return null;
    }

    @Override
    public final void onUpdate(IWorld iWorld) {
        this.onTick(iWorld);
        this.getEntityState().removeState(EntityState.Type.IN_LIQUID);
    }

    protected void onTick(IWorld iWorld) {
        if (this.getPosition().y < -50.0f) {
            this.resetWarp();
        }
        if (this.getEntityState().checkState(EntityState.Type.IN_LIQUID)) {
            this.getPhysicsRigidBody().slowDownLinearVelocity(0.9f);
            this.getPhysicsRigidBody().slowDownAngularVelocity(0.9f);
        }
    }

    @Override
    public void onSpawn(IWorld iWorld) {
        PhysicsWorld world = (PhysicsWorld) iWorld;
        super.onSpawn(iWorld);
        this.init(world.getDynamics());
        world.getDynamics().addCollisionObject(this.getPhysicsRigidBody());
    }

    @Override
    public void onDestroy(IWorld iWorld) {
        PhysicsWorld world = (PhysicsWorld) iWorld;
        super.onDestroy(iWorld);
        world.getDynamics().removeCollisionObject(this.getPhysicsRigidBody());
    }

    protected void init(DynamicsSystem dynamicsSystem) {
        this.physicsRigidBody = new JGemsPhysicsRigidBody(this.constructCollision().createGeom(dynamicsSystem));

        this.getPhysicsRigidBody().setContactStiffness(Float.MAX_VALUE);
        this.getPhysicsRigidBody().setContactDamping(0.0f);
        this.getPhysicsRigidBody().setUserObject(this);
        this.setCollisionFilter(CollisionFilter.ALL);

        this.postInit(dynamicsSystem, this.getPhysicsRigidBody());

        DynamicsUtils.transformRigidBody(this.getPhysicsRigidBody(), this.startPos, this.startRot, this.startScaling);
    }

    protected abstract void postInit(DynamicsSystem dynamicsSystem, JGemsPhysicsRigidBody jGemsPhysicsRigidBody);

    protected abstract IColliderConstructor constructCollision();

    @Override
    public Vector3f getScaling() {
        return DynamicsUtils.getObjectBodyScaling(this.getPhysicsRigidBody());
    }

    public void setScaling(Vector3f scaling) {
        DynamicsUtils.scaleRigidBody(this.getPhysicsRigidBody(), scaling);
    }

    @Override
    public Vector3f getPosition() {
        return DynamicsUtils.getObjectBodyPos(this.getPhysicsRigidBody());
    }

    public void setPosition(Vector3f vector3f) {
        DynamicsUtils.translateRigidBody(this.getPhysicsRigidBody(), vector3f);
    }

    @Override
    public Vector3f getRotation() {
        return DynamicsUtils.getObjectBodyRot(this.getPhysicsRigidBody());
    }

    public void setRotation(Vector3f vector3f) {
        DynamicsUtils.rotateRigidBody(this.getPhysicsRigidBody(), vector3f);
    }

    public int getCollisionGroup() {
        return this.getPhysicsRigidBody().getCollisionGroup();
    }

    public void setCollisionGroup(CollisionFilter... collisionFilters) {
        int i = 0;
        for (CollisionFilter collisionFilter : collisionFilters) {
            i |= collisionFilter.getMask();
        }
        this.getPhysicsRigidBody().setCollisionGroup(i);
    }

    public int getCollisionFilter() {
        return this.getPhysicsRigidBody().getCollideWithGroups();
    }

    public void setCollisionFilter(CollisionFilter... collisionFilters) {
        int i = 0;
        for (CollisionFilter collisionFilter : collisionFilters) {
            i |= collisionFilter.getMask();
        }
        this.getPhysicsRigidBody().setCollideWithGroups(i);
    }

    public EntityState getEntityState() {
        return this.entityState;
    }

    public void setEntityState(@NotNull EntityState state) {
        this.entityState = state;
    }

    @Override
    public boolean canBeDestroyed() {
        return this.canBeDestroyed;
    }

    public JGemsPhysicsRigidBody getPhysicsRigidBody() {
        return this.physicsRigidBody;
    }

    public static class JGemsPhysicsRigidBody extends PhysicsRigidBody {
        private float saveDensity = -1.0f;


        public JGemsPhysicsRigidBody(CollisionShape shape) {
            super(shape);
        }

        public JGemsPhysicsRigidBody(CollisionShape shape, float mass) {
            super(shape, mass);
        }

        public void slowDownLinearVelocity(float val) {
            com.jme3.math.Vector3f vector3f1 = new com.jme3.math.Vector3f();
            this.getLinearVelocity(vector3f1);
            if (vector3f1.length() <= 0.0f) {
                return;
            }
            Vector3f newV = DynamicsUtils.convertV3F_JOML(vector3f1);
            newV.mul(val);
            this.setLinearVelocity(DynamicsUtils.convertV3F_JME(newV));
        }

        public void slowDownAngularVelocity(float val) {
            com.jme3.math.Vector3f vector3f1 = new com.jme3.math.Vector3f();
            this.getAngularVelocity(vector3f1);
            if (vector3f1.length() <= 0.0f) {
                return;
            }
            Vector3f newV = DynamicsUtils.convertV3F_JOML(vector3f1);
            newV.mul(val);
            this.setAngularVelocity(DynamicsUtils.convertV3F_JME(newV));
        }

        public void addLinearVelocity(Vector3f val) {
            com.jme3.math.Vector3f vector3f1 = new com.jme3.math.Vector3f();
            this.getLinearVelocity(vector3f1);
            Vector3f newV = DynamicsUtils.convertV3F_JOML(vector3f1);
            newV.add(val);
            this.setLinearVelocity(DynamicsUtils.convertV3F_JME(newV));
        }

        public void addAngularVelocity(Vector3f val) {
            com.jme3.math.Vector3f vector3f1 = new com.jme3.math.Vector3f();
            this.getAngularVelocity(vector3f1);
            Vector3f newV = DynamicsUtils.convertV3F_JOML(vector3f1);
            newV.add(val);
            this.setAngularVelocity(DynamicsUtils.convertV3F_JME(newV));
        }

        @Override
        public void setPhysicsScale(com.jme3.math.Vector3f newScale) {
            super.setPhysicsScale(newScale);
            this.reCalcDensity();
        }

        public void reCalcDensity() {
            this.setMass(this.calcMass(this.saveDensity));
        }

        public void setMaterial(PhysMaterial material) {
            this.setFriction(material.friction);
            this.setMass(this.calcMass(material.m_density));
            this.setLinearDamping(material.l_damping);
            this.setAngularDamping(material.a_damping);
            this.saveDensity = material.m_density;
        }

        public float calcMass(float density) {
            if (density <= 0.0f || this.isKinematic()) {
                return 0.0f;
            }
            BoundingBox boundingBox = new BoundingBox();
            this.boundingBox(boundingBox);
            com.jme3.math.Vector3f min = new com.jme3.math.Vector3f();
            com.jme3.math.Vector3f max = new com.jme3.math.Vector3f();
            boundingBox.getMin(min);
            boundingBox.getMax(max);
            float X = (max.x - min.x);
            float Y = (max.y - min.y);
            float Z = (max.z - min.z);

            float V = (X) * (Y) * (Z);

            return density * V;
        }

        public void makeDynamic(PhysMaterial material) {
            this.setKinematic(false);
            this.setMaterial(material);
        }

        public void makeStatic(PhysMaterial material) {
            this.setKinematic(true);
            this.setMaterial(material);
        }
    }
}
