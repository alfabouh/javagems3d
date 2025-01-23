package javagems3d.graphics.rendering.scene.renderer.nodes;

import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.rendering.scene.renderer.nodes.base.IRenderNode;
import javagems3d.graphics.rendering.ui.jgems_imgui.JGemsUI;
import javagems3d.graphics.screen.ticking.FrameTicking;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL46;

public interface IUIRenderNode extends IRenderNode {
    final class Default implements IUIRenderNode {
        private final OpenGLRenderer openGLRenderer;
        private final JGemsUI jGemsUI;

        public Default(JGemsUI JGemsUI, OpenGLRenderer openGLRenderer) {
            this.openGLRenderer = openGLRenderer;
            this.jGemsUI = JGemsUI;
        }

        @Override
        public void onRender(FrameTicking frameTicking) {
            GL46.glDisable(GL46.GL_DEPTH_TEST);
            GL46.glEnable(GL46.GL_BLEND);
            GL46.glBlendFunc(GL46.GL_SRC_ALPHA, GL46.GL_ONE_MINUS_SRC_ALPHA);
            this.jGemsUI.renderFrame(frameTicking.getFrameDeltaTime());
            GL46.glDisable(GL46.GL_BLEND);
            GL46.glEnable(GL46.GL_DEPTH_TEST);
        }

        @Override
        public @NotNull OpenGLRenderer getOpenGLRenderer() {
            return this.openGLRenderer;
        }

        @Override
        public void createResources() {
        }

        @Override
        public void destroyResources() {
        }
    }
}
