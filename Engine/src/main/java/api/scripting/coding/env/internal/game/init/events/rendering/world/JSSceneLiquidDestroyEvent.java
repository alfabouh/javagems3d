package api.scripting.coding.env.internal.game.init.events.rendering.world;

import api.scripting.coding.env.def.*;
import api.scripting.coding.env.internal.util.events.JSEventCancellableI;
import api.scripting.coding.env.internal.util.world.render.world.JSSceneWorld;
import api.scripting.coding.env.internal.util.world.render.world.instances.JSSceneObject;
import api.scripting.coding.env.internal.util.world.render.world.instances.JSSceneWorldLiquid;

@JSCodingClass(binding = "JSSceneLiquidDestroyEvent", description = "Event triggered when a liquid in the scene is destroyed, supports cancellation.")
public class JSSceneLiquidDestroyEvent implements JSEventCancellableI {
    @JSCodingField(description = "Cancellation flag")
    @JSHideFromDoc
    private boolean cancel;

    @JSCodingField(description = "Scene world associated with the liquid")
    @JSHideFromDoc
    private JSSceneWorld jsSceneWorld;

    @JSCodingField(description = "The liquid being destroyed")
    @JSHideFromDoc
    private JSSceneWorldLiquid jsLiquid;

    @JSCodingConstructor(description = "Internal event instance. Do not create manually.")
    public JSSceneLiquidDestroyEvent() {
    }

    @JSHideFromDoc
    public JSSceneLiquidDestroyEvent(JSSceneWorld jsSceneWorld, JSSceneWorldLiquid jsLiquid) {
        this.jsSceneWorld = jsSceneWorld;
        this.jsLiquid = jsLiquid;
    }

    @JSCodingFunctionOrMethod(description = "Get the scene world associated with this event")
    public JSSceneWorld getSceneWorld() {
        return this.jsSceneWorld;
    }

    @JSCodingFunctionOrMethod(description = "Get the liquid being destroyed")
    public JSSceneWorldLiquid getLiquid() {
        return this.jsLiquid;
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
        return "JSSceneLiquidDestroyEvent";
    }
}