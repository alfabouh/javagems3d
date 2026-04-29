package api.scripting.coding.env.internal.map.events.mapping;

import api.events.EventBus;
import api.scripting.coding.env.def.*;
import api.scripting.coding.env.internal.util.events.JSEventCancellableI;
import api.scripting.coding.env.internal.util.world.physical.JSPhysicsWorld;
import api.scripting.coding.env.internal.util.world.render.world.JSSceneWorld;

@JSCodingClass(binding = "JSMapProcessingEvent", description = "Event triggered during processing of map objects in the scene.")
public class JSMapProcessingEvent implements JSEventCancellableI {

    @JSHideFromDoc
    private JSSceneWorld jsSceneWorld;

    @JSHideFromDoc
    private JSPhysicsWorld jsPhysicsWorld;

    @JSHideFromDoc
    private EventBus.Run jsRun;

    @JSCodingField(description = "Cancellation flag")
    @JSHideFromDoc
    private boolean cancel;

    @JSCodingConstructor(description = "Internal event instance. Do not create manually.", paramNames = {})
    public JSMapProcessingEvent() {
    }

    @JSHideFromDoc
    public JSMapProcessingEvent(JSPhysicsWorld jsPhysicsWorld, JSSceneWorld jsSceneWorld, EventBus.Run jsRun) {
        this.jsPhysicsWorld = jsPhysicsWorld;
        this.jsSceneWorld = jsSceneWorld;
        this.jsRun = jsRun;
    }

    @JSCodingFunctionOrMethod(description = "Get physics world associated with this event", paramNames = {})
    public JSPhysicsWorld getPhysicsWorld() { return this.jsPhysicsWorld; }

    @JSCodingFunctionOrMethod(description = "Get scene world associated with this event", paramNames = {})
    public JSSceneWorld getSceneWorld() { return this.jsSceneWorld; }

    @JSCodingFunctionOrMethod(description = "Get run mode associated with this event", paramNames = {})
    public EventBus.Run getRun() { return this.jsRun; }

    @JSHideFromDoc
    @Override
    public boolean isCancelled() { return this.cancel; }

    @JSHideFromDoc
    @Override
    public void setCancelled(boolean cancelled) { this.cancel = cancelled; }

    @JSHideFromDoc
    @Override
    public String name() { return "JSMapProcessingEvent"; }
}