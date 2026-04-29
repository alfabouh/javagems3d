package api.scripting.coding.env.internal.game.init.events.rendering.environment;

import api.scripting.coding.env.def.*;
import api.scripting.coding.env.internal.util.events.JSEventI;
import api.scripting.coding.env.internal.util.world.render.processing.JSOpenGLRenderer;
import api.scripting.coding.env.internal.util.world.render.world.environment.JSEnvironment;

@JSCodingClass(binding = "JSCreateRenderEnvironmentEvent", description = "Event triggered when a render environment is created.")
public class JSCreateRenderEnvironmentEvent implements JSEventI {
    @JSCodingField(description = "The rendering environment")
    @JSHideFromDoc
    private JSEnvironment jsEnvironment;

    @JSCodingField(description = "The OpenGL renderer used for rendering")
    @JSHideFromDoc
    private JSOpenGLRenderer jsRenderer;

    @JSCodingConstructor(description = "Internal event instance. Do not create manually.")
    public JSCreateRenderEnvironmentEvent() {
    }

    @JSHideFromDoc
    public JSCreateRenderEnvironmentEvent(JSEnvironment jsEnvironment, JSOpenGLRenderer jsRenderer) {
        this.jsEnvironment = jsEnvironment;
        this.jsRenderer = jsRenderer;
    }

    @JSCodingFunctionOrMethod(description = "Get the rendering environment")
    public JSEnvironment getEnvironment() {
        return this.jsEnvironment;
    }

    @JSCodingFunctionOrMethod(description = "Get the OpenGL renderer")
    public JSOpenGLRenderer getRenderer() {
        return this.jsRenderer;
    }

    @JSHideFromDoc
    @Override
    public String name() {
        return "JSCreateRenderEnvironmentEvent";
    }
}