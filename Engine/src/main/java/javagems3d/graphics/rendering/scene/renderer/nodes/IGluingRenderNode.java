package javagems3d.graphics.rendering.scene.renderer.nodes;

import javagems3d.graphics.rendering.programs.fbo.FBOTexture2DProgram;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.rendering.scene.renderer.nodes.base.IRenderNode;
import javagems3d.graphics.rendering.scene.renderer.processors.post.GluingRenderProcessor;
import javagems3d.graphics.screen.ticking.FrameTicking;
import javagems3d.system.resources.managing.JGemsResourceManager;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL46;

public interface IGluingRenderNode extends IRenderNode {
    FBOTexture2DProgram getInColorBuffer();
    FBOTexture2DProgram getOutColorBuffer();

    final class Default extends IRenderNode.Template implements IGluingRenderNode {
        private GluingRenderProcessor gluingRenderProcessor;
        private final FBOTexture2DProgram inColor;

        public Default(@NotNull FBOTexture2DProgram inColor, OpenGLRenderer openGLRenderer) {
            super(openGLRenderer);
            this.inColor = inColor;
        }

        @Override
        public FBOTexture2DProgram getInColorBuffer() {
            return this.inColor;
        }

        @Override
        public FBOTexture2DProgram getOutColorBuffer() {
            return this.getInColorBuffer();
        }

        @Override
        public void onRender(FrameTicking frameTicking) {
            this.getOutColorBuffer().bindFBO();
            GL46.glClear(GL46.GL_DEPTH_BUFFER_BIT);
            this.getSceneGluingRenderProcessor().runProcessorRendering(frameTicking);
            this.getOutColorBuffer().unBindFBO();
        }

        @Override
        public void createResources() {
            this.gluingRenderProcessor = new GluingRenderProcessor(this.getOutColorBuffer(), this.getOpenGLRenderer(), JGemsResourceManager.globalShaderAssets.scene_gluing);
            this.getSceneGluingRenderProcessor().createResources();
        }

        @Override
        public void destroyResources() {
            this.getSceneGluingRenderProcessor().destroyResources();
        }

        public GluingRenderProcessor getSceneGluingRenderProcessor() {
            return this.gluingRenderProcessor;
        }
    }
}
