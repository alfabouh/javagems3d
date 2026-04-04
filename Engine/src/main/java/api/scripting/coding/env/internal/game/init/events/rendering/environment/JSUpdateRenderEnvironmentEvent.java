package api.scripting.coding.env.internal.game.init.events.rendering.environment;

import api.scripting.coding.env.def.*;
import api.scripting.coding.env.internal.util.events.JSEventI;
import api.scripting.coding.env.internal.util.events.JSEventRun;
import api.scripting.coding.env.internal.util.world.render.screen.camera.JSCamera;
import api.scripting.coding.env.internal.util.world.render.world.environment.JSEnvironment;

@JSCodingClass(binding = "JSUpdateRenderEnvironmentEvent", description = "Event triggered when a render environment is updated.")
public class JSUpdateRenderEnvironmentEvent implements JSEventI {
    @JSCodingField(description = "The rendering environment being updated")
    @JSHideFromDoc
    private JSEnvironment jsEnvironment;

    @JSCodingField(description = "The camera used for rendering")
    @JSHideFromDoc
    private JSCamera jsCamera;

    @JSCodingField(description = "Event run state (PRE or POST)")
    @JSHideFromDoc
    private JSEventRun jsRun;

    @JSCodingConstructor(description = "Internal event instance. Do not create manually.")
    public JSUpdateRenderEnvironmentEvent() {
    }

    @JSHideFromDoc
    public JSUpdateRenderEnvironmentEvent(JSEnvironment jsEnvironment, JSCamera jsCamera, JSEventRun jsRun) {
        this.jsEnvironment = jsEnvironment;
        this.jsCamera = jsCamera;
        this.jsRun = jsRun;
    }

    @JSCodingFunctionOrMethod(description = "Get the rendering environment")
    public JSEnvironment getEnvironment() {
        return this.jsEnvironment;
    }

    @JSCodingFunctionOrMethod(description = "Get the camera used for rendering")
    public JSCamera getCamera() {
        return this.jsCamera;
    }

    @JSCodingFunctionOrMethod(description = "Get the run state (PRE or POST)")
    public JSEventRun getRun() {
        return this.jsRun;
    }

    @JSHideFromDoc
    @Override
    public String name() {
        return "JSUpdateRenderEnvironmentEvent";
    }
}