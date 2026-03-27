package api.scripting.coding.env.internal.util.world.physical.entity.instances;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingField;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.internal.util.math.JSVector3f;
import api.scripting.coding.env.internal.util.world.physical.JSPhysicsWorld;
import api.scripting.coding.env.internal.util.world.physical.entity.JSAbstractWorldItem;
import com.jme3.bullet.objects.PhysicsRigidBody;
import javagems3d.physics.entities.bullet.wrappers.BulletBody;
import javagems3d.physics.world.thread.dynamics.DynamicsUtils;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

@JSCodingClass(binding = "JSBulletBody", description = "...")
public class JSBulletBody extends JSAbstractWorldItem {
    @JSCodingField(description = "...") private final BulletBody bulletBody;

    public JSBulletBody(JSPhysicsWorld world, @NotNull PhysicsRigidBody physicsRigidBody, String itemName) {
        super(world, new JSVector3f(DynamicsUtils.getObjectBodyPos(physicsRigidBody)), new JSVector3f(DynamicsUtils.getObjectBodyRot(physicsRigidBody)), new JSVector3f(DynamicsUtils.getObjectBodyScaling(physicsRigidBody)), itemName);
        this.bulletBody = new BulletBody(world.getJavaPhysicsWorld(), physicsRigidBody, itemName);
    }

    public JSBulletBody(JSPhysicsWorld world, @NotNull PhysicsRigidBody physicsRigidBody) {
        this(world, physicsRigidBody, "js_bullet_ent");
    }

    @JSCodingFunctionOrMethod(description = "Get underlying BulletBody (unsafe, internal use)")
    public BulletBody getJavaBulletBody() {
        return this.bulletBody;
    }

    @JSCodingFunctionOrMethod(description = "Get position")
    public JSVector3f getPosition() {
        Vector3f p = this.bulletBody.getPosition();
        return new JSVector3f(p.x, p.y, p.z);
    }

    @JSCodingFunctionOrMethod(description = "Set position", paramNames = {"pos"})
    public void setPosition(JSVector3f pos) {
        this.bulletBody.setPosition(pos.getJavaVector3f());
    }

    @JSCodingFunctionOrMethod(description = "Get rotation")
    public JSVector3f getRotation() {
        Vector3f r = this.bulletBody.getRotation();
        return new JSVector3f(r.x, r.y, r.z);
    }

    @JSCodingFunctionOrMethod(description = "Set rotation", paramNames = {"rot"})
    public void setRotation(JSVector3f rot) {
        this.bulletBody.setRotation(rot.getJavaVector3f());
    }

    @JSCodingFunctionOrMethod(description = "Get scaling")
    public JSVector3f getScaling() {
        Vector3f s = this.bulletBody.getScaling();
        return new JSVector3f(s.x, s.y, s.z);
    }

    @JSCodingFunctionOrMethod(description = "Set scaling", paramNames = {"scale"})
    public void setScaling(JSVector3f scale) {
        this.bulletBody.setScaling(scale.getJavaVector3f());
    }

    @JSCodingFunctionOrMethod(description = "Apply central force", paramNames = {"force"})
    public void applyForce(JSVector3f force) {
        this.bulletBody.getPhysicsRigidBody().applyCentralForce(DynamicsUtils.convertV3F_JME(force.getJavaVector3f()));
    }

    @JSCodingFunctionOrMethod(description = "Apply impulse", paramNames = {"impulse"})
    public void applyImpulse(JSVector3f impulse) {
        this.bulletBody.getPhysicsRigidBody().applyCentralImpulse(DynamicsUtils.convertV3F_JME(impulse.getJavaVector3f()));
    }

    @JSCodingFunctionOrMethod(description = "Apply force at position", paramNames = {"force", "relativePos"})
    public void applyForceAt(JSVector3f force, JSVector3f relativePos) {
        this.bulletBody.getPhysicsRigidBody().applyForce(DynamicsUtils.convertV3F_JME(force.getJavaVector3f()), DynamicsUtils.convertV3F_JME(relativePos.getJavaVector3f()));
    }

    @JSCodingFunctionOrMethod(description = "Get linear velocity")
    public JSVector3f getVelocity() {
        com.jme3.math.Vector3f v = this.bulletBody.getPhysicsRigidBody().getLinearVelocity(new com.jme3.math.Vector3f());
        return new JSVector3f(v.x, v.y, v.z);
    }

    @JSCodingFunctionOrMethod(description = "Set linear velocity", paramNames = {"vel"})
    public void setVelocity(JSVector3f vel) {
        this.bulletBody.getPhysicsRigidBody().setLinearVelocity(DynamicsUtils.convertV3F_JME(vel.getJavaVector3f()));
    }

    @JSCodingFunctionOrMethod(description = "Get angular velocity")
    public JSVector3f getAngularVelocity() {
        com.jme3.math.Vector3f v = this.bulletBody.getPhysicsRigidBody().getAngularVelocity(new com.jme3.math.Vector3f());
        return new JSVector3f(v.x, v.y, v.z);
    }

    @JSCodingFunctionOrMethod(description = "Set angular velocity", paramNames = {"vel"})
    public void setAngularVelocity(JSVector3f vel) {
        this.bulletBody.getPhysicsRigidBody().setAngularVelocity(DynamicsUtils.convertV3F_JME(vel.getJavaVector3f()));
    }

    @JSCodingFunctionOrMethod(description = "Activate body (wake up)")
    public void activate() {
        this.bulletBody.getPhysicsRigidBody().activate();
    }

    @JSCodingFunctionOrMethod(description = "Check if body is active")
    public boolean isActive() {
        return this.bulletBody.getPhysicsRigidBody().isActive();
    }

    @JSCodingFunctionOrMethod(description = "Destroy entity")
    public void destroy() {
        this.bulletBody.destroy();
    }

    @JSCodingFunctionOrMethod(description = "Check if entity is dead")
    public boolean isDead() {
        return this.bulletBody.isDead();
    }

    @JSCodingFunctionOrMethod(description = "Get forward direction")
    public JSVector3f getLookVector() {
        Vector3f v = this.bulletBody.getLookVector();
        return new JSVector3f(v.x, v.y, v.z);
    }
}