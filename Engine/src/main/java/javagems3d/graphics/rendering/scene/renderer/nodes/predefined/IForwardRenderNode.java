package javagems3d.graphics.rendering.scene.renderer.nodes.predefined;

import javagems3d.graphics.objects.SceneObject;
import javagems3d.graphics.rendering.programs.fbo.FBOTexture2DProgram;
import javagems3d.graphics.rendering.programs.fbo.attachments.T2DAttachmentContainer;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.rendering.scene.renderer.nodes.IRenderNode;
import javagems3d.graphics.rendering.scene.renderer.processors.predefined.DirectGeometryRenderProcessor;
import javagems3d.graphics.screen.ticking.FrameTicking;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL46;

import java.util.Collection;

public interface IForwardRenderNode extends IRenderNode {
    FBOTexture2DProgram getOutColorBuffer();
    FBOTexture2DProgram getOutSkyColorBuffer();

    void setForwardRenderingObjects(@NotNull Collection<SceneObject> forwardRenderingObjects);
    Collection<SceneObject> getForwardRenderingObjects();

    final class Default extends IRenderNode.Template implements IForwardRenderNode {
        private Collection<SceneObject> forwardRenderingObjects;
        private DirectGeometryRenderProcessor directGeometryRenderProcessor;
        private FBOTexture2DProgram colorBuffer;
        private final IDeferredRenderNode deferredRenderNode;
        
        public Default(@NotNull IDeferredRenderNode deferredRenderNode, OpenGLRenderer openGLRenderer) {
            super(openGLRenderer);
            this.deferredRenderNode = deferredRenderNode;
        }

        @Override
        public FBOTexture2DProgram getOutColorBuffer() {
            return this.colorBuffer;
        }

        @Override
        public void onRender(FrameTicking frameTicking) {
            this.getDeferredRenderNode().getOutGBuffer().copyFBOtoFBODepth(this.getOutColorBuffer().getFrameBufferId(), this.getRenderingResolution());
            this.getOutColorBuffer().bindFBO();
            GL46.glClear(GL46.GL_COLOR_BUFFER_BIT | GL46.GL_DEPTH_BUFFER_BIT);
            this.getDirectGeometryRenderProcessor().setDirectMeshObjects(this.getForwardRenderingObjects());
            this.getDirectGeometryRenderProcessor().runProcessorRendering(frameTicking);
            this.getOutColorBuffer().unBindFBO();
        }

        public void initProcessors() {
            this.directGeometryRenderProcessor = new DirectGeometryRenderProcessor(this.getOpenGLRenderer());
        }

        public void initFBOs() {
            this.colorBuffer = new FBOTexture2DProgram(true);
            T2DAttachmentContainer clr = new T2DAttachmentContainer() {{
                add(GL46.GL_COLOR_ATTACHMENT0, GL46.GL_RGB16F, GL46.GL_RGB);
                add(GL46.GL_COLOR_ATTACHMENT1, GL46.GL_RGB16F, GL46.GL_RGB);
            }};
            this.colorBuffer.createFrameBuffer2DTexture(this.getRenderingResolution(), clr, false, GL46.GL_LINEAR, GL46.GL_NONE, GL46.GL_LESS, GL46.GL_CLAMP_TO_EDGE, null);
        }

        @Override
        public void createResources() {
            this.initFBOs();
            this.initProcessors();
            this.getDirectGeometryRenderProcessor().createResources();
        }

        @Override
        public void destroyResources() {
            this.getDirectGeometryRenderProcessor().destroyResources();

            if (this.getOutColorBuffer() != null) {
                this.getOutColorBuffer().clearFBO();
            }
        }

        public IDeferredRenderNode getDeferredRenderNode() {
            return this.deferredRenderNode;
        }

        @Override
        public void setForwardRenderingObjects(@NotNull Collection<SceneObject> forwardRenderingObjects) {
            this.forwardRenderingObjects = forwardRenderingObjects;
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
