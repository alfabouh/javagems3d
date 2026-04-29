package api.scripting.coding.env.internal.game.init.events.rendering.world;

import api.scripting.coding.env.def.*;
import api.scripting.coding.env.internal.util.events.JSEventI;
import api.scripting.coding.env.internal.util.world.render.lighting.JSLightI;
import api.scripting.coding.env.internal.util.world.render.world.JSSceneWorld;
import api.scripting.coding.env.internal.util.world.render.world.instances.interfaces.JSSceneObjectI;

@JSCodingClass(binding = "JSSceneObjectUpdateEvent", description = "Event triggered when a scene object is updated.")
public class JSSceneObjectUpdateEvent implements JSEventI {
    @JSCodingField(description = "Scene world associated with the object")
    @JSHideFromDoc
    private JSSceneWorld jsSceneWorld;

    @JSCodingField(description = "The object being updated")
    @JSHideFromDoc
    private JSSceneObjectI jsObject;

    @JSCodingConstructor(description = "Internal event instance. Do not create manually.")
    public JSSceneObjectUpdateEvent() {
    }

    @JSHideFromDoc
    public JSSceneObjectUpdateEvent(JSSceneWorld jsSceneWorld, JSSceneObjectI jsObject) {
        this.jsSceneWorld = jsSceneWorld;
        this.jsObject = jsObject;
    }

    @JSCodingFunctionOrMethod(description = "Get the scene world associated with this event")
    public JSSceneWorld getSceneWorld() {
        return this.jsSceneWorld;
    }

    @JSCodingFunctionOrMethod(description = "Get the scene object being updated")
    public JSSceneObjectI getObject() {
        return this.jsObject;
    }

    @JSHideFromDoc
    @Override
    public String name() {
        return "JSSceneObjectUpdateEvent";
    }
}