package javagems3d.graphics.rendering.scene.renderer.nodes.templates.abstractions;

import javagems3d.graphics.camera.base.ICamera;
import javagems3d.graphics.objects.IRendered;
import javagems3d.graphics.objects.SceneObject;
import javagems3d.graphics.objects.rendering.pipeline.enums.Pipeline;
import javagems3d.graphics.rendering.programs.fbo.FBOTexture2DProgram;
import javagems3d.graphics.rendering.programs.fbo.attachments.T2DAttachmentContainer;
import javagems3d.graphics.rendering.programs.shaders.unifrom.UniformFunctions;
import javagems3d.graphics.rendering.programs.textures.base.ICubeMapProgram;
import javagems3d.graphics.rendering.programs.textures.base.ITexture2DProgram;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.rendering.scene.renderer.nodes.base.IRenderNode;
import javagems3d.graphics.rendering.scene.renderer.nodes.templates.interfaces.IDeferredRenderNode;
import javagems3d.graphics.rendering.scene.renderer.processors.geometry.DirectGeometryRenderProcessor;
import javagems3d.graphics.rendering.scene.renderer.processors.geometry.IndirectGeometryRenderProcessor;
import javagems3d.graphics.rendering.scene.renderer.processors.post.DeferredColorRenderProcessor;
import javagems3d.graphics.rendering.scene.renderer.processors.post.SSAORenderProcessor;
import javagems3d.graphics.screen.ticking.FrameTicking;
import javagems3d.graphics.transformation.JGemsTransformManager;
import javagems3d.graphics.world.IRenderWorld;
import javagems3d.help.JGemsHelper;
import javagems3d.system.resources.assets.materials.Material;
import javagems3d.system.resources.assets.shaders.buffers.ShaderStorageBufferObject;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import javagems3d.system.resources.assets.shaders.uniform.DefaultUniformDefinitions;
import javagems3d.system.resources.assets.shaders.uniform.UniformString;
import javagems3d.system.resources.assets.texturing.colors.Color4Texture;
import javagems3d.system.service.collections.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL46;

import java.util.Collection;
import java.util.HashSet;
import java.util.function.Consumer;

public abstract class DeferredRenderNode extends IRenderNode.Template implements IDeferredRenderNode {
    private final FBOTexture2DProgram startColorFBO;
    private FBOTexture2DProgram gBuffer;
    private FBOTexture2DProgram ssaoBuffer;

    private DirectGeometryRenderProcessor directGeometryRenderProcessor;
    private IndirectGeometryRenderProcessor indirectGeometryRenderProcessor;
    private SSAORenderProcessor ssaoRenderProcessor;
    private DeferredColorRenderProcessor rawColorRenderProcessor;

    private Collection<SceneObject> indirectDeferredRenderingObjects;
    private Collection<SceneObject> directDeferredRenderingObjects;

