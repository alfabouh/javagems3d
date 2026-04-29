package api.scripting.coding.env.internal.game.init.events.rendering.world;

import api.scripting.coding.env.def.*;
import api.scripting.coding.env.internal.util.events.JSEventCancellableI;
import api.scripting.coding.env.internal.util.events.JSEventRun;
import api.scripting.coding.env.internal.util.misc.JSFrameTicking;
import api.scripting.coding.env.internal.util.world.render.world.JSSceneWorld;

@JSCodingClass(binding = "JSSceneWorldObjectsUpdateEvent", description = "Event triggered during SceneWorld objects update, supports cancellation.")
public class JSSceneWorldObjectsUpdateEvent implements JSEventCancellableI {
    @JSCodingField(description = "Cancellation flag")
    @JSHideFromDoc
    private boolean cancel;

    @JSCodingField(description = "Scene world instance associated with the event")
    @JSHideFromDoc
    private JSSceneWorld jsSceneWorld;

    @JSCodingField(description = "Whether to refresh objects")
    @JSHideFromDoc
    private boolean refresh;

    @JSCodingField(description = "Frame timing data")
    @JSHideFromDoc
    private JSFrameTicking jsFrameTicking;

    @JSCodingField(description = "Update run type (PRE or POST)")
    @JSHideFromDoc
    private JSEventRun jsRun;

    @JSCodingConstructor(description = "Internal event instance. Do not create manually.")
    public JSSceneWorldObjectsUpdateEvent() {
    }

    @JSHideFromDoc
    public JSSceneWorldObjectsUpdateEvent(JSSceneWorld jsSceneWorld, boolean refresh, JSFrameTicking jsFrameTicking, JSEventRun jsRun) {
        this.jsSceneWorld = jsSceneWorld;
        this.refresh = refresh;
        this.jsFrameTicking = jsFrameTicking;
        this.jsRun = jsRun;
    }

    @JSCodingFunctionOrMethod(description = "Get the scene world associated with this event")
    public JSSceneWorld getSceneWorld() {
        return this.jsSceneWorld;
    }

    @JSCodingFunctionOrMethod(description = "Check whether to refresh objects")
    public boolean isRefresh() {
        return this.refresh;
    }

    @JSCodingFunctionOrMethod(description = "Get frame timing data")
    public JSFrameTicking getFrameTicking() {
        return this.jsFrameTicking;
    }

    @JSCodingFunctionOrMethod(description = "Get the run type (PRE or POST) for this update")
    public JSEventRun getRun() {
        return this.jsRun;
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
        return "JSSceneWorldObjectsUpdateEvent";
    }
}