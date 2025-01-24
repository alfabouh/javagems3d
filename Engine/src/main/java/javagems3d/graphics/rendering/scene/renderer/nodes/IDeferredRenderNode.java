package javagems3d.graphics.rendering.scene.renderer.nodes;

import javagems3d.graphics.camera.base.ICamera;
import javagems3d.graphics.objects.SceneObject;
import javagems3d.graphics.objects.rendering.pipeline.enums.Pipeline;
import javagems3d.graphics.rendering.programs.fbo.FBOTexture2DProgram;
import javagems3d.graphics.rendering.programs.fbo.attachments.T2DAttachmentContainer;
import javagems3d.graphics.rendering.programs.shaders.unifrom.UniformFunctions;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.rendering.scene.renderer.nodes.base.IRenderNode;
import javagems3d.graphics.rendering.scene.renderer.processors.geometry.DirectGeometryRenderProcessor;
import javagems3d.graphics.rendering.scene.renderer.processors.geometry.IndirectGeometryRenderProcessor;
import javagems3d.graphics.rendering.scene.renderer.processors.post.DeferredColorRenderProcessor;
import javagems3d.graphics.rendering.scene.renderer.processors.post.SSAORenderProcessor;
import javagems3d.graphics.screen.ticking.FrameTicking;
import javagems3d.graphics.transformation.JGemsTransformation;
import javagems3d.graphics.world.SceneWorld;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import javagems3d.system.resources.assets.shaders.uniform.UniformString;
import javagems3d.system.resources.assets.texturing.CubeMapTexture;
import javagems3d.system.resources.managing.JGemsResourceManager;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL46;

import java.util.Collection;
import java.util.HashSet;
import java.util.function.Consumer;

public interface IDeferredRenderNode extends IRenderNode {
    FBOTexture2DProgram getOutGBuffer();
    FBOTexture2DProgram getOutColorBuffer();
    FBOTexture2DProgram getOutSSAOBuffer();

    void setIndirectDeferredRenderingObjects(@NotNull Collection<SceneObject> indirectDeferredRenderingObjects);
    void setDirectDeferredRenderingObjects(@NotNull Collection<SceneObject> directDeferredRenderingObjects);
    Collection<SceneObject> getIndirectDeferredRenderingObjects();
    Collection<SceneObject> getDirectDeferredRenderingObjects();

    final class Default extends IRenderNode.Template implements IDeferredRenderNode {
        private final FBOTexture2DProgram startColorFBO;
        private FBOTexture2DProgram gBuffer;
        private FBOTexture2DProgram ssaoBuffer;

        private DirectGeometryRenderProcessor directGeometryRenderProcessor;
        private IndirectGeometryRenderProcessor indirectGeometryRenderProcessor;
        private SSAORenderProcessor ssaoRenderProcessor;
        private DeferredColorRenderProcessor rawColorRenderProcessor;

        private Collection<SceneObject> indirectDeferredRenderingObjects;
        private Collection<SceneObject> directDeferredRenderingObjects;

        public Default(@NotNull FBOTexture2DProgram startColorFbo, OpenGLRenderer openGLRenderer) {
            super(openGLRenderer);
            this.indirectDeferredRenderingObjects = new HashSet<>();
            this.directDeferredRenderingObjects = new HashSet<>();
            this.startColorFBO = startColorFbo;
        }

        @Override
        public FBOTexture2DProgram getOutColorBuffer() {
            return this.startColorFBO;
        }

