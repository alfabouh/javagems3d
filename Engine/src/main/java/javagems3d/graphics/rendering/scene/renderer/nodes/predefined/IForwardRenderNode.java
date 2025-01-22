package javagems3d.graphics.rendering.scene.renderer.nodes.predefined;

import javagems3d.graphics.objects.SceneObject;
import javagems3d.graphics.rendering.programs.fbo.FBOTexture2DProgram;
import javagems3d.graphics.rendering.programs.fbo.attachments.T2DAttachmentContainer;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.rendering.scene.renderer.nodes.IRenderNode;
import javagems3d.graphics.rendering.scene.renderer.processors.predefined.DirectGeometryRenderProcessor;
import javagems3d.graphics.rendering.scene.renderer.processors.predefined.SkyboxRenderProcessor;
import javagems3d.graphics.screen.ticking.FrameTicking;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL46;

import java.util.Collection;

public interface IForwardRenderNode extends IRenderNode {
    FBOTexture2DProgram getOutColorBuffer();

    void setForwardRenderingObjects(@NotNull Collection<SceneObject> forwardRenderingObjects);
    Collection<SceneObject> getForwardRenderingObjects();

    final class Default extends IRenderNode.Template implements IForwardRenderNode {
        private Collection<SceneObject> forwardRenderingObjects;
        private DirectGeometryRenderProcessor directGeometryRenderProcessor;
        private SkyboxRenderProcessor skyboxRenderProcessor;
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
            this.getOutColorBuffer().bindFBO();
            this.getDirectGeometryRenderProcessor().setDirectMeshObjects(this.getForwardRenderingObjects());
            this.getDirectGeometryRenderProcessor().runProcessorRendering(frameTicking);

            this.getSkyboxRenderProcessor().runProcessorRendering(frameTicking);
            this.getOutColorBuffer().unBindFBO();
        }

        public void initProcessors() {
            this.directGeometryRenderProcessor = new DirectGeometryRenderProcessor(this.getOpenGLRenderer());
            this.skyboxRenderProcessor = new SkyboxRenderProcessor(this.getOpenGLRenderer().getSceneWorld().getEnvironment().getSkyBox(), this.getDeferredRenderNode(), this.getOpenGLRenderer());
        }

        public void initFBOs() {
        }

        @Override
        public void createResources() {
            this.initFBOs();
            this.initProcessors();

            this.getDirectGeometryRenderProcessor().createResources();
            this.getSkyboxRenderProcessor().createResources();
        }

        @Override
        public void destroyResources() {
            this.getDirectGeometryRenderProcessor().destroyResources();
            this.getSkyboxRenderProcessor().destroyResources();
        }

        @Override
        public void setForwardRenderingObjects(@NotNull Collection<SceneObject> forwardRenderingObjects) {
            this.forwardRenderingObjects = forwardRenderingObjects;
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
