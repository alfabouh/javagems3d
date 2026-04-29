package api.scripting.coding.env.internal.game.init.events.rendering.world;

import api.scripting.coding.env.def.*;
import api.scripting.coding.env.internal.util.events.JSEventCancellableI;
import api.scripting.coding.env.internal.util.world.render.lighting.JSLightI;
import api.scripting.coding.env.internal.util.world.render.world.JSSceneWorld;

@JSCodingClass(binding = "JSSceneLightDestroyEvent", description = "Event triggered when a light in the scene is destroyed, supports cancellation.")
public class JSSceneLightDestroyEvent implements JSEventCancellableI {
    @JSCodingField(description = "Cancellation flag")
    @JSHideFromDoc
    private boolean cancel;

    @JSCodingField(description = "Scene world associated with the light")
    @JSHideFromDoc
    private JSSceneWorld jsSceneWorld;

    @JSCodingField(description = "The light being destroyed")
    @JSHideFromDoc
    private JSLightI jsLight;

    @JSCodingConstructor(description = "Internal event instance. Do not create manually.")
    public JSSceneLightDestroyEvent() {
    }

    @JSHideFromDoc
    public JSSceneLightDestroyEvent(JSSceneWorld jsSceneWorld, JSLightI jsLight) {
        this.jsSceneWorld = jsSceneWorld;
        this.jsLight = jsLight;
    }

    @JSCodingFunctionOrMethod(description = "Get the scene world associated with this event")
    public JSSceneWorld getSceneWorld() {
        return this.jsSceneWorld;
    }

    @JSCodingFunctionOrMethod(description = "Get the light being destroyed")
    public JSLightI getLight() {
        return this.jsLight;
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
        return "JSSceneLightDestroyEvent";
    }
}