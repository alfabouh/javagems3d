package api.scripting.coding.env.internal.util.world.physical.entity.instances;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingConstructor;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.math.JSVector3f;
import api.scripting.coding.env.internal.util.world.physical.JSPhysicsWorld;
import api.scripting.coding.env.internal.util.world.physical.entity.JSWorldItem;
import javagems3d.physics.world.basic.WorldItem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@JSCodingClass(binding = "JSBasicWorldItem", description = "Basic world item representing an entity in the physics world with position, rotation, scaling, and basic state management.")
public class JSBasicWorldItem extends JSWorldItem {

    @JSHideFromDoc
    public JSBasicWorldItem(WorldItem worldItem) {
        super(worldItem);
    }

    @JSCodingConstructor(description = "Create a world item with full transformations and custom name", paramNames = {"world", "position", "rotation", "scaling", "itemName"})
    public JSBasicWorldItem(JSPhysicsWorld world, @NotNull JSVector3f position, @NotNull JSVector3f rotation, @NotNull JSVector3f scaling, String itemName) {
        super(world, position, rotation, scaling, itemName);
    }

    @JSCodingConstructor(description = "Create a world item with position and rotation; scaling defaults to (1,1,1)", paramNames = {"world", "position", "rotation", "itemName"})
    public JSBasicWorldItem(JSPhysicsWorld world, JSVector3f position, JSVector3f rotation, String itemName) {
        super(world, position, rotation, itemName);
    }

    @JSCodingConstructor(description = "Create a world item with position only; rotation defaults to (0,0,0), scaling defaults to (1,1,1)", paramNames = {"world", "position", "itemName"})
    public JSBasicWorldItem(JSPhysicsWorld world, JSVector3f position, String itemName) {
        super(world, position, itemName);
    }

    @JSCodingConstructor(description = "Create a world item with default position, rotation, and scaling; only custom name", paramNames = {"world", "itemName"})
    public JSBasicWorldItem(JSPhysicsWorld world, String itemName) {
        super(world, itemName);
    }

    @JSCodingFunctionOrMethod(description = "Set the initial transformations of the world item", paramNames = {"position", "rotation", "scaling"})
    public void setStartTransformations(@Nullable JSVector3f position, @Nullable JSVector3f rotation, @Nullable JSVector3f scaling) {
        this.worldItem.setStartTransformations(
                position == null ? null : position.getJavaVector3f(),
                rotation == null ? null : rotation.getJavaVector3f(),
                scaling == null ? null : scaling.getJavaVector3f()
        );
    }

    @JSCodingFunctionOrMethod(description = "Reset any warp applied to the object")
    public void resetWarp() {
        this.worldItem.resetWarp();
    }

    @JSCodingFunctionOrMethod(description = "Check if the object has been spawned in the world")
    public boolean isSpawned() {
        return this.worldItem.isSpawned();
    }

    @JSCodingFunctionOrMethod(description = "Get the previous position of the object")
    public JSVector3f getPrevPosition() {
        return new JSVector3f(this.worldItem.getPrevPosition());
    }

    @JSCodingFunctionOrMethod(description = "Set the previous position of the object", paramNames = {"vector3f"})
    public void setPrevPosition(JSVector3f vector3f) {
        this.worldItem.setPrevPosition(vector3f.getJavaVector3f());
    }

    @JSCodingFunctionOrMethod(description = "Get the number of ticks the object has existed")
    public int getTicksExisted() {
        return this.worldItem.getTicksExisted();
    }

    @JSCodingFunctionOrMethod(description = "Get the current position of the object")
    public JSVector3f getPosition() {
        return new JSVector3f(this.worldItem.getPosition());
    }

    @JSCodingFunctionOrMethod(description = "Set the current position of the object", paramNames = {"vector3f"})
    public void setPosition(JSVector3f vector3f) {
        this.worldItem.setPosition(vector3f.getJavaVector3f());
    }

    @JSCodingFunctionOrMethod(description = "Get the current rotation of the object")
    public JSVector3f getRotation() {
        return new JSVector3f(this.worldItem.getRotation());
    }

    @JSCodingFunctionOrMethod(description = "Set the current rotation of the object", paramNames = {"vector3f"})
    public void setRotation(JSVector3f vector3f) {
        this.worldItem.setRotation(vector3f.getJavaVector3f());
    }

    @JSCodingFunctionOrMethod(description = "Get the current scaling of the object")
    public JSVector3f getScaling() {
        return new JSVector3f(this.worldItem.getScaling());
    }

    @JSCodingFunctionOrMethod(description = "Set the scaling of the object", paramNames = {"scaling"})
    public void setScaling(JSVector3f scaling) {
        this.worldItem.setScaling(scaling.getJavaVector3f());
    }

    @JSCodingFunctionOrMethod(description = "Check if the object can be destroyed")
    public boolean canBeDestroyed() {
        return this.worldItem.canBeDestroyed();
    }

    @JSCodingFunctionOrMethod(description = "Get the forward (look) vector of the object")
    public JSVector3f getLookVector() {
        return new JSVector3f(this.worldItem.getLookVector());
    }

    @JSCodingFunctionOrMethod(description = "Mark the object as dead")
    public void setDead() {
        this.worldItem.setDead();
    }

    @JSCodingFunctionOrMethod(description = "Destroy the object")
    public void destroy() {
        this.worldItem.destroy();
    }

    @JSCodingFunctionOrMethod(description = "Check if the object is remote-controlled")
    public boolean isRemoteControlled() {
        return this.worldItem.isRemoteControlled();
    }

    @JSCodingFunctionOrMethod(description = "Check if the object is dead")
    public boolean isDead() {
        return this.worldItem.isDead();
    }

    @JSCodingFunctionOrMethod(description = "Get the physics world this object belongs to")
    public JSPhysicsWorld getWorld() {
        return new JSPhysicsWorld(this.worldItem.getWorld());
    }

    @JSCodingFunctionOrMethod(description = "Get the unique ID of the object")
    public int getItemId() {
        return this.worldItem.getItemId();
    }

    @JSCodingFunctionOrMethod(description = "Get the name of the object")
    public String getItemName() {
        return this.worldItem.getItemName();
    }

    @JSCodingFunctionOrMethod(description = "Get the underlying Java WorldItem object (unsafe, internal use)")
    @Override
    public WorldItem getJavaWorldItem() {
        return super.worldItem;
    }
}