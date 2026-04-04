package api.scripting.coding.env.internal.game.init.events.physics;

import api.scripting.coding.env.def.*;
import api.scripting.coding.env.internal.util.events.JSEventCancellableI;
import api.scripting.coding.env.internal.util.world.physical.JSPhysicsWorld;
import api.scripting.coding.env.internal.util.world.physical.entity.JSWorldItem;
import api.scripting.coding.env.internal.util.world.physical.entity.JSWorldObjectI;

@JSCodingClass(binding = "JSPhysicsWorldObjectAddEvent", description = "Event triggered when a new object is added to the PhysicsWorld.")
public class JSPhysicsWorldObjectAddEvent implements JSEventCancellableI {

    @JSCodingField(description = "Cancellation flag")
    @JSHideFromDoc
    private boolean cancel;

    @JSCodingField(description = "Physics world")
    @JSHideFromDoc
    private JSPhysicsWorld jsWorld;

    @JSCodingField(description = "World object being added")
    @JSHideFromDoc
    private JSWorldObjectI jsObject;

    @JSCodingConstructor(description = "Internal event instance. Do not create manually.")
    public JSPhysicsWorldObjectAddEvent() {
    }

    @JSHideFromDoc
    public JSPhysicsWorldObjectAddEvent(JSPhysicsWorld jsWorld, JSWorldObjectI jsObject) {
        this.jsWorld = jsWorld;
        this.jsObject = jsObject;
    }

    @JSCodingFunctionOrMethod(description = "Get the physics world")
    public JSPhysicsWorld getWorld() {
        return this.jsWorld;
    }

    @JSCodingFunctionOrMethod(description = "Get the world object being added")
    public JSWorldObjectI getObject() {
        return this.jsObject;
    }

    @JSCodingFunctionOrMethod(description = "Check whether this event is cancelled.")
    @Override
    public boolean isCancelled() {
        return this.cancel;
    }

    @JSCodingFunctionOrMethod(description = "Set event cancellation state. If true, default behavior will not execute.", paramNames = {"cancelled"})
    @Override
    public void setCancelled(boolean cancelled) {
        this.cancel = cancelled;
    }

    @JSHideFromDoc
    @Override
    public String name() {
        return "JSPhysicsWorldObjectAddEvent";
    }
}