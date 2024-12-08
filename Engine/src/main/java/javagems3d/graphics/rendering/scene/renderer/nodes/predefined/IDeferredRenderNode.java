package javagems3d.graphics.rendering.scene.renderer.nodes.predefined;

import javagems3d.graphics.rendering.programs.fbo.FBOTexture2DProgram;
import javagems3d.graphics.rendering.scene.renderer.JGemsOpenGLRenderer;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.rendering.scene.renderer.nodes.IRenderNode;
import javagems3d.graphics.rendering.scene.renderer.processors.IndirectGeometryRenderProcessor;
import javagems3d.graphics.screen.ticking.FrameTicking;
import org.jetbrains.annotations.NotNull;

public interface IDeferredRenderNode extends IRenderNode {
    @NotNull FBOTexture2DProgram getOutFboGBuffer();

    final class Default extends IRenderNode.Template implements IDeferredRenderNode {
        private final IndirectGeometryRenderProcessor indirectGeometryRenderProcessor;

        public Default(OpenGLRenderer openGLRenderer) {
            super(openGLRenderer);
            this.indirectGeometryRenderProcessor = new IndirectGeometryRenderProcessor(openGLRenderer);
        }

        public @NotNull FBOTexture2DProgram getOutFboGBuffer() {
            return this.getIndirectGeometryRenderProcessor().getGBuffer();
        }

        @Override
        public void onRender(FrameTicking frameTicking) {
            this.getOutFboGBuffer().bindFBO();
            this.getIndirectGeometryRenderProcessor().setIndirectMeshObjects(JGemsOpenGLRenderer.getFilteredSetToRender(this.getOpenGLRenderer().getSceneWorld().getSceneObjects()));
            this.getIndirectGeometryRenderProcessor().onRender(frameTicking);
            this.getOutFboGBuffer().unBindFBO();
        }

        @Override
        public void createResources() {
            this.getIndirectGeometryRenderProcessor().createResources();
        }

        @Override
        public void destroyResources() {
            this.getIndirectGeometryRenderProcessor().destroyResources();
        }

        public IndirectGeometryRenderProcessor getIndirectGeometryRenderProcessor() {
            return this.indirectGeometryRenderProcessor;
        }
    }
}
