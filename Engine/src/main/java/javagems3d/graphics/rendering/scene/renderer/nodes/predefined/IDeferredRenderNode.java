package javagems3d.graphics.rendering.scene.renderer.nodes.predefined;

import javagems3d.graphics.objects.SceneObject;
import javagems3d.graphics.rendering.programs.fbo.FBOTexture2DProgram;
import javagems3d.graphics.rendering.programs.fbo.attachments.T2DAttachmentContainer;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.rendering.scene.renderer.nodes.IRenderNode;
import javagems3d.graphics.rendering.scene.renderer.processors.predefined.IndirectGeometryRenderProcessor;
import javagems3d.graphics.rendering.scene.renderer.processors.predefined.DeferredSceneColorRenderProcessor;
import javagems3d.graphics.rendering.scene.renderer.processors.predefined.SSAORenderProcessor;
import javagems3d.graphics.screen.ticking.FrameTicking;
import javagems3d.system.resources.managing.JGemsResourceManager;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL46;

import java.util.Collection;
import java.util.HashSet;

public interface IDeferredRenderNode extends IRenderNode {
    FBOTexture2DProgram getOutGBuffer();
    FBOTexture2DProgram getOutColorBuffer();
    FBOTexture2DProgram getOutSSAOBuffer();

    void setIndirectDeferredRenderingObjects(@NotNull Collection<SceneObject> indirectDeferredRenderingObjects);
    void setDirectDeferredRenderingObjects(@NotNull Collection<SceneObject> directDeferredRenderingObjects);
    Collection<SceneObject> getIndirectDeferredRenderingObjects();
    Collection<SceneObject> getDirectDeferredRenderingObjects();

    final class Default extends IRenderNode.Template implements IDeferredRenderNode {
        private FBOTexture2DProgram gBuffer;
        private FBOTexture2DProgram ssaoBuffer;

        private IndirectGeometryRenderProcessor indirectGeometryRenderProcessor;
        private SSAORenderProcessor ssaoRenderProcessor;
        private DeferredSceneColorRenderProcessor rawColorRenderProcessor;

        private Collection<SceneObject> indirectDeferredRenderingObjects;
        private Collection<SceneObject> directDeferredRenderingObjects;

        public Default(OpenGLRenderer openGLRenderer) {
            super(openGLRenderer);
            this.indirectDeferredRenderingObjects = new HashSet<>();
            this.directDeferredRenderingObjects = new HashSet<>();
        }

        public FBOTexture2DProgram getOutGBuffer() {
            return this.gBuffer;
        }

        @Override
        public FBOTexture2DProgram getOutColorBuffer() {
            return this.getRawColorRenderProcessor().getColorBuffer();
        }

        @Override
        public FBOTexture2DProgram getOutSSAOBuffer() {
            return this.ssaoBuffer;
        }

        @Override
        public void onRender(FrameTicking frameTicking) {
            this.getOutGBuffer().bindFBO();
            GL46.glClear(GL46.GL_COLOR_BUFFER_BIT | GL46.GL_DEPTH_BUFFER_BIT);
            this.getIndirectGeometryRenderProcessor().setIndirectMeshObjects(this.getIndirectDeferredRenderingObjects());
            this.getIndirectGeometryRenderProcessor().onRender(frameTicking);
            this.getOutGBuffer().unBindFBO();

            this.getOutSSAOBuffer().bindFBO();
            this.getSSAORenderProcessor().onRender(frameTicking);
            this.getOutSSAOBuffer().unBindFBO();

            this.getRawColorRenderProcessor().setSsaoValid(this.getSSAORenderProcessor().isValid());
            this.getRawColorRenderProcessor().onRender(frameTicking);
        }

