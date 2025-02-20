package javagems3d.graphics.rendering.scene.renderer.processors;

import javagems3d.graphics.rendering.scene.renderer.IResourceInit;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.screen.ticking.FrameTicking;
import javagems3d.graphics.world.SceneWorld;
import javagems3d.physics.world.IWorld;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;

public interface IRenderProcessor extends IResourceInit {
    void runProcessorRendering(FrameTicking frameTicking);

    default Vector2i getRenderingResolution() {
        return this.getOpenGLRenderer().getRenderingResolution();
    }

    @NotNull OpenGLRenderer getOpenGLRenderer();

    abstract class Template implements IRenderProcessor {
        private final OpenGLRenderer openGLRenderer;

        public Template(OpenGLRenderer openGLRenderer) {
            this.openGLRenderer = openGLRenderer;
        }

        @NotNull
        public IWorld getSceneWorld() {
            return this.getOpenGLRenderer().getWorld();
        }

        @Override
        @NotNull
        public OpenGLRenderer getOpenGLRenderer() {
            return this.openGLRenderer;
        }
    }
}