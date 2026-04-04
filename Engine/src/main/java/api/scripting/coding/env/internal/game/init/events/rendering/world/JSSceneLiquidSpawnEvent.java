package api.scripting.coding.env.internal.game.init.events.rendering.world;

import api.scripting.coding.env.def.*;
import api.scripting.coding.env.internal.util.events.JSEventCancellableI;
import api.scripting.coding.env.internal.util.world.physical.zones.instances.JSLiquid;
import api.scripting.coding.env.internal.util.world.render.data.JSLiquidRenderData;
import api.scripting.coding.env.internal.util.world.render.world.JSSceneWorld;

@JSCodingClass(binding = "JSSceneLiquidSpawnEvent", description = "Event triggered when a liquid is spawned in the scene, supports cancellation.")
public class JSSceneLiquidSpawnEvent implements JSEventCancellableI {
    @JSCodingField(description = "Cancellation flag")
    @JSHideFromDoc
    private boolean cancel;

    @JSCodingField(description = "Scene world associated with the liquid")
    @JSHideFromDoc
    private JSSceneWorld jsSceneWorld;

    @JSCodingField(description = "The liquid being spawned")
    @JSHideFromDoc
    private JSLiquid jsLiquid;

    @JSCodingField(description = "Render data for the spawned liquid")
    @JSHideFromDoc
    private JSLiquidRenderData jsRenderData;

    @JSCodingConstructor(description = "Internal event instance. Do not create manually.")
    public JSSceneLiquidSpawnEvent() {
    }

    @JSHideFromDoc
    public JSSceneLiquidSpawnEvent(JSSceneWorld jsSceneWorld, JSLiquid jsLiquid, JSLiquidRenderData jsRenderData) {
        this.jsSceneWorld = jsSceneWorld;
        this.jsLiquid = jsLiquid;
        this.jsRenderData = jsRenderData;
    }

    @JSCodingFunctionOrMethod(description = "Get the scene world associated with this event")
    public JSSceneWorld getSceneWorld() {
        return this.jsSceneWorld;
    }

    @JSCodingFunctionOrMethod(description = "Get the liquid being spawned")
    public JSLiquid getLiquid() {
        return this.jsLiquid;
    }

    @JSCodingFunctionOrMethod(description = "Get render data for the spawned liquid")
    public JSLiquidRenderData getRenderData() {
        return this.jsRenderData;
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
        return "JSSceneLiquidSpawnEvent";
    }
}