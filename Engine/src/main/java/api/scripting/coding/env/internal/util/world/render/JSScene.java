package api.scripting.coding.env.internal.util.world.render;

import api.scripting.coding.env.def.*;
import api.scripting.coding.env.internal.util.world.render.screen.JSWindow;
import javagems3d.graphics.rendering.scene.JGemsScene;

@JSCodingClass(binding = "JSScene", description = "Wrapper for JGemsScene providing access to rendering and window.")
public class JSScene {
    @JSCodingField(description = "Underlying Java scene object")
    private final JGemsScene scene;

    @JSHideFromDoc
    public JSScene(JGemsScene scene) {
        this.scene = scene;
    }

    @JSCodingConstructor(description = "Create JSScene with window and render world", paramNames = {"window", "renderWorld"})
    public JSScene(JSWindow window, JSRenderWorld renderWorld) {
        this.scene = new JGemsScene(window.getJavaWindow(), renderWorld.getJavaRenderWorld());
    }

    @JSCodingFunctionOrMethod(description = "Get associated window")
    public JSWindow getWindow() {
        return new JSWindow(this.scene.getWindow());
    }

    @JSCodingFunctionOrMethod(description = "Get underlying Java scene object")
    public JGemsScene getJavaScene() {
        return this.scene;
    }
}