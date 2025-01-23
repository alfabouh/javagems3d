package javagems3d.graphics.rendering.scene.renderer.nodes;

import javagems3d.graphics.objects.SceneObject;
import javagems3d.graphics.objects.rendering.pipeline.enums.Pipeline;
import javagems3d.graphics.rendering.programs.fbo.FBOTexture2DProgram;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.rendering.scene.renderer.nodes.base.IRenderNode;
import javagems3d.graphics.rendering.scene.renderer.processors.geometry.DirectGeometryRenderProcessor;
import javagems3d.graphics.rendering.scene.renderer.processors.skybox.BackgroundRenderProcessor;
import javagems3d.graphics.rendering.scene.renderer.processors.skybox.SkyboxRenderProcessor;
import javagems3d.graphics.screen.ticking.FrameTicking;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public interface IForwardRenderNode extends IRenderNode {
    FBOTexture2DProgram getOutColorBuffer();

    void setForwardRenderingObjects(@NotNull Collection<SceneObject> forwardRenderingObjects);
    Collection<SceneObject> getForwardRenderingObjects();

    final class Default extends IRenderNode.Template implements IForwardRenderNode {
        private Collection<SceneObject> forwardRenderingObjects;
        private DirectGeometryRenderProcessor directGeometryRenderProcessor;
        private SkyboxRenderProcessor skyboxRenderProcessor;
        private BackgroundRenderProcessor backgroundRenderProcessor;
        private final IDeferredRenderNode deferredRenderNode;

        public Default(@NotNull IDeferredRenderNode deferredRenderNode, OpenGLRenderer openGLRenderer) {
            super(openGLRenderer);
            this.deferredRenderNode = deferredRenderNode;
        }

        @Override
        public FBOTexture2DProgram getOutColorBuffer() {
            return this.getDeferredRenderNode().getOutColorBuffer();
        }

        @Override
        public void onRender(FrameTicking frameTicking) {
            this.getBackgroundRenderProcessor().runProcessorRendering(frameTicking);

            this.getOutColorBuffer().bindFBO();
            this.getDirectGeometryRenderProcessor().setDirectMeshObjects(this.getForwardRenderingObjects());
            this.getDirectGeometryRenderProcessor().runProcessorRendering(frameTicking);

            this.getSkyboxRenderProcessor().setBackgroundTexture(this.getBackgroundRenderProcessor().getBackground().getTextureByIndex(0));
            this.getSkyboxRenderProcessor().runProcessorRendering(frameTicking);
            this.getOutColorBuffer().unBindFBO();
        }

        public void initProcessors() {
            this.directGeometryRenderProcessor = new DirectGeometryRenderProcessor(Pipeline.SCENE, this.getOpenGLRenderer());
            this.skyboxRenderProcessor = new SkyboxRenderProcessor(this.getSceneWorld().getEnvironment().getSkyBox(), this.getOpenGLRenderer());
            this.backgroundRenderProcessor = new BackgroundRenderProcessor(this.getSceneWorld().getEnvironment().getSkyBox(), this.getDeferredRenderNode(), this.getOpenGLRenderer());
        }

        public void initFBOs() {
        }

        @Override
        public void createResources() {
            this.initFBOs();
            this.initProcessors();

            this.getDirectGeometryRenderProcessor().createResources();
            this.getSkyboxRenderProcessor().createResources();
            this.getBackgroundRenderProcessor().createResources();
        }

        @Override
        public void destroyResources() {
            this.getDirectGeometryRenderProcessor().destroyResources();
            this.getSkyboxRenderProcessor().destroyResources();
            this.getBackgroundRenderProcessor().destroyResources();
        }

        @Override
        public void setForwardRenderingObjects(@NotNull Collection<SceneObject> forwardRenderingObjects) {
            this.forwardRenderingObjects = forwardRenderingObjects;
        }

        public BackgroundRenderProcessor getBackgroundRenderProcessor() {
            return this.backgroundRenderProcessor;
        }

        public IDeferredRenderNode getDeferredRenderNode() {
            return this.deferredRenderNode;
        }

        public SkyboxRenderProcessor getSkyboxRenderProcessor() {
            return this.skyboxRenderProcessor;
        }

        public DirectGeometryRenderProcessor getDirectGeometryRenderProcessor() {
            return this.directGeometryRenderProcessor;
        }

        @Override
        public Collection<SceneObject> getForwardRenderingObjects() {
            return this.forwardRenderingObjects;
        }
    }
}
