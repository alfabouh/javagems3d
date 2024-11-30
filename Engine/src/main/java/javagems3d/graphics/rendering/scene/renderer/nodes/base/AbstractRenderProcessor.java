package javagems3d.graphics.rendering.scene.renderer.nodes.base;

import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.screen.window.IWindow;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;

public abstract class AbstractRenderProcessor implements IRenderProcessor {
    private final OpenGLRenderer openGLRenderer;
    private int renderOrder;

    public AbstractRenderProcessor(int renderOrder, @NotNull OpenGLRenderer openGLRenderer) {
        this.openGLRenderer = openGLRenderer;
        this.renderOrder = renderOrder;
    }

    public AbstractRenderProcessor setRenderOrder(int renderOrder) {
        this.renderOrder = renderOrder;
        return this;
    }

    @Override
    public int getNodeOrder() {
        return this.renderOrder;
    }

    public final Vector2i getWindowSize() {
        return this.getOpenGLRenderer().getWindowSize();
    }

    public @NotNull OpenGLRenderer getOpenGLRenderer() {
        return this.openGLRenderer;
    }

    @Override
    public void onWindowResize(IWindow window) {
        //pass
    }
}
