package javagems3d.graphics.rendering.scene.renderer.nodes;

import javagems3d.graphics.rendering.programs.fbo.FBOTexture2DProgram;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.rendering.scene.renderer.nodes.base.IRenderNode;
import javagems3d.graphics.rendering.scene.renderer.processors.post.SceneGluingRenderProcessor;
import javagems3d.graphics.screen.ticking.FrameTicking;
import org.jetbrains.annotations.NotNull;

public interface IGluingRenderNode extends IRenderNode {
    FBOTexture2DProgram getOutGluedScene();

    final class Default extends IRenderNode.Template implements IGluingRenderNode {
        private final SceneGluingRenderProcessor sceneGluingRenderProcessor;

        public Default(@NotNull IDeferredRenderNode deferredRenderNode, OpenGLRenderer openGLRenderer) {
            super(openGLRenderer);
            this.sceneGluingRenderProcessor = new SceneGluingRenderProcessor(deferredRenderNode, openGLRenderer);
        }

        @Override
        public void onRender(FrameTicking frameTicking) {
            this.getSceneGluingRenderProcessor().runProcessorRendering(frameTicking);
        }

        @Override
        public void createResources() {
            this.getSceneGluingRenderProcessor().createResources();
        }

        @Override
        public void destroyResources() {
            this.getSceneGluingRenderProcessor().destroyResources();
        }

        public SceneGluingRenderProcessor getSceneGluingRenderProcessor() {
            return this.sceneGluingRenderProcessor;
        }

        @Override
        public FBOTexture2DProgram getOutGluedScene() {
            return this.getSceneGluingRenderProcessor().getGluedScene();
        }
    }
}
