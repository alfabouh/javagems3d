package workbench.graphics.scene.nodes;

import javagems3d.graphics.camera.base.ICamera;
import javagems3d.graphics.objects.IRendered;
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
import javagems3d.help.JGemsShadersHelper;
import javagems3d.system.resources.assets.materials.Material;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import javagems3d.system.resources.assets.shaders.uniform.UniformString;
import javagems3d.system.resources.assets.texturing.Color4Texture;
import javagems3d.system.resources.assets.texturing.CubeMapTexture;
import javagems3d.system.service.collections.Pair;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL46;
import workbench.graphics.scene.nodes.templates.WIDeferredRenderNode;
import workbench.graphics.scene.processors.WDeferredColorRenderProcessor;
import workbench.graphics.scene.world.WBenchWorld;
import workbench.graphics.screen.WBenchScreen;
import workbench.resources.WBenchResourceManager;

import java.util.Collection;
import java.util.HashSet;
import java.util.function.Consumer;

public final class WDeferredRenderNode extends IRenderNode.Template implements WIDeferredRenderNode {
    private final FBOTexture2DProgram startColorFBO;
    private FBOTexture2DProgram gBuffer;

    private DirectGeometryRenderProcessor directGeometryRenderProcessor;
    private IndirectGeometryRenderProcessor indirectGeometryRenderProcessor;
    private WDeferredColorRenderProcessor rawColorRenderProcessor;

    private Collection<SceneObject> indirectDeferredRenderingObjects;
    private Collection<SceneObject> directDeferredRenderingObjects;

