package api.scripting.coding.env.internal.util.world.physical.entity.instances;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.math.JSVector3f;
import api.scripting.coding.env.internal.util.world.physical.JSPhysicsWorld;
import api.scripting.coding.env.internal.util.world.physical.entity.JSAbstractWorldItem;
import javagems3d.physics.world.basic.WorldItem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@JSCodingClass(binding = "JSBasicWorldItem", description = "...")
public class JSBasicWorldItem extends JSAbstractWorldItem {
    @JSHideFromDoc
    public JSBasicWorldItem(WorldItem worldItem) {
        super(worldItem);
    }

    public JSBasicWorldItem(JSPhysicsWorld world, @NotNull JSVector3f position, @NotNull JSVector3f rotation, @NotNull JSVector3f scaling, String itemName) {
        super(world, position, rotation, scaling, itemName);
    }

    public JSBasicWorldItem(JSPhysicsWorld world, JSVector3f position, JSVector3f rotation, String itemName) {
        super(world, position, rotation, itemName);
    }

    public JSBasicWorldItem(JSPhysicsWorld world, JSVector3f position, String itemName) {
        super(world, position, itemName);
    }

    public JSBasicWorldItem(JSPhysicsWorld world, String itemName) {
        super(world, itemName);
    }

    @JSCodingFunctionOrMethod(description = "...")
    public void setStartTransformations(@Nullable JSVector3f position, @Nullable JSVector3f rotation, @Nullable JSVector3f scaling) {
        this.worldItem.setStartTransformations(position == null ? null : position.getJavaVector3f(), rotation == null ? null : rotation.getJavaVector3f(), scaling == null ? null : scaling.getJavaVector3f());
    }

    @JSCodingFunctionOrMethod(description = "...")
    public void resetWarp() {
        this.worldItem.resetWarp();
    }

    @JSCodingFunctionOrMethod(description = "...")
    public boolean isSpawned() {
        return this.worldItem.isSpawned();
    }

    @JSCodingFunctionOrMethod(description = "...")
    public JSVector3f getPrevPosition() {
        return new JSVector3f(this.worldItem.getPrevPosition());
    }

    @JSCodingFunctionOrMethod(description = "...")
    public void setPrevPosition(JSVector3f vector3f) {
        this.worldItem.setPrevPosition(vector3f.getJavaVector3f());
    }

    @JSCodingFunctionOrMethod(description = "...")
    public int getTicksExisted() {
        return this.worldItem.getTicksExisted();
    }

    @JSCodingFunctionOrMethod(description = "...")
    public JSVector3f getPosition() {
        return new JSVector3f(this.worldItem.getPosition());
    }

    @JSCodingFunctionOrMethod(description = "...")
    public void setPosition(JSVector3f vector3f) {
        this.worldItem.setPosition(vector3f.getJavaVector3f());
    }

    @JSCodingFunctionOrMethod(description = "...")
    public JSVector3f getRotation() {
        return new JSVector3f(this.worldItem.getRotation());
    }

    @JSCodingFunctionOrMethod(description = "...")
    public void setRotation(JSVector3f vector3f) {
        this.worldItem.setRotation(vector3f.getJavaVector3f());
    }

    @JSCodingFunctionOrMethod(description = "...")
    public JSVector3f getScaling() {
        return new JSVector3f(this.worldItem.getScaling());
    }

    @JSCodingFunctionOrMethod(description = "...")
    public void setScaling(JSVector3f scaling) {
        this.worldItem.setScaling(scaling.getJavaVector3f());
    }

    @JSCodingFunctionOrMethod(description = "...")
    public boolean canBeDestroyed() {
        return this.worldItem.canBeDestroyed();
    }

    @JSCodingFunctionOrMethod(description = "...")
    public JSVector3f getLookVector() {
        return new JSVector3f(this.worldItem.getLookVector());
    }

    @JSCodingFunctionOrMethod(description = "...")
    public void setDead() {
        this.worldItem.setDead();
    }

    @JSCodingFunctionOrMethod(description = "...")
    public void destroy() {
        this.worldItem.destroy();
    }

    @JSCodingFunctionOrMethod(description = "...")
    public boolean isRemoteControlled() {
        return this.worldItem.isRemoteControlled();
    }

    @JSCodingFunctionOrMethod(description = "...")
    public boolean isDead() {
        return this.worldItem.isDead();
    }

    @JSCodingFunctionOrMethod(description = "...")
    public JSPhysicsWorld getWorld() {
        return new JSPhysicsWorld(this.worldItem.getWorld());
    }

    @JSCodingFunctionOrMethod(description = "...")
    public int getItemId() {
        return this.worldItem.getItemId();
    }

    @JSCodingFunctionOrMethod(description = "...")
    public String getItemName() {
        return this.worldItem.getItemName();
    }

    @JSCodingFunctionOrMethod(description = "...")
    @Override
    public WorldItem getJavaWorldItem() {
        return super.worldItem;
    }
}
