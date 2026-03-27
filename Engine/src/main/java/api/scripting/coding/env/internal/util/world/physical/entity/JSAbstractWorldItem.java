package api.scripting.coding.env.internal.util.world.physical.entity;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.math.JSVector3f;
import api.scripting.coding.env.internal.util.world.physical.JSPhysicsWorld;
import javagems3d.physics.world.basic.BasicWorldItem;
import javagems3d.physics.world.basic.WorldItem;
import org.jetbrains.annotations.NotNull;

@JSCodingClass(binding = "JSAbstractWorldItem", description = "...")
public abstract class JSAbstractWorldItem implements JSWorldItemI {
    protected final WorldItem worldItem;

    @JSHideFromDoc
    public JSAbstractWorldItem(WorldItem worldItem) {
        this.worldItem = worldItem;
    }

    public JSAbstractWorldItem(JSPhysicsWorld world, @NotNull JSVector3f position, @NotNull JSVector3f rotation, @NotNull JSVector3f scaling, String itemName) {
        this.worldItem = new BasicWorldItem(world.getJavaPhysicsWorld(), position.getJavaVector3f(), rotation.getJavaVector3f(), scaling.getJavaVector3f(), itemName);
    }

    public JSAbstractWorldItem(JSPhysicsWorld world, JSVector3f position, JSVector3f rotation, String itemName) {
        this.worldItem = new BasicWorldItem(world.getJavaPhysicsWorld(), position.getJavaVector3f(), rotation.getJavaVector3f(), itemName);
    }

    public JSAbstractWorldItem(JSPhysicsWorld world, JSVector3f position, String itemName) {
        this.worldItem = new BasicWorldItem(world.getJavaPhysicsWorld(), position.getJavaVector3f(), itemName);
    }

    public JSAbstractWorldItem(JSPhysicsWorld world, String itemName) {
        this.worldItem = new BasicWorldItem(world.getJavaPhysicsWorld(), itemName);
    }

    @JSCodingFunctionOrMethod(description = "...")
    @Override
    public WorldItem getJavaWorldItem() {
        return this.worldItem;
    }
}
