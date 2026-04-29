package api.scripting.coding.env.internal.map.events.mapping;

import api.events.EventBus;
import api.scripting.coding.env.def.*;
import api.scripting.coding.env.internal.util.events.JSEventCancellableI;
import api.scripting.coding.env.internal.util.mapping.row.JSRowMapObjectData;
import api.scripting.coding.env.internal.util.world.physical.JSPhysicsWorld;
import api.scripting.coding.env.internal.util.world.render.world.JSSceneWorld;

@JSCodingClass(binding = "JSMapMarkerConvertEvent", description = "Event triggered during conversion of a map marker to a scene marker instance.")
public class JSMapMarkerConvertEvent implements JSEventCancellableI {
    @JSHideFromDoc
    private JSSceneWorld jsSceneWorld;

    @JSHideFromDoc
    private JSPhysicsWorld jsPhysicsWorld;

    @JSHideFromDoc
    private JSRowMapObjectData jsTemplate;

    @JSCodingField(description = "Cancellation flag")
    @JSHideFromDoc
    private boolean cancel;

    @JSCodingConstructor(description = "Internal event instance. Do not create manually.", paramNames = {})
    public JSMapMarkerConvertEvent() {
    }

    @JSHideFromDoc
    public JSMapMarkerConvertEvent(JSSceneWorld jsSceneWorld, JSPhysicsWorld jsPhysicsWorld, JSRowMapObjectData jsTemplate) {
        this.jsSceneWorld = jsSceneWorld;
        this.jsPhysicsWorld = jsPhysicsWorld;
        this.jsTemplate = jsTemplate;
    }

    @JSCodingFunctionOrMethod(description = "Get scene world associated with this event", paramNames = {})
    public JSSceneWorld getSceneWorld() { return this.jsSceneWorld; }

    @JSCodingFunctionOrMethod(description = "Get physics world associated with this event", paramNames = {})
    public JSPhysicsWorld getPhysicsWorld() { return this.jsPhysicsWorld; }

    @JSCodingFunctionOrMethod(description = "Get template data of the map object", paramNames = {})
    public JSRowMapObjectData getTemplate() { return this.jsTemplate; }

    @JSHideFromDoc
    @Override
    public boolean isCancelled() { return this.cancel; }

    @JSHideFromDoc
    @Override
    public void setCancelled(boolean cancelled) { this.cancel = cancelled; }

    @JSHideFromDoc
    @Override
    public String name() { return "JSMapMarkerConvertEvent"; }
}