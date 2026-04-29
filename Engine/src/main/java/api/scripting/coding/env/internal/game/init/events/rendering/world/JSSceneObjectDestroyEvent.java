package api.scripting.coding.env.internal.game.init.events.rendering.world;

import api.scripting.coding.env.def.*;
import api.scripting.coding.env.internal.util.events.JSEventCancellableI;
import api.scripting.coding.env.internal.util.world.render.world.JSSceneWorld;
import api.scripting.coding.env.internal.util.world.render.world.instances.interfaces.JSSceneObjectI;

@JSCodingClass(binding = "JSSceneObjectDestroyEvent", description = "Event triggered when a scene object is destroyed, supports cancellation.")
public class JSSceneObjectDestroyEvent implements JSEventCancellableI {
    @JSCodingField(description = "Cancellation flag")
    @JSHideFromDoc
    private boolean cancel;

    @JSCodingField(description = "Scene world associated with the object")
    @JSHideFromDoc
    private JSSceneWorld jsSceneWorld;

    @JSCodingField(description = "The object being destroyed")
    @JSHideFromDoc
    private JSSceneObjectI jsObject;

    @JSCodingConstructor(description = "Internal event instance. Do not create manually.")
    public JSSceneObjectDestroyEvent() {
    }

    @JSHideFromDoc
    public JSSceneObjectDestroyEvent(JSSceneWorld jsSceneWorld, JSSceneObjectI jsObject) {
        this.jsSceneWorld = jsSceneWorld;
        this.jsObject = jsObject;
    }

    @JSCodingFunctionOrMethod(description = "Get the scene world associated with this event")
    public JSSceneWorld getSceneWorld() {
        return this.jsSceneWorld;
    }

    @JSCodingFunctionOrMethod(description = "Get the scene object being destroyed")
    public JSSceneObjectI getObject() {
        return this.jsObject;
    }

    @JSCodingFunctionOrMethod(description = "Check whether this event is cancelled")
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
        return "JSSceneObjectDestroyEvent";
    }
}