        public FBOTexture2DProgram getOutGBuffer() {
            return this.gBuffer;
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
            this.getIndirectGeometryRenderProcessor().runProcessorRendering(frameTicking);
            this.getDirectGeometryRenderProcessor().setDirectMeshObjects(this.getDirectDeferredRenderingObjects());
            this.getDirectGeometryRenderProcessor().runProcessorRendering(frameTicking);
            this.getOutGBuffer().unBindFBO();

            this.getOutSSAOBuffer().bindFBO();
            this.getSSAORenderProcessor().runProcessorRendering(frameTicking);
            this.getOutSSAOBuffer().unBindFBO();

            this.getOutColorBuffer().bindFBO();
            GL46.glClear(GL46.GL_COLOR_BUFFER_BIT | GL46.GL_DEPTH_BUFFER_BIT);
            this.getRawColorRenderProcessor().setSsaoValidate(this.getSSAORenderProcessor().isValid());
            this.getRawColorRenderProcessor().runProcessorRendering(frameTicking);
            this.getOutColorBuffer().unBindFBO();

            this.getOutGBuffer().copyFBOtoFBODepth(this.getOutColorBuffer().getFrameBufferId(), this.getRenderingResolution());
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
            T2DAttachmentContainer clr = new T2DAttachmentContainer() {{
                add(GL46.GL_COLOR_ATTACHMENT0, GL46.GL_RGB16F, GL46.GL_RGB);
                add(GL46.GL_COLOR_ATTACHMENT1, GL46.GL_RGB16F, GL46.GL_RGB);
            }};

            this.getOutGBuffer().createFrameBuffer2DTexture(this.getRenderingResolution(), gBuffer, true, GL46.GL_LINEAR, GL46.GL_NONE, GL46.GL_LESS, GL46.GL_CLAMP_TO_EDGE, null);
            this.getOutSSAOBuffer().createFrameBuffer2DTexture(this.getRenderingResolution(), ssao, false, GL46.GL_LINEAR, GL46.GL_NONE, GL46.GL_LESS, GL46.GL_CLAMP_TO_EDGE, null);
            this.getOutColorBuffer().createFrameBuffer2DTexture(this.getRenderingResolution(), clr, true, GL46.GL_LINEAR, GL46.GL_NONE, GL46.GL_LESS, GL46.GL_CLAMP_TO_EDGE, null);
        }

        @Override
        public void createResources() {
            this.initFBOs();
            final Consumer<JGemsShaderManager> uniformsHandler = (shaderManager) -> {
                final SceneWorld sceneWorld = this.getSceneWorld();
                final ICamera camera = this.getSceneWorld().getCamera();
                final Matrix4f cameraMatrix = JGemsTransformation.INSTANCE.getCameraViewMatrix();
                final Matrix4f projection = JGemsTransformation.INSTANCE.getPerspectiveMatrix();
                final CubeMapTexture cubeMapProgram = sceneWorld.getEnvironment().getSkyBox().getSky2DTexture();

                shaderManager.performUniformNoWarn(new UniformString("camera_pos"), UniformFunctions.VEC3F(camera.getCamPosition()));
                if (cubeMapProgram != null && shaderManager.isUniformExist(new UniformString("ambient_cube_map"))) {
                    shaderManager.performUniformTexture(new UniformString("ambient_cube_map"), cubeMapProgram);
                }
                shaderManager.performUniform(new UniformString("projection_matrix"), UniformFunctions.MAT4F(projection));
                shaderManager.performUniform(new UniformString("view_matrix"), UniformFunctions.MAT4F(cameraMatrix));
            };

            this.directGeometryRenderProcessor = new DirectGeometryRenderProcessor(Pipeline.SCENE, this.getOpenGLRenderer());
            this.indirectGeometryRenderProcessor = new IndirectGeometryRenderProcessor(uniformsHandler, Pipeline.SCENE, this.getOpenGLRenderer());
            this.ssaoRenderProcessor = new SSAORenderProcessor(this.getOpenGLRenderer(), this.getOutGBuffer(), JGemsResourceManager.globalShaderAssets.world_ssao);
            this.rawColorRenderProcessor = new DeferredColorRenderProcessor(this.getOpenGLRenderer(), this.getOutGBuffer(), this.getOutSSAOBuffer(), JGemsResourceManager.globalShaderAssets.world_deferred);

            this.getSSAORenderProcessor().createResources();
            this.getDirectGeometryRenderProcessor().createResources();
            this.getIndirectGeometryRenderProcessor().createResources();
            this.getRawColorRenderProcessor().createResources();
        }

        @Override
        public void destroyResources() {
            this.getSSAORenderProcessor().destroyResources();
            this.getDirectGeometryRenderProcessor().destroyResources();
            this.getIndirectGeometryRenderProcessor().destroyResources();
            this.getRawColorRenderProcessor().destroyResources();

            if (this.getOutGBuffer() != null) {
                this.getOutGBuffer().clearFBO();
            }
            if (this.getOutSSAOBuffer() != null) {
                this.getOutSSAOBuffer().clearFBO();
            }
            if (this.getOutColorBuffer() != null) {
                this.getOutColorBuffer().clearFBO();
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

        public DeferredColorRenderProcessor getRawColorRenderProcessor() {
            return this.rawColorRenderProcessor;
        }

        public SSAORenderProcessor getSSAORenderProcessor() {
            return this.ssaoRenderProcessor;
        }

        public IndirectGeometryRenderProcessor getIndirectGeometryRenderProcessor() {
            return this.indirectGeometryRenderProcessor;
        }

        public DirectGeometryRenderProcessor getDirectGeometryRenderProcessor() {
            return this.directGeometryRenderProcessor;
        }
    }
}
