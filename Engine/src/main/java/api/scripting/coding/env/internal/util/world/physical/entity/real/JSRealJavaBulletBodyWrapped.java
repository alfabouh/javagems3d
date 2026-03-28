package api.scripting.coding.env.internal.util.world.physical.entity.real;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingConstructor;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.math.JSVector3f;
import api.scripting.coding.env.internal.util.world.physical.entity.JSWorldItemI;
import com.jme3.bullet.objects.PhysicsRigidBody;
import javagems3d.physics.entities.bullet.wrappers.BulletBody;
import javagems3d.physics.world.PhysicsWorld;
import javagems3d.physics.world.basic.IWorldObject;
import javagems3d.physics.world.basic.WorldItem;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

@JSCodingClass(binding = "JSRealJavaBulletBodyWrapped", description = "Physics-based world object backed by a rigid body. Provides access to position, rotation and scaling through physics simulation.")
public abstract class JSRealJavaBulletBodyWrapped extends BulletBody implements JSWorldItemI {

    @JSCodingConstructor(description = "Create bullet body with physics world, rigid body and name", paramNames = {"world", "body", "itemName"})
    public JSRealJavaBulletBodyWrapped(PhysicsWorld world, @NotNull PhysicsRigidBody body, String itemName) {
        super(world, body, itemName);
    }

    @JSCodingConstructor(description = "Create bullet body with physics world and rigid body (default name)", paramNames = {"world", "body"})
    public JSRealJavaBulletBodyWrapped(PhysicsWorld world, @NotNull PhysicsRigidBody body) {
        super(world, body);
    }

    @JSCodingFunctionOrMethod(description = "Get position from physics body")
    public JSVector3f getPositionJS() {
        Vector3f p = super.getPosition();
        return new JSVector3f(p.x, p.y, p.z);
    }

    @JSCodingFunctionOrMethod(description = "Set position to physics body", paramNames = {"pos"})
    public void setPositionJS(JSVector3f pos) {
        super.setPosition(pos.getJavaVector3f());
    }

    @JSCodingFunctionOrMethod(description = "Get rotation from physics body")
    public JSVector3f getRotationJS() {
        Vector3f r = super.getRotation();
        return new JSVector3f(r.x, r.y, r.z);
    }

    @JSCodingFunctionOrMethod(description = "Set rotation to physics body", paramNames = {"rot"})
    public void setRotationJS(JSVector3f rot) {
        super.setRotation(rot.getJavaVector3f());
    }

    @JSCodingFunctionOrMethod(description = "Get scaling from physics body")
    public JSVector3f getScalingJS() {
        Vector3f s = super.getScaling();
        return new JSVector3f(s.x, s.y, s.z);
    }

    @JSCodingFunctionOrMethod(description = "Set scaling to physics body", paramNames = {"scale"})
    public void setScalingJS(JSVector3f scale) {
        super.setScaling(scale.getJavaVector3f());
    }

    @JSCodingFunctionOrMethod(description = "Get forward/look direction")
    public JSVector3f getLookVector3() {
        Vector3f v = super.getLookVector();
        return new JSVector3f(v.x, v.y, v.z);
    }

    @JSCodingFunctionOrMethod(description = "Destroy object and remove physics body from world")
    public void destroy() {
        super.destroy();
    }

    @JSCodingFunctionOrMethod(description = "Mark object as dead")
    public void setDead() {
        super.setDead();
    }

    @JSCodingFunctionOrMethod(description = "Check if object is dead")
    public boolean isDead() {
        return super.isDead();
    }

    @JSCodingFunctionOrMethod(description = "Check if object is spawned")
    public boolean isSpawned() {
        return super.isSpawned();
    }

    @JSCodingFunctionOrMethod(description = "Get ticks existed")
    public int getTicksExisted() {
        return super.getTicksExisted();
    }

    @JSCodingFunctionOrMethod(description = "Get object id")
    public int getItemId() {
        return super.getItemId();
    }

    @JSCodingFunctionOrMethod(description = "Get object name")
    public String getItemName() {
        return super.getItemName();
    }

    @JSCodingFunctionOrMethod(description = "Real java object")
    @Override
    public IWorldObject getJavaWorldItem() {
        return this;
    }
}