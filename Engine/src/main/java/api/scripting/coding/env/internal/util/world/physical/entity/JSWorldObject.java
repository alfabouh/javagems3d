package api.scripting.coding.env.internal.util.world.physical.entity;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingConstructor;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.math.JSVector3f;
import api.scripting.coding.env.internal.util.world.physical.JSPhysicsWorld;
import javagems3d.physics.world.basic.BasicWorldItem;
import javagems3d.physics.world.basic.WorldItem;
import org.jetbrains.annotations.NotNull;

@JSCodingClass(binding = "JSWorldObject", description = "Wrapper for WorldItem objects.")
public class JSWorldObject implements JSWorldItemI {
    protected final WorldItem worldItem;

    @JSHideFromDoc
    public JSWorldObject(WorldItem worldItem) {
        this.worldItem = worldItem;
    }

    @JSCodingConstructor(description = "Create a BasicWorldItem with full transform", paramNames = {"world", "position", "rotation", "scaling", "itemName"})
    public JSWorldObject(JSPhysicsWorld world, @NotNull JSVector3f position, @NotNull JSVector3f rotation, @NotNull JSVector3f scaling, String itemName) {
        this.worldItem = new BasicWorldItem(world.getJavaPhysicsWorld(), position.getJavaVector3f(), rotation.getJavaVector3f(), scaling.getJavaVector3f(), itemName);
    }

    @JSCodingConstructor(description = "Create a BasicWorldItem with position and rotation", paramNames = {"world", "position", "rotation", "itemName"})
    public JSWorldObject(JSPhysicsWorld world, JSVector3f position, JSVector3f rotation, String itemName) {
        this.worldItem = new BasicWorldItem(world.getJavaPhysicsWorld(), position.getJavaVector3f(), rotation.getJavaVector3f(), itemName);
    }

    @JSCodingConstructor(description = "Create a BasicWorldItem with position only", paramNames = {"world", "position", "itemName"})
    public JSWorldObject(JSPhysicsWorld world, JSVector3f position, String itemName) {
        this.worldItem = new BasicWorldItem(world.getJavaPhysicsWorld(), position.getJavaVector3f(), itemName);
    }

    @JSCodingConstructor(description = "Create a BasicWorldItem with world only", paramNames = {"world", "itemName"})
    public JSWorldObject(JSPhysicsWorld world, String itemName) {
        this.worldItem = new BasicWorldItem(world.getJavaPhysicsWorld(), itemName);
    }

    @JSCodingFunctionOrMethod(description = "Get underlying Java WorldItem")
    @Override
    public WorldItem getJavaWorldObject() {
        return this.worldItem;
    }
}