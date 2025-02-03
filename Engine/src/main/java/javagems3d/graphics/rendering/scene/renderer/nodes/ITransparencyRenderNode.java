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
import javagems3d.graphics.screen.ticking.FrameTicking;
import javagems3d.graphics.transformation.JGemsTransformManager;
import javagems3d.graphics.world.SceneWorld;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import javagems3d.system.resources.assets.shaders.uniform.UniformString;
import javagems3d.system.resources.assets.texturing.CubeMapTexture;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL46;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.function.Consumer;

public interface ITransparencyRenderNode extends IRenderNode {
    FBOTexture2DProgram getOutColorBuffer();
    FBOTexture2DProgram getInColorBuffer();

    void setIndirectDeferredRenderingObjects(@NotNull Collection<SceneObject> indirectDeferredRenderingObjects);
    void setDirectDeferredRenderingObjects(@NotNull Collection<SceneObject> directDeferredRenderingObjects);

    Collection<SceneObject> getIndirectDeferredRenderingObjects();
    Collection<SceneObject> getDirectDeferredRenderingObjects();

    final class Default extends IRenderNode.Template implements ITransparencyRenderNode {
        private FBOTexture2DProgram outColor;
        private final FBOTexture2DProgram inColor;

        private Collection<SceneObject> indirectDeferredRenderingObjects;
        private Collection<SceneObject> directDeferredRenderingObjects;

        private DirectGeometryRenderProcessor directGeometryRenderProcessor;
        private IndirectGeometryRenderProcessor indirectGeometryRenderProcessor;

        public Default(@NotNull FBOTexture2DProgram inColor, OpenGLRenderer openGLRenderer) {
            super(openGLRenderer);
            this.indirectDeferredRenderingObjects = new HashSet<>();
            this.directDeferredRenderingObjects = new HashSet<>();
            this.inColor = inColor;
        }

        @Override
        public void onRender(FrameTicking frameTicking) {
            this.getInColorBuffer().copyFBOtoFBODepth(this.getOutColorBuffer().getFrameBufferId(), this.getRenderingResolution());

            GL46.glDepthMask(false);
            GL46.glEnable(GL46.GL_BLEND);
            GL46.glBlendFunci(0, GL46.GL_ONE, GL46.GL_ONE);
            GL46.glBlendFunci(1, GL46.GL_ZERO, GL46.GL_ONE_MINUS_SRC_COLOR);
            GL46.glBlendFunci(2, GL46.GL_ONE, GL46.GL_ONE);
            GL46.glBlendEquation(GL46.GL_FUNC_ADD);
            this.getOutColorBuffer().bindFBO();
            GL46.glClearBufferfv(GL46.GL_COLOR, 0, new float[]{0.0f, 0.0f, 0.0f, 0.0f});
            GL46.glClearBufferfv(GL46.GL_COLOR, 1, new float[]{1.0f, 1.0f, 1.0f, 1.0f});
            GL46.glClearBufferfv(GL46.GL_COLOR, 2, new float[]{0.0f, 0.0f, 0.0f, 0.0f});

          // this.getIndirectGeometryRenderProcessor().setIndirectMeshObjects(this.getIndirectDeferredRenderingObjects());
          // this.getIndirectGeometryRenderProcessor().runProcessorRendering(frameTicking);
          // this.getDirectGeometryRenderProcessor().setDirectMeshObjects(this.getDirectDeferredRenderingObjects());
          // this.getDirectGeometryRenderProcessor().runProcessorRendering(frameTicking);

            this.getOutColorBuffer().unBindFBO();
            GL46.glDisable(GL46.GL_BLEND);
            GL46.glDepthMask(true);
        }

        @Override
        public void createResources() {
            this.outColor = new FBOTexture2DProgram(true);
            T2DAttachmentContainer clr = new T2DAttachmentContainer() {{
                add(GL46.GL_COLOR_ATTACHMENT0, GL46.GL_RGBA16F, GL46.GL_RGBA);
                add(GL46.GL_COLOR_ATTACHMENT1, GL46.GL_R8, GL46.GL_RED);
                add(GL46.GL_COLOR_ATTACHMENT2, GL46.GL_RGBA16F, GL46.GL_RGBA);
            }};
            this.getOutColorBuffer().createFrameBuffer2DTexture(this.getRenderingResolution(), clr, false, GL46.GL_LINEAR, GL46.GL_NONE, GL46.GL_LESS, GL46.GL_CLAMP_TO_EDGE, null);

            final Consumer<JGemsShaderManager> uniformsHandler = (shaderManager) -> {
                final SceneWorld sceneWorld = this.getSceneWorld();
                final ICamera camera = this.getSceneWorld().getCamera();
                final Matrix4f cameraMatrix = JGemsTransformManager.INSTANCE.getCameraViewMatrix();
                final Matrix4f projection = JGemsTransformManager.INSTANCE.getPerspectiveMatrix();
                final CubeMapTexture cubeMapProgram = sceneWorld.getEnvironment().getSkyBox().getSky2DTexture();

                shaderManager.performUniformNoWarn(new UniformString("camera_pos"), UniformFunctions.VEC3F(camera.getCamPosition()));
                if (cubeMapProgram != null && shaderManager.isUniformExist(new UniformString("ambient_cube_map"))) {
                    shaderManager.performUniformTexture(new UniformString("ambient_cube_map"), cubeMapProgram);
                }
                shaderManager.performUniform(new UniformString("projection_matrix"), UniformFunctions.MAT4F(projection));
                shaderManager.performUniform(new UniformString("view_matrix"), UniformFunctions.MAT4F(cameraMatrix));
            };

            this.directGeometryRenderProcessor = new DirectGeometryRenderProcessor(Pipeline.TRANSPARENCY, this.getOpenGLRenderer());
            this.indirectGeometryRenderProcessor = new IndirectGeometryRenderProcessor(uniformsHandler, Pipeline.TRANSPARENCY, this.getOpenGLRenderer());
            this.getDirectGeometryRenderProcessor().createResources();
            this.getIndirectGeometryRenderProcessor().createResources();
        }

        @Override
        public void destroyResources() {
            this.getDirectGeometryRenderProcessor().destroyResources();
            this.getIndirectGeometryRenderProcessor().destroyResources();
            if (this.getOutColorBuffer() != null) {
                this.getOutColorBuffer().clearFBO();
            }
        }

        public DirectGeometryRenderProcessor getDirectGeometryRenderProcessor() {
            return this.directGeometryRenderProcessor;
        }

        public IndirectGeometryRenderProcessor getIndirectGeometryRenderProcessor() {
            return this.indirectGeometryRenderProcessor;
        }

        @Override
        public FBOTexture2DProgram getOutColorBuffer() {
            return this.outColor;
        }

        @Override
        public FBOTexture2DProgram getInColorBuffer() {
            return this.inColor;
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
    }
}
