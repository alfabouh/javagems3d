package api.scripting.coding.env.internal.map.events.mapping;

import api.scripting.coding.env.def.*;
import api.scripting.coding.env.internal.util.events.JSEventCancellableI;
import api.scripting.coding.env.internal.util.mapping.row.JSRowMapObjectData;
import api.scripting.coding.env.internal.util.misc.JSPair;
import api.scripting.coding.env.internal.util.world.physical.JSPhysicsWorld;
import api.scripting.coding.env.internal.util.world.render.lighting.JSLightI;
import api.scripting.coding.env.internal.util.world.render.world.JSSceneWorld;

@JSCodingClass(binding = "JSMapPointLightConvertEvent", description = "Event triggered during map conversion to create a point light from template data.")
public class JSMapPointLightConvertEvent implements JSEventCancellableI {
    @JSCodingField(description = "Cancellation flag")
    private boolean cancel;

    @JSHideFromDoc
    private JSSceneWorld jsSceneWorld;

    @JSHideFromDoc
    private JSPhysicsWorld jsPhysicsWorld;

    @JSHideFromDoc
    private JSRowMapObjectData jsTemplate;

    @JSHideFromDoc
    private JSPair<JSLightI, Integer> jsResult;

    @JSCodingConstructor(description = "Internal event instance. Do not create manually.")
    public JSMapPointLightConvertEvent() {
    }

    @JSHideFromDoc
    public JSMapPointLightConvertEvent(JSSceneWorld jsSceneWorld, JSPhysicsWorld jsPhysicsWorld, JSRowMapObjectData jsTemplate) {
        this.jsSceneWorld = jsSceneWorld;
        this.jsPhysicsWorld = jsPhysicsWorld;
        this.jsTemplate = jsTemplate;
    }

    @JSCodingFunctionOrMethod(description = "Get scene world", paramNames = {})
    public JSSceneWorld getSceneWorld() {
        return this.jsSceneWorld;
    }

    @JSCodingFunctionOrMethod(description = "Get physics world", paramNames = {})
    public JSPhysicsWorld getPhysicsWorld() {
        return this.jsPhysicsWorld;
    }

    @JSCodingFunctionOrMethod(description = "Get template data", paramNames = {})
    public JSRowMapObjectData getTemplate() {
        return this.jsTemplate;
    }

    @JSCodingFunctionOrMethod(description = "Get conversion result as a pair of point light and integer", paramNames = {})
    public JSPair<JSLightI, Integer> getResult() {
        return this.jsResult;
    }

    @JSCodingFunctionOrMethod(description = "Set conversion result as a pair of point light and integer", paramNames = {"result"})
    public void setResult(JSPair<JSLightI, Integer> result) {
        this.jsResult = result;
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
        return "JSMapPointLightConvertEvent";
    }
}