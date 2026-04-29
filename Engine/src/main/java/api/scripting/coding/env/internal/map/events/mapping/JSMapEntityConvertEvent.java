package api.scripting.coding.env.internal.map.events.mapping;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingConstructor;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.map.events.mapping.data.JSEntityData;
import api.scripting.coding.env.internal.util.events.JSEventCancellableI;
import api.scripting.coding.env.internal.util.mapping.row.JSRowMapObjectData;
import api.scripting.coding.env.internal.util.world.physical.JSPhysicsWorld;
import api.scripting.coding.env.internal.util.world.physical.entity.JSWorldItem;
import api.scripting.coding.env.internal.util.world.render.world.JSSceneWorld;

@JSCodingClass(binding = "JSMapEntityConvertEvent", description = "Event triggered during conversion of a map entity to a world item.")
public class JSMapEntityConvertEvent implements JSEventCancellableI {

    @JSHideFromDoc
    private boolean cancel;

    @JSHideFromDoc
    private JSSceneWorld jsSceneWorld;

    @JSHideFromDoc
    private JSPhysicsWorld jsPhysicsWorld;

    @JSHideFromDoc
    private JSRowMapObjectData jsTemplate;

    @JSHideFromDoc
    private JSEntityData jsEntityData;

    @JSHideFromDoc
    private JSWorldItem jsResult;

    @JSCodingConstructor(description = "Internal event instance. Do not create manually.")
    public JSMapEntityConvertEvent() {
    }

    @JSCodingConstructor(description = "Internal event instance. Do not create manually.", paramNames = {"sceneWorld", "physicsWorld", "template", "entityData"})
    public JSMapEntityConvertEvent(JSSceneWorld jsSceneWorld, JSPhysicsWorld jsPhysicsWorld, JSRowMapObjectData jsTemplate, JSEntityData jsEntityData) {
        this.jsSceneWorld = jsSceneWorld;
        this.jsPhysicsWorld = jsPhysicsWorld;
        this.jsTemplate = jsTemplate;
        this.jsEntityData = jsEntityData;
    }

    @JSCodingFunctionOrMethod(description = "Get scene world", paramNames = {})
    public JSSceneWorld getSceneWorld() { return this.jsSceneWorld; }

    @JSCodingFunctionOrMethod(description = "Get physics world", paramNames = {})
    public JSPhysicsWorld getPhysicsWorld() { return this.jsPhysicsWorld; }

    @JSCodingFunctionOrMethod(description = "Get map object template", paramNames = {})
    public JSRowMapObjectData getTemplate() { return this.jsTemplate; }

    @JSCodingFunctionOrMethod(description = "Get entity data", paramNames = {})
    public JSEntityData getEntityData() { return this.jsEntityData; }

    @JSCodingFunctionOrMethod(description = "Get conversion result", paramNames = {})
    public JSWorldItem getResult() { return this.jsResult; }

    @JSCodingFunctionOrMethod(description = "Set conversion result", paramNames = {"result"})
    public void setResult(JSWorldItem result) { this.jsResult = result; }

    @JSCodingFunctionOrMethod(description = "Check whether this event is cancelled.")
    @Override
    public boolean isCancelled() { return this.cancel; }

    @JSCodingFunctionOrMethod(description = "Set event cancellation state. If true, default behavior will not execute.", paramNames = {"cancelled"})
    @Override
    public void setCancelled(boolean cancelled) { this.cancel = cancelled; }

    @JSHideFromDoc
    @Override
    public String name() { return "JSMapEntityConvertEvent"; }
}