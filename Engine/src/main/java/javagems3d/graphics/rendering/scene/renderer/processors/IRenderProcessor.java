package javagems3d.graphics.rendering.scene.renderer.processors;

import javagems3d.graphics.rendering.scene.renderer.IResourceInit;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.screen.ticking.FrameTicking;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;

public interface IRenderProcessor extends IResourceInit {
    void onRender(FrameTicking frameTicking);

    default Vector2i getWindowSize() {
        return this.getOpenGLRenderer().getWindowSize();
    }

    @NotNull OpenGLRenderer getOpenGLRenderer();

    abstract class Template implements IRenderProcessor {
        private final OpenGLRenderer openGLRenderer;

        public Template(OpenGLRenderer openGLRenderer) {
            this.openGLRenderer = openGLRenderer;
        }

        @Override
        @NotNull
        public OpenGLRenderer getOpenGLRenderer() {
            return this.openGLRenderer;
        }
    }
}