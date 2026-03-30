package api.scripting.coding.env.internal.util.world.render;

import api.scripting.coding.env.def.*;
import api.scripting.coding.env.internal.util.world.render.screen.JSWindow;
import api.scripting.coding.env.internal.util.world.render.world.JSSceneWorld;
import javagems3d.graphics.rendering.scene.JGemsScene;
import javagems3d.graphics.screen.JGemsScreen;
import javagems3d.graphics.world.SceneWorld;

@JSCodingClass(binding = "JSScene", description = "Wrapper for JGemsScene providing access to rendering and window.")
public class JSScene {
    private final JGemsScreen gemsScreen;

    @JSHideFromDoc
    public JSScene(JGemsScreen screen) {
        this.gemsScreen = screen;
    }

    @JSCodingFunctionOrMethod(description = "Get associated window")
    public JSSceneWorld getSceneWorld() {
        return new JSSceneWorld((SceneWorld) this.gemsScreen.getSceneWorld());
    }

    @JSCodingFunctionOrMethod(description = "Get associated window")
    public JSWindow getWindow() {
        return new JSWindow(this.gemsScreen.getWindow());
    }

    @JSCodingFunctionOrMethod(description = "Get underlying Java scene object")
    public JGemsScene getJavaScene() {
        return this.gemsScreen.getScene();
    }
}