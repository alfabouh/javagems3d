package api.scripting.coding.env.internal.util.world.physical.entity.real;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingConstructor;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.internal.util.math.JSVector3f;
import api.scripting.coding.env.internal.util.world.physical.entity.JSWorldItemI;
import api.scripting.coding.env.internal.util.world.physical.entity.JSWorldObjectI;
import javagems3d.physics.world.PhysicsWorld;
import javagems3d.physics.world.basic.WorldItem;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

@JSCodingClass(binding = "JSRealJavaWorldObjectWrapped", description = "Base class for real world objects. Provides access to position, rotation, scaling and lifecycle control.")
public abstract class JSRealJavaWorldObjectWrapped extends WorldItem implements JSWorldItemI {

    @JSCodingConstructor(description = "Create world item with full transform", paramNames = {"world", "position", "rotation", "scaling", "itemName"})
    public JSRealJavaWorldObjectWrapped(PhysicsWorld world, @NotNull Vector3f position, @NotNull Vector3f rotation, @NotNull Vector3f scaling, String itemName) {
        super(world, position, rotation, scaling, itemName);
    }

    @JSCodingConstructor(description = "Create world item with position and rotation", paramNames = {"world", "position", "rotation", "itemName"})
    public JSRealJavaWorldObjectWrapped(PhysicsWorld world, Vector3f position, Vector3f rotation, String itemName) {
        super(world, position, rotation, itemName);
    }

    @JSCodingConstructor(description = "Create world item with position only", paramNames = {"world", "position", "itemName"})
    public JSRealJavaWorldObjectWrapped(PhysicsWorld world, Vector3f position, String itemName) {
        super(world, position, itemName);
    }

    @JSCodingConstructor(description = "Create world item with only world and name", paramNames = {"world", "itemName"})
    public JSRealJavaWorldObjectWrapped(PhysicsWorld world, String itemName) {
        super(world, itemName);
    }

    @JSCodingFunctionOrMethod(description = "Get position")
    public JSVector3f getPositionJS() {
        Vector3f p = super.getPosition();
        return new JSVector3f(p.x, p.y, p.z);
    }

    @JSCodingFunctionOrMethod(description = "Set position", paramNames = {"pos"})
    public void setPositionJS(JSVector3f pos) {
        super.setPosition(pos.getJavaVector3f());
    }

    @JSCodingFunctionOrMethod(description = "Get rotation")
    public JSVector3f getRotationJS() {
        Vector3f r = super.getRotation();
        return new JSVector3f(r.x, r.y, r.z);
    }

    @JSCodingFunctionOrMethod(description = "Set rotation", paramNames = {"rot"})
    public void setRotationJS(JSVector3f rot) {
        super.setRotation(rot.getJavaVector3f());
    }

    @JSCodingFunctionOrMethod(description = "Get scaling")
    public JSVector3f getScalingJS() {
        Vector3f s = super.getScaling();
        return new JSVector3f(s.x, s.y, s.z);
    }

    @JSCodingFunctionOrMethod(description = "Set scaling", paramNames = {"scale"})
    public void setScalingJS(JSVector3f scale) {
        super.setScaling(scale.getJavaVector3f());
    }

    @JSCodingFunctionOrMethod(description = "Get forward/look direction vector")
    public JSVector3f getLookVector3() {
        Vector3f v = super.getLookVector();
        return new JSVector3f(v.x, v.y, v.z);
    }

    @JSCodingFunctionOrMethod(description = "Reset position and rotation to initial values")
    public void resetWarp() {
        super.resetWarp();
    }

    @JSCodingFunctionOrMethod(description = "Destroy object")
    public void destroy() {
        super.destroy();
    }

    @JSCodingFunctionOrMethod(description = "Mark object as dead (safe destroy)")
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
    public WorldItem getJavaWorldObject() {
        return this;
    }
}