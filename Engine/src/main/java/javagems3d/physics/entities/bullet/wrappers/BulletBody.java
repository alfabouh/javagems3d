package javagems3d.physics.entities.bullet.wrappers;

import com.jme3.bullet.objects.PhysicsRigidBody;
import javagems3d.physics.world.IWorld;
import javagems3d.physics.world.PhysicsWorld;
import javagems3d.physics.world.basic.WorldItem;
import javagems3d.physics.world.thread.dynamics.DynamicsUtils;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class BulletBody extends WorldItem {
    private final PhysicsRigidBody physicsRigidBody;

    public BulletBody(PhysicsWorld world, @NotNull PhysicsRigidBody physicsRigidBody, String itemName) {
        super(world, DynamicsUtils.getObjectBodyPos(physicsRigidBody), DynamicsUtils.getObjectBodyRot(physicsRigidBody), DynamicsUtils.getObjectBodyScaling(physicsRigidBody), itemName);
        this.physicsRigidBody = physicsRigidBody;
    }

    public BulletBody(PhysicsWorld world, @NotNull PhysicsRigidBody physicsRigidBody) {
        this(world, physicsRigidBody, "bullet_ent");
    }

    @Override
    public void onSpawn(IWorld iWorld) {
        PhysicsWorld world = (PhysicsWorld) iWorld;
        super.onSpawn(iWorld);
        world.getDynamics().addCollisionObject(this.getPhysicsRigidBody());
    }

    @Override
    public void onDestroy(IWorld iWorld) {
        PhysicsWorld world = (PhysicsWorld) iWorld;
        super.onDestroy(iWorld);
        world.getDynamics().removeCollisionObject(this.getPhysicsRigidBody());
    }

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

    public PhysicsRigidBody getPhysicsRigidBody() {
        return this.physicsRigidBody;
    }
}