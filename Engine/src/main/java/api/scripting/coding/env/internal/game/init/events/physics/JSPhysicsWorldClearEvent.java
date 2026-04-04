package api.scripting.coding.env.internal.game.init.events.physics;

import api.scripting.coding.env.def.*;
import api.scripting.coding.env.internal.util.events.JSEventCancellableI;
import api.scripting.coding.env.internal.util.world.physical.JSPhysicsWorld;

@JSCodingClass(binding = "JSPhysicsWorldClearEvent", description = "Event triggered when the PhysicsWorld is cleared.")
public class JSPhysicsWorldClearEvent implements JSEventCancellableI {

    @JSCodingField(description = "Cancellation flag")
    @JSHideFromDoc
    private boolean cancel;

    @JSCodingField(description = "The physics world")
    @JSHideFromDoc
    private JSPhysicsWorld jsWorld;

    @JSCodingConstructor(description = "Internal event instance. Do not create manually.")
    public JSPhysicsWorldClearEvent() {
    }

    @JSHideFromDoc
    public JSPhysicsWorldClearEvent(JSPhysicsWorld jsWorld) {
        this.jsWorld = jsWorld;
    }

    @JSCodingFunctionOrMethod(description = "Get the physics world")
    public JSPhysicsWorld getWorld() {
        return this.jsWorld;
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
        return "JSPhysicsWorldClearEvent";
    }
}