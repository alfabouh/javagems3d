package api.scripting.coding.env.internal.util.world.render.processing;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;

@JSCodingClass(binding = "JSOpenGLRenderer", description = "Wrapper for OpenGLRenderer. Provides access to window, world, and basic rendering operations.")
public class JSOpenGLRenderer {
    private final OpenGLRenderer renderer;

    @JSHideFromDoc
    public JSOpenGLRenderer(OpenGLRenderer renderer) {
        this.renderer = renderer;
    }

    @JSCodingFunctionOrMethod(description = "Real java object")
    public OpenGLRenderer getJavaRenderer() {
        return this.renderer;
    }
}