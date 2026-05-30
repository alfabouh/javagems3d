package api.scripting.coding.env.internal.util.world.physical;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.world.physical.entity.JSWorldObjectI;
import javagems3d.physics.world.PhysicsWorld;
import javagems3d.physics.world.basic.WorldItem;
import javagems3d.physics.world.thread.dynamics.DynamicsSystem;

@JSCodingClass(binding = "JSPhysicsWorld", description = "Wrapper for PhysicsWorld, providing entity management and physics ticks access.")
public class JSPhysicsWorld {
    @JSHideFromDoc
    private final PhysicsWorld physicsWorld;

    @JSHideFromDoc
    public JSPhysicsWorld(PhysicsWorld physicsWorld) {
        this.physicsWorld = physicsWorld;
    }

    @JSCodingFunctionOrMethod(description = "Get current physics ticks")
    public int getTicks() {
        return this.physicsWorld.getTicks();
    }

    @JSCodingFunctionOrMethod(description = "Remove all entities from the world")
    public void killAllEntities() {
        this.physicsWorld.killItems();
    }

    @JSCodingFunctionOrMethod(description = "Clear all entities and world state")
    public void clearWorld() {
        this.physicsWorld.clear();
    }

    @JSCodingFunctionOrMethod(description = "Add object to the world", paramNames = {"worldItem"})
    public void addObject(JSWorldObjectI worldItem) {
        this.physicsWorld.addObject(worldItem.getJavaWorldObject());
    }

    @JSCodingFunctionOrMethod(description = "Remove object from the world", paramNames = {"worldItem"})
    public void removeObject(JSWorldObjectI worldItem) {
        this.physicsWorld.removeObject(worldItem.getJavaWorldObject());
    }

    @JSCodingFunctionOrMethod(description = "Check if world contains given object", paramNames = {"worldItem"})
    public boolean contains(JSWorldObjectI worldItem) {
        return this.physicsWorld.contains((WorldItem) worldItem.getJavaWorldObject());
    }

    @JSCodingFunctionOrMethod(description = "Get object by ID", paramNames = {"id"})
    public JSWorldObjectI getItemByID(int id) {
        WorldItem item = this.physicsWorld.getItemByID(id);
        if (item == null) {
            return null;
        }
        return () -> item;
    }

    @JSCodingFunctionOrMethod(description = "Get total number of world objects")
    public int countItems() {
        return this.physicsWorld.countItems();
    }

    @JSCodingFunctionOrMethod(description = "Get underlying DynamicsSystem for physics calculations")
    public DynamicsSystem getJavaDynamicsSystem() {
        return this.physicsWorld.getDynamics();
    }

    @JSCodingFunctionOrMethod(description = "Get underlying Java PhysicsWorld object")
    public PhysicsWorld getJavaPhysicsWorld() {
        return this.physicsWorld;
    }
}