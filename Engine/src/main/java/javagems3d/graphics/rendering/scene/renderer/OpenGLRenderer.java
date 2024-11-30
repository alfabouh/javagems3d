package javagems3d.graphics.rendering.scene.renderer;

import javagems3d.graphics.rendering.scene.ISceneRenderer;
import javagems3d.graphics.rendering.scene.JGemsSceneData;
import javagems3d.graphics.screen.window.IWindow;
import javagems3d.graphics.screen.window.Window;
import org.joml.Vector2i;

public abstract class OpenGLRenderer implements ISceneRenderer {
    private JGemsSceneData sceneData;
    private final IWindow window;

    public OpenGLRenderer(IWindow window, JGemsSceneData sceneData) {
        this.sceneData = sceneData;
        this.window = window;
    }

    public OpenGLRenderer setSceneData(JGemsSceneData sceneData) {
        this.sceneData = sceneData;
        return this;
    }

    public void recreateResources() {
        this.destroyResources();
        this.createResources();
    }

    public final Vector2i getWindowSize() {
        return this.getWindow().getWindowSize();
    }

    @Override
    public IWindow getWindow() {
        return this.window;
    }

    public JGemsSceneData getSceneData() {
        return this.sceneData;
    }
}
