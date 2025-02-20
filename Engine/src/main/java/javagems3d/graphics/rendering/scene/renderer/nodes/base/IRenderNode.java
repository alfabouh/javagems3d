package javagems3d.graphics.rendering.scene.renderer.nodes.base;

import javagems3d.graphics.rendering.scene.renderer.IResourceInit;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.screen.ticking.FrameTicking;
import javagems3d.graphics.screen.window.IWindow;
import javagems3d.graphics.world.SceneWorld;
import javagems3d.physics.world.IWorld;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;

public interface IRenderNode extends IWindow.ResizeEvent, IResourceInit {
    void onRender(FrameTicking frameTicking);
    @NotNull OpenGLRenderer getOpenGLRenderer();

    default Vector2i getRenderingResolution() {
        return this.getOpenGLRenderer().getRenderingResolution();
    }
    
    @Override
    default void onWindowResize(IWindow window) {
        this.recreateResources();
    }

    abstract class Template {
        private final OpenGLRenderer openGLRenderer;

        public Template(@NotNull OpenGLRenderer openGLRenderer) {
            this.openGLRenderer = openGLRenderer;
        }

        @NotNull
        public IWorld getSceneWorld() {
            return this.getOpenGLRenderer().getWorld();
        }

        @NotNull
        public OpenGLRenderer getOpenGLRenderer() {
            return this.openGLRenderer;
        }
    }
}