        public void initProcessors() {
            this.indirectGeometryRenderProcessor = new IndirectGeometryRenderProcessor(this.getOpenGLRenderer());
            this.ssaoRenderProcessor = new SSAORenderProcessor(this.getOpenGLRenderer(), this.getOutGBuffer(), JGemsResourceManager.globalShaderAssets.world_ssao);
            this.rawColorRenderProcessor = new DeferredSceneColorRenderProcessor(this.getOpenGLRenderer(), this.getOutGBuffer(), this.getOutSSAOBuffer(), JGemsResourceManager.globalShaderAssets.world_deferred);
        }

        public void initFBOs() {
            this.gBuffer = new FBOTexture2DProgram(true);
            this.ssaoBuffer = new FBOTexture2DProgram(true);

            T2DAttachmentContainer gBuffer = new T2DAttachmentContainer() {{
                add(GL46.GL_COLOR_ATTACHMENT0, GL46.GL_RGB32F, GL46.GL_RGB);
                add(GL46.GL_COLOR_ATTACHMENT1, GL46.GL_RGB32F, GL46.GL_RGB);
                add(GL46.GL_COLOR_ATTACHMENT2, GL46.GL_RGBA, GL46.GL_RGBA);
                add(GL46.GL_COLOR_ATTACHMENT3, GL46.GL_RGB, GL46.GL_RGB);
                add(GL46.GL_COLOR_ATTACHMENT4, GL46.GL_RGB, GL46.GL_RGB);
            }};
            T2DAttachmentContainer ssao = new T2DAttachmentContainer() {{
                add(GL46.GL_COLOR_ATTACHMENT0, GL46.GL_R16F, GL46.GL_RED);
            }};

            this.getOutGBuffer().createFrameBuffer2DTexture(this.getOpenGLRenderer().getRenderingResolution(), gBuffer, true, GL46.GL_NEAREST, GL46.GL_NONE, GL46.GL_LESS, GL46.GL_CLAMP_TO_EDGE, null);
            this.ssaoBuffer.createFrameBuffer2DTexture(this.getOpenGLRenderer().getRenderingResolution(), ssao, false, GL46.GL_LINEAR, GL46.GL_NONE, GL46.GL_LESS, GL46.GL_CLAMP_TO_EDGE, null);
        }

        @Override
        public void createResources() {
            this.initFBOs();
            this.initProcessors();

            this.getSSAORenderProcessor().createResources();
            this.getIndirectGeometryRenderProcessor().createResources();
            this.getRawColorRenderProcessor().createResources();
        }

        @Override
        public void destroyResources() {
            this.getSSAORenderProcessor().destroyResources();
            this.getIndirectGeometryRenderProcessor().destroyResources();
            this.getRawColorRenderProcessor().destroyResources();

            if (this.getOutGBuffer() != null) {
                this.getOutGBuffer().clearFBO();
            }
            if (this.getOutSSAOBuffer() != null) {
                this.getOutSSAOBuffer().clearFBO();
            }
        }

        public void setIndirectDeferredRenderingObjects(@NotNull Collection<SceneObject> indirectDeferredRenderingObjects) {
            this.indirectDeferredRenderingObjects = indirectDeferredRenderingObjects;
        }

        public void setDirectDeferredRenderingObjects(@NotNull Collection<SceneObject> directDeferredRenderingObjects) {
            this.directDeferredRenderingObjects = directDeferredRenderingObjects;
        }

        public Collection<SceneObject> getIndirectDeferredRenderingObjects() {
            return this.indirectDeferredRenderingObjects;
        }

        public Collection<SceneObject> getDirectDeferredRenderingObjects() {
            return this.directDeferredRenderingObjects;
        }

        public DeferredSceneColorRenderProcessor getRawColorRenderProcessor() {
            return this.rawColorRenderProcessor;
        }

        public SSAORenderProcessor getSSAORenderProcessor() {
            return this.ssaoRenderProcessor;
        }

        public IndirectGeometryRenderProcessor getIndirectGeometryRenderProcessor() {
            return this.indirectGeometryRenderProcessor;
        }
    }
}
