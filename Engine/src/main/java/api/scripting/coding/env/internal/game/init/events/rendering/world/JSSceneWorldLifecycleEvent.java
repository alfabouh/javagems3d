package api.scripting.coding.env.internal.game.init.events.rendering.world;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingConstructor;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.events.JSEventI;
import api.scripting.coding.env.internal.util.events.JSEventState;
import api.scripting.coding.env.internal.util.settings.JSGameSettings;
import api.scripting.coding.env.internal.util.settings.JSPerfTestResult;
import api.scripting.coding.env.internal.util.world.render.world.JSSceneWorld;

@JSCodingClass(binding = "JSSceneWorldLifecycleEvent", description = "...")
public class JSSceneWorldLifecycleEvent implements JSEventI {
    @JSHideFromDoc private JSSceneWorld jsSceneWorld;
    @JSHideFromDoc private JSEventState jsEventState;

    @JSCodingConstructor(description = "Internal event instance. Do not create manually.")
    public JSSceneWorldLifecycleEvent() {
    }

    @JSHideFromDoc
    public JSSceneWorldLifecycleEvent(JSSceneWorld jsSceneWorld, JSEventState jsEventState) {
        this.jsSceneWorld = jsSceneWorld;
        this.jsEventState = jsEventState;
    }

    @JSCodingFunctionOrMethod(description = "...", paramNames = {""})
    public JSSceneWorld getSceneWorld() {
        return this.jsSceneWorld;
    }

    @JSCodingFunctionOrMethod(description = "...", paramNames = {""})
    public JSEventState getEventState() {
        return this.jsEventState;
    }

    @JSHideFromDoc
    @Override
    public String name() {
        return "JSSceneWorldLifecycleEvent";
    }
}