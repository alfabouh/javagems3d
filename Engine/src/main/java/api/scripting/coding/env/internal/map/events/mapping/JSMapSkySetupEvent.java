package api.scripting.coding.env.internal.map.events.mapping;

import api.scripting.coding.env.def.*;
import api.scripting.coding.env.internal.util.events.JSEventCancellableI;
import api.scripting.coding.env.internal.util.world.render.world.JSSceneWorld;
import api.scripting.coding.env.internal.util.world.render.world.environment.JSEnvironment;

@JSCodingClass(binding = "JSMapSkySetupEvent", description = "Event triggered during setup of sky and sun data in the scene.")
public class JSMapSkySetupEvent implements JSEventCancellableI {
    @JSHideFromDoc
    private JSSceneWorld jsSceneWorld;

    @JSHideFromDoc
    private JSEnvironment jsEnvironment;

    @JSCodingField(description = "Cancellation flag")
    @JSHideFromDoc
    private boolean cancel;

    @JSCodingConstructor(description = "Internal event instance. Do not create manually.", paramNames = {})
    public JSMapSkySetupEvent() {
    }

    @JSHideFromDoc
    public JSMapSkySetupEvent(JSSceneWorld jsSceneWorld, JSEnvironment jsEnvironment) {
        this.jsSceneWorld = jsSceneWorld;
        this.jsEnvironment = jsEnvironment;
    }

    @JSCodingFunctionOrMethod(description = "Get scene world associated with this event", paramNames = {})
    public JSSceneWorld getJsSceneWorld() {
        return this.jsSceneWorld;
    }

    @JSCodingFunctionOrMethod(description = "Get scene environment associated with this event", paramNames = {})
    public JSEnvironment getJsEnvironment() {
        return this.jsEnvironment;
    }

    @JSHideFromDoc
    @Override
    public boolean isCancelled() { return this.cancel; }

    @JSHideFromDoc
    @Override
    public void setCancelled(boolean cancelled) { this.cancel = cancelled; }

    @JSHideFromDoc
    @Override
    public String name() { return "JSMapSkySetupEvent"; }
}