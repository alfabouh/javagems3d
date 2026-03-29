package api.scripting.coding.env.internal.util.world.render;

import api.scripting.coding.env.def.*;
import api.scripting.coding.env.internal.util.world.render.screen.JSWindow;
import api.scripting.coding.env.internal.util.world.render.world.JSSceneWorld;
import javagems3d.graphics.rendering.scene.JGemsScene;
import javagems3d.graphics.world.SceneWorld;

@JSCodingClass(binding = "JSScene", description = "Wrapper for JGemsScene providing access to rendering and window.")
public class JSScene {
    @JSCodingField(description = "Underlying Java scene object")
    private final JGemsScene scene;
    private final JSSceneWorld sceneWorld;

    @JSHideFromDoc
    public JSScene(JGemsScene scene) {
        this.scene = scene;
        this.sceneWorld = new JSSceneWorld((SceneWorld) scene.getWorld());
    }

    @JSCodingFunctionOrMethod(description = "Get associated window")
    public JSSceneWorld getSceneWorld() {
        return this.sceneWorld;
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