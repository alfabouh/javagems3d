package workbench.graphics.scene.nodes;

import javagems3d.graphics.camera.base.ICamera;
import javagems3d.graphics.objects.IRendered;
import javagems3d.graphics.objects.SceneObject;
import javagems3d.graphics.objects.rendering.pipeline.enums.Pipeline;
import javagems3d.graphics.rendering.programs.fbo.FBOTexture2DProgram;
import javagems3d.graphics.rendering.programs.fbo.attachments.T2DAttachmentContainer;
import javagems3d.graphics.rendering.programs.shaders.unifrom.UniformFunctions;
import javagems3d.graphics.rendering.programs.textures.base.ICubeMapProgram;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.rendering.scene.renderer.nodes.base.IRenderNode;
import javagems3d.graphics.rendering.scene.renderer.processors.geometry.DirectGeometryRenderProcessor;
import javagems3d.graphics.rendering.scene.renderer.processors.geometry.IndirectGeometryRenderProcessor;
import javagems3d.graphics.screen.ticking.FrameTicking;
import javagems3d.graphics.transformation.JGemsTransformManager;
import javagems3d.help.JGemsShadersHelper;
import javagems3d.system.resources.assets.materials.Material;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import javagems3d.system.resources.assets.shaders.uniform.UniformString;
import javagems3d.system.resources.assets.texturing.Color4Texture;
import javagems3d.system.service.collections.Pair;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL46;
import workbench.graphics.scene.nodes.templates.WITransparencyRenderNode;
import workbench.graphics.scene.world.WBenchWorld;
import workbench.resources.WBenchResourceManager;

import java.util.Collection;
import java.util.HashSet;
import java.util.function.Consumer;

public final class WTransparencyRenderNode extends IRenderNode.Template implements WITransparencyRenderNode {
    private final FBOTexture2DProgram outColor;
    private final FBOTexture2DProgram inColor;

    private Collection<SceneObject> indirectDeferredRenderingObjects;
    private Collection<SceneObject> directDeferredRenderingObjects;

    private DirectGeometryRenderProcessor directGeometryRenderProcessor;
    private IndirectGeometryRenderProcessor indirectGeometryRenderProcessor;

    public WTransparencyRenderNode(@NotNull FBOTexture2DProgram inColor, OpenGLRenderer openGLRenderer) {
        super(openGLRenderer);
        this.indirectDeferredRenderingObjects = new HashSet<>();
        this.directDeferredRenderingObjects = new HashSet<>();
        this.inColor = inColor;
        this.outColor = new FBOTexture2DProgram(true);
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

        this.getIndirectGeometryRenderProcessor().setIndirectMeshObjects(this.getIndirectDeferredRenderingObjects());
        this.getIndirectGeometryRenderProcessor().runProcessorRendering(frameTicking);
        this.getDirectGeometryRenderProcessor().setDirectMeshObjects(this.getDirectDeferredRenderingObjects());
        this.getDirectGeometryRenderProcessor().runProcessorRendering(frameTicking);

        this.getOutColorBuffer().unBindFBO();
        GL46.glDisable(GL46.GL_BLEND);
        GL46.glDepthMask(true);
    }

    @Override
    public void createResources() {
        T2DAttachmentContainer clr = new T2DAttachmentContainer() {{
            add(GL46.GL_COLOR_ATTACHMENT0, GL46.GL_RGBA16F, GL46.GL_RGBA);
            add(GL46.GL_COLOR_ATTACHMENT1, GL46.GL_R8, GL46.GL_RED);
            add(GL46.GL_COLOR_ATTACHMENT2, GL46.GL_RGBA16F, GL46.GL_RGBA);
        }};
        this.getOutColorBuffer().createFrameBuffer2DTexture(this.getRenderingResolution(), clr, true, GL46.GL_LINEAR, GL46.GL_NONE, GL46.GL_LESS, GL46.GL_CLAMP_TO_EDGE, null);

        final WBenchWorld wBenchWorld = (WBenchWorld) this.getWBenchWorld();
        final Consumer<JGemsShaderManager> uniformsHandlerI = (shaderManager) -> {
            final ICamera camera = this.getOpenGLRenderer().getCamera();
            final Matrix4f cameraMatrix = JGemsTransformManager.INSTANCE.getCameraViewMatrix();
            final Matrix4f projection = JGemsTransformManager.INSTANCE.getPerspectiveMatrix();
            final ICubeMapProgram cubeMapProgram = wBenchWorld.getEnvironment().getSkyBox().getTexture();

            shaderManager.performUniformNoWarn(new UniformString("camera_pos"), UniformFunctions.VEC3F(camera.getCamPosition()));
            if (cubeMapProgram != null && shaderManager.isUniformExist(new UniformString("ambient_cube_map"))) {
                shaderManager.performUniformTexture(new UniformString("ambient_cube_map"), cubeMapProgram);
            }
            shaderManager.performUniform(new UniformString("projection_matrix"), UniformFunctions.MAT4F(projection));
            shaderManager.performUniform(new UniformString("view_matrix"), UniformFunctions.MAT4F(cameraMatrix));
            JGemsShadersHelper.performShadowsInfo(wBenchWorld.getEnvironment(), shaderManager);
        };
        final Consumer<Pair<JGemsShaderManager, IRendered>> uniformsHandlerD = WDeferredRenderNode.getDefaultConsumerForDirectObjects(wBenchWorld);

        this.directGeometryRenderProcessor = new DirectGeometryRenderProcessor(uniformsHandlerD, Pipeline.TRANSPARENCY, this.getOpenGLRenderer());
        this.indirectGeometryRenderProcessor = new IndirectGeometryRenderProcessor(uniformsHandlerI, WBenchResourceManager.localShaderAssets.IndirectBufferData, WBenchResourceManager.localShaderAssets.PropertiesData, Pipeline.TRANSPARENCY, this.getOpenGLRenderer());
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