    public WDeferredRenderNode(@NotNull FBOTexture2DProgram startColorFbo, OpenGLRenderer openGLRenderer) {
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
    public void onRender(FrameTicking frameTicking) {
        WBenchScreen.clearColor();
        this.getOutGBuffer().bindFBO();
        GL46.glClear(GL46.GL_COLOR_BUFFER_BIT | GL46.GL_DEPTH_BUFFER_BIT);
        this.getIndirectGeometryRenderProcessor().setIndirectMeshObjects(this.getIndirectDeferredRenderingObjects());
        this.getIndirectGeometryRenderProcessor().runProcessorRendering(frameTicking);
        this.getDirectGeometryRenderProcessor().setDirectMeshObjects(this.getDirectDeferredRenderingObjects());
        this.getDirectGeometryRenderProcessor().runProcessorRendering(frameTicking);
        this.getOutGBuffer().unBindFBO();

        this.getOutColorBuffer().bindFBO();
        GL46.glClear(GL46.GL_COLOR_BUFFER_BIT | GL46.GL_DEPTH_BUFFER_BIT);
        this.getRawColorRenderProcessor().runProcessorRendering(frameTicking);
        this.getOutColorBuffer().unBindFBO();

        this.getOutGBuffer().copyFBOtoFBODepth(this.getOutColorBuffer().getFrameBufferId(), this.getRenderingResolution());
    }

    public void initFBOs() {
        this.gBuffer = new FBOTexture2DProgram(true);

        T2DAttachmentContainer gBuffer = new T2DAttachmentContainer() {{
            add(GL46.GL_COLOR_ATTACHMENT0, GL46.GL_RGB32F, GL46.GL_RGB);
            add(GL46.GL_COLOR_ATTACHMENT1, GL46.GL_RGB32F, GL46.GL_RGB);
            add(GL46.GL_COLOR_ATTACHMENT2, GL46.GL_RGBA, GL46.GL_RGBA);
            add(GL46.GL_COLOR_ATTACHMENT3, GL46.GL_RGB, GL46.GL_RGB);
            add(GL46.GL_COLOR_ATTACHMENT4, GL46.GL_RGB, GL46.GL_RGB);
        }};
        T2DAttachmentContainer clr = new T2DAttachmentContainer() {{
            add(GL46.GL_COLOR_ATTACHMENT0, GL46.GL_RGBA16F, GL46.GL_RGBA);
            add(GL46.GL_COLOR_ATTACHMENT1, GL46.GL_RGBA16F, GL46.GL_RGBA);
        }};

        this.getOutGBuffer().createFrameBuffer2DTexture(this.getRenderingResolution(), gBuffer, true, GL46.GL_LINEAR, GL46.GL_NONE, GL46.GL_LESS, GL46.GL_CLAMP_TO_EDGE, null);
        this.getOutColorBuffer().createFrameBuffer2DTexture(this.getRenderingResolution(), clr, true, GL46.GL_LINEAR, GL46.GL_NONE, GL46.GL_LESS, GL46.GL_CLAMP_TO_EDGE, null);
    }

    public static Consumer<Pair<JGemsShaderManager, IRendered>> getDefaultConsumerForDirectObjects(WBenchWorld wBenchWorld) {
        return (pair) -> {
            JGemsShadersHelper.performModelMaterialOnShader(wBenchWorld.getEnvironment(), pair.getFirst(), new Material(new Color4Texture(1.0f, 1.0f, 1.0f)));
        };
    }

    @Override
    public void createResources() {
        this.initFBOs();
        final WBenchWorld wBenchWorld = (WBenchWorld) this.getWBenchWorld();
        final Consumer<JGemsShaderManager> uniformsHandlerI = (shaderManager) -> {
            final ICamera camera = this.getOpenGLRenderer().getCamera();
            final Matrix4f cameraMatrix = JGemsTransformManager.INSTANCE.getCameraViewMatrix();
            final Matrix4f projection = JGemsTransformManager.INSTANCE.getPerspectiveMatrix();
            final CubeMapTexture cubeMapProgram = (CubeMapTexture) wBenchWorld.getEnvironment().getSkyBox().getTexture();

            shaderManager.performUniformNoWarn(new UniformString("camera_pos"), UniformFunctions.VEC3F(camera.getCamPosition()));
            if (cubeMapProgram != null && shaderManager.isUniformExist(new UniformString("ambient_cube_map"))) {
                shaderManager.performUniformTexture(new UniformString("ambient_cube_map"), cubeMapProgram);
            }
            shaderManager.performUniform(new UniformString("projection_matrix"), UniformFunctions.MAT4F(projection));
            shaderManager.performUniform(new UniformString("view_matrix"), UniformFunctions.MAT4F(cameraMatrix));
            shaderManager.performUniformTexture(new UniformString("animationsMatrix"), WBenchResourceManager.getAnimationsTextureBuffer());
        };
        final Consumer<Pair<JGemsShaderManager, IRendered>> uniformsHandlerD = WDeferredRenderNode.getDefaultConsumerForDirectObjects(wBenchWorld);

        this.directGeometryRenderProcessor = new DirectGeometryRenderProcessor(uniformsHandlerD, Pipeline.SCENE, this.getOpenGLRenderer());
        this.indirectGeometryRenderProcessor = new IndirectGeometryRenderProcessor(uniformsHandlerI, WBenchResourceManager.localShaderAssets.IndirectBufferData, WBenchResourceManager.localShaderAssets.PropertiesData, Pipeline.SCENE, this.getOpenGLRenderer());
        this.rawColorRenderProcessor = new WDeferredColorRenderProcessor(this.getOpenGLRenderer(), this.getOutGBuffer(), WBenchResourceManager.localShaderAssets.world_deferred);

        this.getDirectGeometryRenderProcessor().createResources();
        this.getIndirectGeometryRenderProcessor().createResources();
        this.getRawColorRenderProcessor().createResources();
    }

    @Override
    public void destroyResources() {
        this.getDirectGeometryRenderProcessor().destroyResources();
        this.getIndirectGeometryRenderProcessor().destroyResources();
        this.getRawColorRenderProcessor().destroyResources();

        if (this.getOutGBuffer() != null) {
            this.getOutGBuffer().clearFBO();
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

    @Override
    public Collection<SceneObject> getRejectedIndirectDeferredRenderingObjects() {
        return this.getIndirectGeometryRenderProcessor().getRejected();
    }

    @Override
    public Collection<SceneObject> getRejectedDirectDeferredRenderingObjects() {
        return this.getDirectGeometryRenderProcessor().getRejected();
    }

    public WDeferredColorRenderProcessor getRawColorRenderProcessor() {
        return this.rawColorRenderProcessor;
    }

    public IndirectGeometryRenderProcessor getIndirectGeometryRenderProcessor() {
        return this.indirectGeometryRenderProcessor;
    }

    public DirectGeometryRenderProcessor getDirectGeometryRenderProcessor() {
        return this.directGeometryRenderProcessor;
    }
}
