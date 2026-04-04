package api.scripting.coding.env.internal.game.init.events.rendering.environment;

import api.scripting.coding.env.def.*;
import api.scripting.coding.env.internal.util.events.JSEventI;
import api.scripting.coding.env.internal.util.world.render.world.environment.JSEnvironment;

@JSCodingClass(binding = "JSDestroyRenderEnvironmentEvent", description = "Event triggered when a render environment is destroyed.")
public class JSDestroyRenderEnvironmentEvent implements JSEventI {
    @JSCodingField(description = "The rendering environment being destroyed")
    @JSHideFromDoc
    private JSEnvironment jsEnvironment;

    @JSCodingConstructor(description = "Internal event instance. Do not create manually.")
    public JSDestroyRenderEnvironmentEvent() {
    }

    @JSHideFromDoc
    public JSDestroyRenderEnvironmentEvent(JSEnvironment jsEnvironment) {
        this.jsEnvironment = jsEnvironment;
    }

    @JSCodingFunctionOrMethod(description = "Get the rendering environment being destroyed")
    public JSEnvironment getEnvironment() {
        return this.jsEnvironment;
    }

    @JSHideFromDoc
    @Override
    public String name() {
        return "JSDestroyRenderEnvironmentEvent";
    }
}