    public DeferredRenderNode(@NotNull FBOTexture2DProgram startColorFbo, OpenGLRenderer openGLRenderer) {
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
    public @Nullable FBOTexture2DProgram getOutSSAOBuffer() {
        return this.ssaoBuffer;
    }

    @Override
    public void onRender(FrameTicking frameTicking) {
       //for (SceneObject sceneObject : this.getIndirectDeferredRenderingObjects()) {
       //    CullingAABB cullingAABB = sceneObject.pickAABBDataFromMesh();
       //    if (cullingAABB != null) {
       //        JGemsOpenGLRenderer.DebugLinesDrawer().addRequest(DebugLinesDrawer.BoxRequest(cullingAABB.getAabbMin(), cullingAABB.getAabbMax(), new Vector3f(1.0f, 0.0f, 0.0f), DebugLinesDrawer.noDepth(), DebugLinesDrawer.Depth()));
       //    }
       //}

        this.getOutGBuffer().bindFBO();
        GL46.glClear(GL46.GL_COLOR_BUFFER_BIT | GL46.GL_DEPTH_BUFFER_BIT);
        this.getIndirectGeometryRenderProcessor().setIndirectMeshObjects(this.getIndirectDeferredRenderingObjects());
        this.getIndirectGeometryRenderProcessor().runProcessorRendering(frameTicking);
        this.getDirectGeometryRenderProcessor().setDirectMeshObjects(this.getDirectDeferredRenderingObjects());
        this.getDirectGeometryRenderProcessor().runProcessorRendering(frameTicking);
        this.getOutGBuffer().unBindFBO();

        if (this.getOutSSAOBuffer() != null) {
            this.getOutSSAOBuffer().bindFBO();
            this.getSSAORenderProcessor().runProcessorRendering(frameTicking);
            this.getOutSSAOBuffer().unBindFBO();
        }

        this.getOutColorBuffer().bindFBO();
        GL46.glClear(GL46.GL_COLOR_BUFFER_BIT | GL46.GL_DEPTH_BUFFER_BIT);
        this.getRawColorRenderProcessor().runProcessorRendering(frameTicking);
        this.getOutColorBuffer().unBindFBO();

        this.getOutGBuffer().copyFBOtoFBODepth(this.getOutColorBuffer().getFrameBufferId(), this.getRenderingResolution());
    }

    public void initFBOs() {
        this.gBuffer = new FBOTexture2DProgram(true, false);
        if (this.useSsao()) {
            this.ssaoBuffer = new FBOTexture2DProgram(true, false);
        }

        T2DAttachmentContainer gBuffer = new T2DAttachmentContainer() {{
            add(GL46.GL_COLOR_ATTACHMENT0, GL46.GL_RGB32F, GL46.GL_RGB);
            add(GL46.GL_COLOR_ATTACHMENT1, GL46.GL_RGB32F, GL46.GL_RGB);
            add(GL46.GL_COLOR_ATTACHMENT2, GL46.GL_RGBA, GL46.GL_RGBA);
            add(GL46.GL_COLOR_ATTACHMENT3, GL46.GL_RGB, GL46.GL_RGB);
            add(GL46.GL_COLOR_ATTACHMENT4, GL46.GL_RG, GL46.GL_RG);
        }};
        T2DAttachmentContainer clr = new T2DAttachmentContainer() {{
            add(GL46.GL_COLOR_ATTACHMENT0, GL46.GL_RGB16F, GL46.GL_RGB);
            add(GL46.GL_COLOR_ATTACHMENT1, GL46.GL_RGB16F, GL46.GL_RGB);
        }};
        this.getOutGBuffer().createFrameBuffer2DTexture(this.getRenderingResolution(), gBuffer, true, GL46.GL_LINEAR, GL46.GL_NONE, GL46.GL_LESS, GL46.GL_CLAMP_TO_EDGE, null);

        if (this.getOutSSAOBuffer() != null) {
            T2DAttachmentContainer ssao = new T2DAttachmentContainer() {{
                add(GL46.GL_COLOR_ATTACHMENT0, GL46.GL_R16F, GL46.GL_RED);
            }};
            this.getOutSSAOBuffer().createFrameBuffer2DTexture(this.getRenderingResolution(), ssao, false, GL46.GL_LINEAR, GL46.GL_NONE, GL46.GL_LESS, GL46.GL_CLAMP_TO_EDGE, null);
        }
        this.getOutColorBuffer().createFrameBuffer2DTexture(this.getRenderingResolution(), clr, true, GL46.GL_LINEAR, GL46.GL_NONE, GL46.GL_LESS, GL46.GL_CLAMP_TO_EDGE, null);
    }

    public static Consumer<Pair<JGemsShaderManager, IRendered>> getDefaultConsumerForDirectObjects(IRenderWorld renderWorld) {
        return (pair) -> {
            JGemsHelper.render().performModelMaterialOnShader(renderWorld.getEnvironment(), pair.first(), new Material(new Color4Texture(1.0f, 1.0f, 1.0f)));
        };
    }

    @Override
    public void createResources() {
        this.initFBOs();
        final Consumer<JGemsShaderManager> uniformsHandlerI = (shaderManager) -> {
            final ICamera camera = this.getOpenGLRenderer().getCamera();
            final Matrix4f cameraMatrix = JGemsTransformManager.INSTANCE.getCameraViewMatrix();
            final Matrix4f projection = JGemsTransformManager.INSTANCE.getPerspectiveMatrix();
            final ICubeMapProgram cubeMapProgram = this.getWorld().getEnvironment().getSkyBox().getTexture();
            shaderManager.performUniformNoWarn(new UniformString(DefaultUniformDefinitions.CAMERA_POS), UniformFunctions.VEC3F(camera.getCamPosition()));
            if (cubeMapProgram != null && shaderManager.isUniformExist(new UniformString(DefaultUniformDefinitions.AMBIENT_CUBEMAP))) {
                shaderManager.performUniformTextureBindless(new UniformString(DefaultUniformDefinitions.AMBIENT_CUBEMAP), cubeMapProgram);
            }
            shaderManager.performUniform(new UniformString(DefaultUniformDefinitions.PROJECTION_MATRIX), UniformFunctions.MAT4F(projection));
            shaderManager.performUniform(new UniformString(DefaultUniformDefinitions.VIEW_MATRIX), UniformFunctions.MAT4F(cameraMatrix));
            shaderManager.performUniformTexture(new UniformString(DefaultUniformDefinitions.ANIMATIONS_MATRIX), this.getAnimationsTexture());
        };

        final Consumer<Pair<JGemsShaderManager, IRendered>> uniformsHandlerD = DeferredRenderNode.getDefaultConsumerForDirectObjects(this.getWorld());
        this.directGeometryRenderProcessor = new DirectGeometryRenderProcessor(uniformsHandlerD, Pipeline.SCENE, this.getOpenGLRenderer());
        this.indirectGeometryRenderProcessor = new IndirectGeometryRenderProcessor(uniformsHandlerI, this.getIndirectBufferData(), this.getPropertiesData(), Pipeline.SCENE, this.getOpenGLRenderer());
        if (this.getOutSSAOBuffer() != null && this.getSsaoShader() != null) {
            this.ssaoRenderProcessor = new SSAORenderProcessor(this.getOpenGLRenderer(), this.getOutGBuffer(), this.getSsaoShader());
        }
        this.rawColorRenderProcessor = new DeferredColorRenderProcessor(this.getOpenGLRenderer(), this.getOutGBuffer(), this.getOutSSAOBuffer(), getDeferredRendererShader());

        if (this.getSSAORenderProcessor() != null) {
            this.getSSAORenderProcessor().createResources();
        }
        this.getDirectGeometryRenderProcessor().createResources();
        this.getIndirectGeometryRenderProcessor().createResources();
        this.getRawColorRenderProcessor().createResources();
    }

    public abstract boolean useSsao();
    public abstract @NotNull ITexture2DProgram getAnimationsTexture();
    public abstract @NotNull ShaderStorageBufferObject getIndirectBufferData();
    public abstract @NotNull ShaderStorageBufferObject getPropertiesData();
    public abstract @Nullable JGemsShaderManager getSsaoShader();
    public abstract @NotNull JGemsShaderManager getDeferredRendererShader();

    @Override
    public void destroyResources() {
        if (this.getSSAORenderProcessor() != null) {
            this.getSSAORenderProcessor().destroyResources();
        }
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

    @Override
    public Collection<SceneObject> getRejectedIndirectDeferredRenderingObjects() {
        return this.getIndirectGeometryRenderProcessor().getRejected();
    }

    @Override
    public Collection<SceneObject> getRejectedDirectDeferredRenderingObjects() {
        return this.getDirectGeometryRenderProcessor().getRejected();
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

    /*
    public void MillionCubesTest() {
        final Consumer<JGemsShaderManager> uniformsHandler = (shaderManager) -> {
            final SceneWorld sceneWorld = (SceneWorld) this.getWorld();
            final ICamera camera = this.getOpenGLRenderer().getCamera();
            final Matrix4f cameraMatrix = JGemsTransformManager.INSTANCE.getCameraViewMatrix();
            final Matrix4f projection = JGemsTransformManager.INSTANCE.getPerspectiveMatrix();
            final ICubeMapProgram cubeMapProgram = sceneWorld.getEnvironment().getSkyBox().getTexture();

            shaderManager.performUniformNoWarn(new UniformString(DefaultUniformDefinitions.CAMERA_POS), UniformFunctions.VEC3F(camera.getCamPosition()));
            if (cubeMapProgram != null && shaderManager.isUniformExist(new UniformString(DefaultUniformDefinitions.AMBIENT_CUBEMAP))) {
                shaderManager.performUniformTextureBindless(new UniformString(DefaultUniformDefinitions.AMBIENT_CUBEMAP), cubeMapProgram);
            }
            shaderManager.performUniform(new UniformString(DefaultUniformDefinitions.PROJECTION_MATRIX), UniformFunctions.MAT4F(projection));
            shaderManager.performUniform(new UniformString(DefaultUniformDefinitions.VIEW_MATRIX), UniformFunctions.MAT4F(cameraMatrix));
        };

        IndirectBufferProgram renderBuffer = this.getOpenGLRenderer().getSceneIndirectBuffer();
        BaseIndirectCommandsProgram baseIndirectCommandProgram1 = new BaseIndirectCommandsProgram(renderBuffer);
        baseIndirectCommandProgram1.createBuffer();
        //baseIndirectCommandProgram1.buildCommands(null, null, JGemsResourceManager.globalModelAssets.grassCube, 1_000_000);
        GroupedIndirectRenderer.IRenderingFunction renderingFunction = IndirectRenderFabric.DEFAULT_FUNC;
        renderingFunction.func(JGemsResourceManager.globalShaderAssets.world_gbuffer_indirect, baseIndirectCommandProgram1, renderBuffer, ArbitraryArguments.pass(uniformsHandler));
        baseIndirectCommandProgram1.destroyBuffer();
    }
     */
}
