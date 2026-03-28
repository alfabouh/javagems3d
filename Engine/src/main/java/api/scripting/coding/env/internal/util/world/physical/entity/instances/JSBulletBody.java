package api.scripting.coding.env.internal.util.world.physical.entity.instances;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingField;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.math.JSVector3f;
import api.scripting.coding.env.internal.util.world.physical.JSPhysicsWorld;
import api.scripting.coding.env.internal.util.world.physical.entity.JSWorldItem;
import com.jme3.bullet.objects.PhysicsRigidBody;
import javagems3d.physics.entities.bullet.wrappers.BulletBody;
import javagems3d.physics.world.basic.WorldItem;
import javagems3d.physics.world.thread.dynamics.DynamicsUtils;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

@JSCodingClass(binding = "JSBulletBody", description = "Physics body backed by Bullet, with full control over transform, velocity and forces.")
public class JSBulletBody extends JSWorldItem {
    @JSCodingField(description = "Underlying BulletBody (Java side)")
    private final BulletBody bulletBody;

    @JSHideFromDoc
    public JSBulletBody(BulletBody bulletBody) {
        super(new JSPhysicsWorld(bulletBody.getWorld()), new JSVector3f(bulletBody.getPosition()), new JSVector3f(bulletBody.getRotation()), new JSVector3f(bulletBody.getScaling()), bulletBody.getItemName());
        this.bulletBody = bulletBody;
    }

    public JSBulletBody(JSPhysicsWorld world, @NotNull PhysicsRigidBody physicsRigidBody, String itemName) {
        super(world, new JSVector3f(DynamicsUtils.getObjectBodyPos(physicsRigidBody)), new JSVector3f(DynamicsUtils.getObjectBodyRot(physicsRigidBody)), new JSVector3f(DynamicsUtils.getObjectBodyScaling(physicsRigidBody)), itemName);
        this.bulletBody = new BulletBody(world.getJavaPhysicsWorld(), physicsRigidBody, itemName);
    }

    public JSBulletBody(JSPhysicsWorld world, @NotNull PhysicsRigidBody physicsRigidBody) {
        this(world, physicsRigidBody, "js_bullet_ent");
    }

    @JSCodingFunctionOrMethod(description = "Get position")
    public JSVector3f getPosition() {
        Vector3f v = bulletBody.getPosition();
        return new JSVector3f(v.x, v.y, v.z);
    }

    @JSCodingFunctionOrMethod(description = "Set position", paramNames = {"pos"})
    public void setPosition(JSVector3f pos) {
        bulletBody.setPosition(pos.getJavaVector3f());
    }

    @JSCodingFunctionOrMethod(description = "Get rotation")
    public JSVector3f getRotation() {
        Vector3f v = bulletBody.getRotation();
        return new JSVector3f(v.x, v.y, v.z);
    }

    @JSCodingFunctionOrMethod(description = "Set rotation", paramNames = {"rot"})
    public void setRotation(JSVector3f rot) {
        bulletBody.setRotation(rot.getJavaVector3f());
    }

    @JSCodingFunctionOrMethod(description = "Get scaling")
    public JSVector3f getScaling() {
        Vector3f v = bulletBody.getScaling();
        return new JSVector3f(v.x, v.y, v.z);
    }

    @JSCodingFunctionOrMethod(description = "Set scaling", paramNames = {"scale"})
    public void setScaling(JSVector3f scale) {
        bulletBody.setScaling(scale.getJavaVector3f());
    }

    @JSCodingFunctionOrMethod(description = "Get linear velocity")
    public JSVector3f getVelocity() {
        com.jme3.math.Vector3f v = bulletBody.getPhysicsRigidBody().getLinearVelocity(new com.jme3.math.Vector3f());
        return new JSVector3f(v.x, v.y, v.z);
    }

    @JSCodingFunctionOrMethod(description = "Set linear velocity", paramNames = {"vel"})
    public void setVelocity(JSVector3f vel) {
        bulletBody.getPhysicsRigidBody().setLinearVelocity(DynamicsUtils.convertV3F_JME(vel.getJavaVector3f()));
    }

    @JSCodingFunctionOrMethod(description = "Apply impulse", paramNames = {"impulse"})
    public void applyImpulse(JSVector3f impulse) {
        bulletBody.getPhysicsRigidBody().applyCentralImpulse(DynamicsUtils.convertV3F_JME(impulse.getJavaVector3f()));
    }

    @JSCodingFunctionOrMethod(description = "Apply continuous force", paramNames = {"force"})
    public void applyForce(JSVector3f force) {
        bulletBody.getPhysicsRigidBody().applyCentralForce(DynamicsUtils.convertV3F_JME(force.getJavaVector3f()));
    }

    @JSCodingFunctionOrMethod(description = "Apply force at position", paramNames = {"force", "relativePos"})
    public void applyForceAt(JSVector3f force, JSVector3f relativePos) {
        bulletBody.getPhysicsRigidBody().applyForce(DynamicsUtils.convertV3F_JME(force.getJavaVector3f()), DynamicsUtils.convertV3F_JME(relativePos.getJavaVector3f()));
    }

    @JSCodingFunctionOrMethod(description = "Get angular velocity")
    public JSVector3f getAngularVelocity() {
        com.jme3.math.Vector3f v = bulletBody.getPhysicsRigidBody().getAngularVelocity(new com.jme3.math.Vector3f());
        return new JSVector3f(v.x, v.y, v.z);
    }

    @JSCodingFunctionOrMethod(description = "Set angular velocity", paramNames = {"vel"})
    public void setAngularVelocity(JSVector3f vel) {
        bulletBody.getPhysicsRigidBody().setAngularVelocity(DynamicsUtils.convertV3F_JME(vel.getJavaVector3f()));
    }

    @JSCodingFunctionOrMethod(description = "Activate body")
    public void activate() {
        bulletBody.getPhysicsRigidBody().activate();
    }

    @JSCodingFunctionOrMethod(description = "Check if body is active")
    public boolean isActive() {
        return bulletBody.getPhysicsRigidBody().isActive();
    }

    @JSCodingFunctionOrMethod(description = "Destroy entity")
    public void destroy() {
        bulletBody.destroy();
    }

    @JSCodingFunctionOrMethod(description = "Check if entity is dead")
    public boolean isDead() {
        return bulletBody.isDead();
    }

    @JSCodingFunctionOrMethod(description = "Get forward/look vector")
    public JSVector3f getLookVector() {
        Vector3f v = bulletBody.getLookVector();
        return new JSVector3f(v.x, v.y, v.z);
    }

    @JSCodingFunctionOrMethod(description = "Get underlying BulletBody (unsafe)")
    @JSHideFromDoc
    public BulletBody getJavaBulletBody() {
        return bulletBody;
    }

    @JSCodingFunctionOrMethod(description = "Real java object")
    @Override
    public WorldItem getJavaWorldItem() {
        return bulletBody;
    }
}