package api.scripting.coding.env.internal.game.init.events.rendering.ogl;

import api.scripting.coding.env.def.*;
import api.scripting.coding.env.internal.util.events.JSEventI;
import api.scripting.coding.env.internal.util.world.render.processing.JSOpenGLRenderer;

@JSCodingClass(binding = "JSStopRendererOGLEvent", description = "Event triggered when the OpenGL renderer is stopped.")
public class JSStopRendererOGLEvent implements JSEventI {

    @JSCodingField(description = "The OpenGL renderer")
    @JSHideFromDoc
    private JSOpenGLRenderer jsRenderer;

    @JSCodingConstructor(description = "Internal event instance. Do not create manually.")
    public JSStopRendererOGLEvent() {
    }

    @JSHideFromDoc
    public JSStopRendererOGLEvent(JSOpenGLRenderer jsRenderer) {
        this.jsRenderer = jsRenderer;
    }

    @JSCodingFunctionOrMethod(description = "Get the OpenGL renderer")
    public JSOpenGLRenderer getRenderer() {
        return this.jsRenderer;
    }

    @JSHideFromDoc
    @Override
    public String name() {
        return "JSStopRendererOGLEvent";
    }
}