package ru.jgems3d.engine.graphics.opengl.rendering.scene;

import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL43;
import org.lwjgl.opengl.GL45;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import ru.jgems3d.engine.JGems3D;
import ru.jgems3d.engine.JGemsHelper;
import ru.jgems3d.engine.graphics.opengl.dear_imgui.DIMGuiRenderJGems;
import ru.jgems3d.engine.graphics.opengl.environment.Environment;
import ru.jgems3d.engine.graphics.opengl.environment.shadow.ShadowScene;
import ru.jgems3d.engine.graphics.opengl.rendering.JGemsSceneGlobalConstants;
import ru.jgems3d.engine.graphics.opengl.rendering.JGemsSceneUtils;
import ru.jgems3d.engine.graphics.opengl.rendering.debug.GlobalRenderDebugConstants;
import ru.jgems3d.engine.graphics.opengl.rendering.items.IModeledSceneObject;
import ru.jgems3d.engine.graphics.opengl.rendering.programs.fbo.FBOTexture2DProgram;
import ru.jgems3d.engine.graphics.opengl.rendering.programs.fbo.attachments.T2DAttachmentContainer;
import ru.jgems3d.engine.graphics.opengl.rendering.programs.textures.TextureProgram;
import ru.jgems3d.engine.graphics.opengl.rendering.scene.base.ISceneRenderer;
import ru.jgems3d.engine.graphics.opengl.rendering.scene.render_base.SceneData;
import ru.jgems3d.engine.graphics.opengl.rendering.scene.render_base.SceneRenderBase;
import ru.jgems3d.engine.graphics.opengl.rendering.scene.render_base.groups.deferred.WorldDeferredRender;
import ru.jgems3d.engine.graphics.opengl.rendering.scene.render_base.groups.forward.*;
import ru.jgems3d.engine.graphics.opengl.rendering.scene.render_base.groups.transparent.LiquidsRender;
import ru.jgems3d.engine.graphics.opengl.rendering.scene.render_base.groups.transparent.ParticlesRender;
import ru.jgems3d.engine.graphics.opengl.rendering.scene.render_base.groups.transparent.WorldTransparentRender;
import ru.jgems3d.engine.graphics.opengl.rendering.scene.tick.FrameTicking;
import ru.jgems3d.engine.graphics.opengl.screen.window.Window;
import ru.jgems3d.engine.system.resources.assets.models.Model;
import ru.jgems3d.engine.system.resources.assets.models.basic.MeshHelper;
import ru.jgems3d.engine.system.resources.assets.models.formats.Format2D;
import ru.jgems3d.engine.system.resources.assets.shaders.manager.JGemsShaderManager;
import ru.jgems3d.engine.system.resources.manager.JGemsResourceManager;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

public class JGemsOpenGLRendererNew implements ISceneRenderer {
    private boolean wantToTakeScreenshot;

    private final SceneData sceneData;
    private final SceneRenderBaseContainer sceneRenderBaseContainer;
    private final DIMGuiRenderJGems dearImGuiRender;
    private final Set<FBOTexture2DProgram> fboSet;
    private final ShadowScene shadowScene;

    private GuiRender guiRender;
    private InventoryRender inventoryRender;
    private WorldTransparentRender worldTransparentRender;

    private FBOTexture2DProgram bloomBlurredBuffer;
    private FBOTexture2DProgram forwardAndDeferredScenesBuffer;
    private FBOTexture2DProgram gBuffer;
    private FBOTexture2DProgram ssaoBuffer;
    private FBOTexture2DProgram sceneGluingBuffer;
    private FBOTexture2DProgram fxaaBuffer;
    private FBOTexture2DProgram finalRenderedSceneFbo;
    private FBOTexture2DProgram transparencySceneBuffer;

    private TextureProgram ssaoNoiseTexture;
    private TextureProgram ssaoKernelTexture;
    private TextureProgram ssaoBufferTexture;


    public JGemsOpenGLRendererNew(Window window, SceneData sceneData) {
        this.sceneData = sceneData;
        this.sceneRenderBaseContainer = new SceneRenderBaseContainer();

        this.createResources(window.getWindowDimensions());

        this.dearImGuiRender = new DIMGuiRenderJGems(window, JGemsResourceManager.getGlobalGameResources().getResourceCache());
        this.shadowScene = new ShadowScene(this.getSceneData().getSceneWorld());
        this.fboSet = new HashSet<>();

        this.createFBOObjects();
    }

    public FBOTexture2DProgram getSceneGluingBuffer() {
        return this.sceneGluingBuffer;
    }

    public FBOTexture2DProgram getFxaaBuffer() {
        return this.fxaaBuffer;
    }

    public TextureProgram getSsaoBufferTexture() {
        return this.ssaoBufferTexture;
    }

    public TextureProgram getSsaoKernelTexture() {
        return this.ssaoKernelTexture;
    }

    public TextureProgram getSsaoNoiseTexture() {
        return this.ssaoNoiseTexture;
    }

    public FBOTexture2DProgram getTransparencySceneBuffer() {
        return this.transparencySceneBuffer;
    }

    public FBOTexture2DProgram getFinalRenderedSceneFbo() {
        return this.finalRenderedSceneFbo;
    }

    public FBOTexture2DProgram getSsaoBuffer() {
        return this.ssaoBuffer;
    }

    public FBOTexture2DProgram getGBuffer() {
        return this.gBuffer;
    }

    public FBOTexture2DProgram getForwardAndDeferredScenesBuffer() {
        return this.forwardAndDeferredScenesBuffer;
    }

    public FBOTexture2DProgram getBloomBlurredBuffer() {
        return this.bloomBlurredBuffer;
    }

    protected WorldTransparentRender getWorldTransparentRender() {
        return this.worldTransparentRender;
    }

    protected InventoryRender getInventoryRender() {
        return this.inventoryRender;
    }

    protected GuiRender getGuiRender() {
        return this.guiRender;
    }


    private void updateUBOs(FrameTicking frameTicking) {
        JGemsOpenGLRenderer.getGameUboShader().performUniformBuffer(JGemsResourceManager.globalShaderAssets.Misc, new float[]{frameTicking.getFrameDeltaTime()});
        Environment environment = this.getSceneData().getSceneWorld().getEnvironment();
        try (MemoryStack memoryStack = MemoryStack.stackPush()) {
            FloatBuffer value1Buffer = memoryStack.mallocFloat(4);
            value1Buffer.put(!GlobalRenderDebugConstants.FULL_BRIGHT ? environment.getFog().getDensity() : 0.0f);
            value1Buffer.put(environment.getFog().getColor().x);
            value1Buffer.put(environment.getFog().getColor().y);
            value1Buffer.put(environment.getFog().getColor().z);
            value1Buffer.flip();
            JGemsOpenGLRenderer.getGameUboShader().performUniformBuffer(JGemsResourceManager.globalShaderAssets.Fog, value1Buffer);
        }
    }

    protected void createFBOObjects() {
        this.transparencySceneBuffer = this.addFBOInSet(new FBOTexture2DProgram(true));
        this.bloomBlurredBuffer = this.addFBOInSet(new FBOTexture2DProgram(true));
        this.forwardAndDeferredScenesBuffer = this.addFBOInSet(new FBOTexture2DProgram(true));
        this.gBuffer = this.addFBOInSet(new FBOTexture2DProgram(true));
        this.ssaoBuffer = this.addFBOInSet(new FBOTexture2DProgram(true));
        this.finalRenderedSceneFbo = this.addFBOInSet(new FBOTexture2DProgram(true));
        this.sceneGluingBuffer = this.addFBOInSet(new FBOTexture2DProgram(true));
        this.fxaaBuffer = this.addFBOInSet(new FBOTexture2DProgram(true));
    }

    private FBOTexture2DProgram addFBOInSet(FBOTexture2DProgram fboTexture2DProgram) {
        this.getFboSet().add(fboTexture2DProgram);
        return fboTexture2DProgram;
    }

    public void recreateResources(Vector2i windowSize) {
        this.destroyResources();
        this.createResources(windowSize);
    }

    @Override
    public void createResources(Vector2i windowSize) {
        this.createSSAOResources(this.getSSAOParams(windowSize));

        T2DAttachmentContainer transparency = new T2DAttachmentContainer() {{
            add(GL30.GL_COLOR_ATTACHMENT0, GL43.GL_RGBA16F, GL30.GL_RGBA);
            add(GL30.GL_COLOR_ATTACHMENT1, GL43.GL_R8, GL30.GL_RED);
        }};
        this.transparencySceneBuffer.createFrameBuffer2DTexture(windowSize, transparency, true, GL30.GL_NEAREST, GL30.GL_COMPARE_REF_TO_TEXTURE, GL30.GL_LESS, GL30.GL_CLAMP_TO_EDGE, null);

        T2DAttachmentContainer blur = new T2DAttachmentContainer() {{
            add(GL30.GL_COLOR_ATTACHMENT0, GL30.GL_RGB, GL30.GL_RGB);
        }};
        this.bloomBlurredBuffer.createFrameBuffer2DTexture(windowSize, blur, false, GL30.GL_NEAREST, GL30.GL_COMPARE_REF_TO_TEXTURE, GL30.GL_LESS, GL30.GL_CLAMP_TO_EDGE, null);

        T2DAttachmentContainer allScene = new T2DAttachmentContainer() {{
            add(GL30.GL_COLOR_ATTACHMENT0, GL43.GL_RGB16F, GL30.GL_RGB);
            add(GL30.GL_COLOR_ATTACHMENT1, GL43.GL_RGB16F, GL30.GL_RGB);
        }};
        this.forwardAndDeferredScenesBuffer.createFrameBuffer2DTexture(windowSize, allScene, true, GL30.GL_NEAREST, GL30.GL_COMPARE_REF_TO_TEXTURE, GL30.GL_LESS, GL30.GL_CLAMP_TO_EDGE, null);

        T2DAttachmentContainer gBuffer = new T2DAttachmentContainer() {{
            add(GL30.GL_COLOR_ATTACHMENT0, GL43.GL_RGB32F, GL30.GL_RGB);
            add(GL30.GL_COLOR_ATTACHMENT1, GL43.GL_RGB32F, GL30.GL_RGB);
            add(GL30.GL_COLOR_ATTACHMENT2, GL43.GL_RGBA, GL30.GL_RGBA);
            add(GL30.GL_COLOR_ATTACHMENT3, GL43.GL_RGB, GL30.GL_RGB);
            add(GL30.GL_COLOR_ATTACHMENT4, GL43.GL_RGB, GL30.GL_RGB);
            add(GL30.GL_COLOR_ATTACHMENT5, GL43.GL_RGB, GL30.GL_RGB);
        }};
        this.gBuffer.createFrameBuffer2DTexture(new Vector2i(windowSize), gBuffer, true, GL30.GL_NEAREST, GL30.GL_COMPARE_REF_TO_TEXTURE, GL30.GL_LESS, GL30.GL_CLAMP_TO_EDGE, null);

        T2DAttachmentContainer post1 = new T2DAttachmentContainer() {{
            add(GL30.GL_COLOR_ATTACHMENT0, GL30.GL_RGB, GL30.GL_RGB);
        }};
        this.finalRenderedSceneFbo.createFrameBuffer2DTexture(windowSize, post1, false, GL30.GL_NEAREST, GL30.GL_COMPARE_REF_TO_TEXTURE, GL30.GL_LESS, GL30.GL_CLAMP_TO_EDGE, null);

        T2DAttachmentContainer ssao = new T2DAttachmentContainer() {{
            add(GL30.GL_COLOR_ATTACHMENT0, GL30.GL_R16F, GL30.GL_RED);
        }};
        this.ssaoBuffer.createFrameBuffer2DTexture(windowSize, ssao, true, GL30.GL_LINEAR, GL30.GL_COMPARE_REF_TO_TEXTURE, GL30.GL_LESS, GL30.GL_CLAMP_TO_EDGE, null);

        T2DAttachmentContainer fxaa = new T2DAttachmentContainer() {{
            add(GL30.GL_COLOR_ATTACHMENT0, GL30.GL_RGB, GL30.GL_RGB);
        }};
        this.fxaaBuffer.createFrameBuffer2DTexture(windowSize, fxaa, true, GL30.GL_LINEAR, GL30.GL_COMPARE_REF_TO_TEXTURE, GL30.GL_LESS, GL30.GL_CLAMP_TO_EDGE, null);

        T2DAttachmentContainer gluing = new T2DAttachmentContainer() {{
            add(GL30.GL_COLOR_ATTACHMENT0, GL30.GL_RGB16F, GL30.GL_RGB);
            add(GL30.GL_COLOR_ATTACHMENT1, GL30.GL_RGB16F, GL30.GL_RGB);
        }};
        this.ssaoBuffer.createFrameBuffer2DTexture(windowSize, gluing, true, GL30.GL_LINEAR, GL30.GL_COMPARE_REF_TO_TEXTURE, GL30.GL_LESS, GL30.GL_CLAMP_TO_EDGE, null);
    }

    private void createSSAOResources(Vector3i ssaoParams) {
        if (ssaoParams == null) {
            return;
        }
        this.ssaoKernelTexture = this.calcSSAOKernel(ssaoParams.z * ssaoParams.z);
        this.ssaoNoiseTexture = this.calcSSAONoise(JGemsSceneGlobalConstants.SSAO_NOISE_SIZE * JGemsSceneGlobalConstants.SSAO_NOISE_SIZE);
        this.ssaoBufferTexture = this.createSSAOBuffer(new Vector2i(ssaoParams.x, ssaoParams.y));
    }

    private void destroySsaoTextures() {
        if (this.getSsaoNoiseTexture() != null) {
            this.getSsaoNoiseTexture().cleanUp();
            this.ssaoNoiseTexture = null;
        }
        if (this.getSsaoKernelTexture() != null) {
            this.getSsaoKernelTexture().cleanUp();
            this.ssaoKernelTexture = null;
        }
        if (this.getSsaoBufferTexture() != null) {
            this.getSsaoBufferTexture().cleanUp();
            this.ssaoBufferTexture = null;
        }
    }

    @Override
    public void destroyResources() {
        this.destroySsaoTextures();
        this.getFboSet().forEach(FBOTexture2DProgram::clearFBO);
    }

    private void fillScene() {
        this.guiRender = new GuiRender(this);
        this.inventoryRender = new InventoryRender(this);
        this.worldTransparentRender = new WorldTransparentRender(this);

        this.getSceneRenderBaseContainer().addBaseInForwardContainer(new WorldForwardRender(this));
        this.getSceneRenderBaseContainer().addBaseInForwardContainer(new SkyRender(this));
        this.getSceneRenderBaseContainer().addBaseInForwardContainer(new DebugRender(this));

        this.getSceneRenderBaseContainer().addBaseInDeferredContainer(new WorldDeferredRender(this));

        this.getSceneRenderBaseContainer().addBaseInTransparencyContainer(new ParticlesRender(this));
        this.getSceneRenderBaseContainer().addBaseInTransparencyContainer(new LiquidsRender(this));
        this.getSceneRenderBaseContainer().addBaseInTransparencyContainer(this.getWorldTransparentRender());

        this.getSceneRenderBaseContainer().addBaseInInventoryForwardContainer(this.getInventoryRender());
        this.getSceneRenderBaseContainer().addBaseInGUIContainer(this.getGuiRender());
    }

    @Override
    public void onStartRender() {
        this.fillScene();
        this.getSceneRenderBaseContainer().startAll();
    }

    @Override
    public void onStopRender() {
        this.getDearImGuiRender().cleanUp();
        this.getSceneRenderBaseContainer().endAll();
        this.destroyResources();
    }

    public void screenBloomHDRCorrection(Model<Format2D> model) {
        GL30.glEnable(GL30.GL_BLEND);
        GL30.glBlendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);

        this.getFinalRenderedSceneFbo().bindFBO();
        GL30.glClear(GL30.GL_COLOR_BUFFER_BIT | GL30.GL_DEPTH_BUFFER_BIT);

        this.getPostProcessingShader().bind();
        this.getPostProcessingShader().performUniformTexture("texture_sampler", this.getSceneFbo().getTextureIDByIndex(0), GL30.GL_TEXTURE_2D);
        this.getPostProcessingShader().performUniformTexture("blur_sampler", this.getFboBlur().getTextureIDByIndex(0), GL30.GL_TEXTURE_2D);

        this.getPostProcessingShader().performUniformTexture("accumulated_alpha", this.getTransparencyFBO().getTextureIDByIndex(0), GL30.GL_TEXTURE_2D);
        this.getPostProcessingShader().performUniformTexture("reveal_alpha", this.getTransparencyFBO().getTextureIDByIndex(1), GL30.GL_TEXTURE_2D);

        this.getPostProcessingShader().getUtils().performOrthographicMatrix(model);
        JGemsSceneUtils.renderModel(model, GL30.GL_TRIANGLES);
        this.getPostProcessingShader().unBind();

        this.getFinalRenderedSceneFbo().unBindFBO();
        GL30.glDisable(GL30.GL_BLEND);
    }

    @Override
    public void onRender(FrameTicking frameTicking, Vector2i windowSize) {
        this.updateUBOs(frameTicking);
        if (this.getSceneData().getCamera() == null) {
            GL30.glClear(GL30.GL_COLOR_BUFFER_BIT);
            this.guiRender.onRender(frameTicking);
            this.takeScreenShotIfNeeded(windowSize);
            return;
        }
        this.getSceneData().getSceneWorld().getEnvironment().onUpdate(this.getSceneData().getSceneWorld());
        try (Model<Format2D> model = MeshHelper.generatePlane2DModelInverted(new Vector2f(0.0f), new Vector2f(windowSize), 0)) {
            if (JGems3D.get().isPaused()) {
                GL30.glClear(GL30.GL_COLOR_BUFFER_BIT);
                this.guiRender.onRender(frameTicking);
            } else {
                GL30.glClear(GL30.GL_COLOR_BUFFER_BIT | GL30.GL_DEPTH_BUFFER_BIT | GL30.GL_STENCIL_BUFFER_BIT);
                this.getShadowScene().renderAllModelsInShadowMap(this.getSceneData().getSceneWorld().getModeledSceneEntities());
                JGems3D.get().getScreen().normalizeViewPort();

                this.renderForwardAndDeferredScenes(frameTicking, windowSize, model);
                this.blurBloomBuffer(model, windowSize);
                this.renderTransparentObjects(frameTicking, windowSize);
                this.renderSceneWithBloomAndHDR(frameTicking, model);
                this.postFXAA(model);
                this.renderFinalScene(model);

                this.getInventoryRender().onRender(frameTicking);
                this.getGuiRender().onRender(partialTicks);
            }
        }
        this.takeScreenShotIfNeeded(windowSize);
    }

    public void renderForwardAndDeferredScenes(FrameTicking frameTicking, Vector2i windowSize, Model<Format2D> model) {
        this.deferredGeometry(frameTicking);

        GL30.glDisable(GL30.GL_DEPTH_TEST);
        this.calcSSAOValueOnGBuffer(model, windowSize);
        GL30.glEnable(GL30.GL_DEPTH_TEST);

        this.getForwardAndDeferredScenesBuffer().bindFBO();
        GL30.glClear(GL30.GL_COLOR_BUFFER_BIT | GL30.GL_DEPTH_BUFFER_BIT);
        this.deferredLighting(model);
        this.getForwardAndDeferredScenesBuffer().unBindFBO();

        this.getGBuffer().copyFBOtoFBODepth(this.getForwardAndDeferredScenesBuffer().getFrameBufferId(), windowSize);

        this.getForwardAndDeferredScenesBuffer().bindFBO();
        this.renderForwardScene(frameTicking);
        this.getForwardAndDeferredScenesBuffer().unBindFBO();
    }

    private void deferredGeometry(FrameTicking frameTicking) {
        this.getGBuffer().bindFBO();
        GL30.glClear(GL30.GL_COLOR_BUFFER_BIT | GL30.GL_DEPTH_BUFFER_BIT);
        SceneRenderBaseContainer.renderSceneRenderSet(frameTicking, this.getSceneRenderBaseContainer().getDeferredRenderSet());
        this.getGBuffer().unBindFBO();
    }

    private void deferredLighting(Model<Format2D> model) {
        JGemsShaderManager deferredShader = JGemsResourceManager.globalShaderAssets.world_deferred;
        deferredShader.bind();
        deferredShader.performUniform("view_matrix", JGemsSceneUtils.getMainCameraViewMatrix());
        deferredShader.performUniformTexture("gPositions", this.getGBuffer().getTextureIDByIndex(0), GL30.GL_TEXTURE_2D);
        deferredShader.performUniformTexture("gNormals", this.getGBuffer().getTextureIDByIndex(1), GL30.GL_TEXTURE_2D);
        deferredShader.performUniformTexture("gTexture", this.getGBuffer().getTextureIDByIndex(2), GL30.GL_TEXTURE_2D);
        deferredShader.performUniformTexture("gEmission", this.getGBuffer().getTextureIDByIndex(3), GL30.GL_TEXTURE_2D);
        deferredShader.performUniformTexture("gSpecular", this.getGBuffer().getTextureIDByIndex(4), GL30.GL_TEXTURE_2D);
        deferredShader.performUniformTexture("gMetallic", this.getGBuffer().getTextureIDByIndex(5), GL30.GL_TEXTURE_2D);
        deferredShader.performUniformTexture("ssaoSampler", this.getSsaoBuffer().getTextureIDByIndex(0), GL30.GL_TEXTURE_2D);
        deferredShader.performUniform("isSsaoValid", this.getSsaoBufferTexture() != null);
        deferredShader.getUtils().performShadowsInfo();
        deferredShader.getUtils().performOrthographicMatrix(model);
        JGemsSceneUtils.renderModel(model, GL30.GL_TRIANGLES);
        deferredShader.unBind();
    }

    private void renderForwardScene(FrameTicking frameTicking) {
        GL30.glEnable(GL30.GL_BLEND);
        GL30.glBlendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);
        SceneRenderBaseContainer.renderSceneRenderSet(frameTicking, this.getSceneRenderBaseContainer().getForwardRenderSet());
        GL30.glDisable(GL30.GL_BLEND);
    }

    private void renderTransparentObjects(FrameTicking frameTicking, Vector2i windowSize) {
        this.getForwardAndDeferredScenesBuffer().copyFBOtoFBODepth(this.getTransparencySceneBuffer().getFrameBufferId(), windowSize);
        GL30.glDepthMask(false);
        GL30.glEnable(GL30.GL_BLEND);
        GL45.glBlendFunci(0, GL45.GL_ONE, GL45.GL_ONE);
        GL45.glBlendFunci(1, GL45.GL_ZERO, GL45.GL_ONE_MINUS_SRC_COLOR);
        GL45.glBlendEquation(GL30.GL_FUNC_ADD);
        this.getTransparencySceneBuffer().bindFBO();
        GL45.glClearBufferfv(GL30.GL_COLOR, 0, new float[] {0.0f, 0.0f, 0.0f, 0.0f});
        GL45.glClearBufferfv(GL30.GL_COLOR, 1, new float[] {1.0f, 1.0f, 1.0f, 1.0f});
        SceneRenderBaseContainer.renderSceneRenderSet(frameTicking, this.getSceneRenderBaseContainer().getTransparencyRenderSet());
        this.getTransparencySceneBuffer().unBindFBO();
        GL30.glDisable(GL30.GL_BLEND);
        GL30.glDepthMask(true);
    }

    private void calcSSAOValueOnGBuffer(Model<Format2D> model, Vector2i windowSize) {
        JGemsShaderManager ssaoComputeShader = JGemsResourceManager.globalShaderAssets.world_ssao;
        ssaoComputeShader.startComputing();
        ssaoComputeShader.performUniform("noiseScale", new Vector2f(windowSize).div((float) JGemsSceneGlobalConstants.SSAO_NOISE_SIZE));
        ssaoComputeShader.performUniform("projection_matrix", JGemsSceneUtils.getMainPerspectiveMatrix());
        ssaoComputeShader.performUniformTexture("gPositions", this.getGBuffer().getTextureIDByIndex(0), GL30.GL_TEXTURE_2D);
        ssaoComputeShader.performUniformTexture("gNormals", this.getGBuffer().getTextureIDByIndex(1), GL30.GL_TEXTURE_2D);
        ssaoComputeShader.performUniformTexture("ssaoNoise", this.getSsaoNoiseTexture().getTextureId(), GL30.GL_TEXTURE_2D);
        ssaoComputeShader.performUniformTexture("ssaoKernel", this.getSsaoKernelTexture().getTextureId(), GL30.GL_TEXTURE_2D);
        GL43.glBindImageTexture(4, this.getSsaoBufferTexture().getTextureId(), 0, false, 0, GL30.GL_WRITE_ONLY, GL30.GL_RGBA16F);
        ssaoComputeShader.dispatchComputeShader(windowSize.x / 16, windowSize.y / 16, 1, GL43.GL_SHADER_IMAGE_ACCESS_BARRIER_BIT);
        ssaoComputeShader.endComputing();

        JGemsShaderManager ssaoBlur = JGemsResourceManager.globalShaderAssets.blur_ssao;
        this.getSsaoBuffer().bindFBO();
        ssaoBlur.bind();
        ssaoBlur.performUniformTexture("texture_sampler", this.getSsaoBufferTexture().getTextureId(), GL30.GL_TEXTURE_2D);
        ssaoBlur.getUtils().performOrthographicMatrix(model);
        JGemsSceneUtils.renderModel(model, GL30.GL_TRIANGLES);
        ssaoBlur.unBind();
        this.getSsaoBuffer().unBindFBO();
    }

    private void blurBloomBuffer(Model<Format2D> model, Vector2i windowSize) {
        if (JGems3D.get().getGameSettings().bloom.getValue() == 0) {
            this.getBloomBlurredBuffer().bindFBO();
            GL30.glClear(GL30.GL_COLOR_BUFFER_BIT);
            this.getBloomBlurredBuffer().unBindFBO();
            return;
        }
        JGemsShaderManager blurShader = JGemsResourceManager.globalShaderAssets.blur13;
        FBOTexture2DProgram startFbo = this.getForwardAndDeferredScenesBuffer();
        int startBinding = 1;
        int steps = 6;

        blurShader.bind();
        blurShader.performUniform("resolution", new Vector2f(windowSize).div(2.0f));
        for (int i = 0; i < steps; i++) {
            this.getBloomBlurredBuffer().bindFBO();
            blurShader.performUniformTexture("texture_sampler", startFbo.getTextureIDByIndex(startBinding), GL30.GL_TEXTURE_2D);
            blurShader.performUniform("direction", i % 2 == 0 ? new Vector2f(1.0f, 0.0f) : new Vector2f(0.0f, 1.0f));
            blurShader.getUtils().performOrthographicMatrix(model);
            JGemsSceneUtils.renderModel(model, GL30.GL_TRIANGLES);
            this.getBloomBlurredBuffer().unBindFBO();
            startFbo = this.getBloomBlurredBuffer();
            startBinding = 0;
        }
        blurShader.unBind();
    }

    public void addModelNodeInTransparencyPass(WorldTransparentRender.RenderNodeInfo node) {
        this.getWorldTransparentRender().addModelNodeInTransparencyPass(node);
    }

    public void addSceneModelObjectInTransparencyPass(IModeledSceneObject modeledSceneObject) {
        this.getWorldTransparentRender().addSceneModelObjectInTransparencyPass(modeledSceneObject);
    }

    private void takeScreenShotIfNeeded(Vector2i windowSize) {
        if (this.wantToTakeScreenshot) {
            JGemsHelper.getLogger().log("Took screenshot!");
            this.writeBufferInFile(windowSize);
            this.wantToTakeScreenshot = false;
        }
    }

    @Override
    public void onWindowResize(Vector2i windowSize) {
        this.recreateResources(windowSize);
        this.getDearImGuiRender().onResize(windowSize);
    }

    public void takeScreenShot() {
        this.wantToTakeScreenshot = true;
    }

    public ShadowScene getShadowScene() {
        return this.shadowScene;
    }

    private Set<FBOTexture2DProgram> getFboSet() {
        return this.fboSet;
    }

    public DIMGuiRenderJGems getDearImGuiRender() {
        return this.dearImGuiRender;
    }

    public SceneRenderBaseContainer getSceneRenderBaseContainer() {
        return this.sceneRenderBaseContainer;
    }

    @Override
    public SceneData getSceneData() {
        return this.sceneData;
    }

    private TextureProgram calcSSAOKernel(int size) {
        TextureProgram textureProgram = new TextureProgram();
        FloatBuffer floatBuffer = MemoryUtil.memAllocFloat(size * 3);
        for (int i = 0; i < size; ++i) {
            float x = JGems3D.random.nextFloat() * 2.0f - 1.0f;
            float y = JGems3D.random.nextFloat() * 2.0f - 1.0f;
            float z = JGems3D.random.nextFloat();

            Vector3f sample = new Vector3f(x, y, z);
            sample.normalize();
            sample.mul(JGems3D.random.nextFloat());

            float scale = (float) i / ((float) size);
            scale = JGemsHelper.lerp(0.1f, 1.0f, scale * scale);
            sample.mul(scale);

            floatBuffer.put(sample.x);
            floatBuffer.put(sample.y);
            floatBuffer.put(sample.z);
        }
        floatBuffer.flip();
        int s = (int) Math.sqrt(size);
        textureProgram.createTexture(new Vector2i(s), GL30.GL_RGB16F, GL30.GL_RGB, GL30.GL_NEAREST, GL30.GL_NEAREST, GL30.GL_NONE, GL30.GL_LESS, GL30.GL_REPEAT, GL30.GL_REPEAT, null, floatBuffer);
        MemoryUtil.memFree(floatBuffer);
        return textureProgram;
    }

    private TextureProgram calcSSAONoise(int size) {
        TextureProgram textureProgram = new TextureProgram();
        FloatBuffer floatBuffer = MemoryUtil.memAllocFloat(size * 3);
        for (int i = 0; i < size; ++i) {
            float x = JGems3D.random.nextFloat() * 2.0f - 1.0f;
            float y = JGems3D.random.nextFloat() * 2.0f - 1.0f;
            floatBuffer.put(x);
            floatBuffer.put(y);
            floatBuffer.put(0.0f);
        }
        floatBuffer.flip();
        int s = (int) Math.sqrt(size);
        textureProgram.createTexture(new Vector2i(s), GL30.GL_RGB16F, GL30.GL_RGB, GL30.GL_NEAREST, GL30.GL_NEAREST, GL30.GL_NONE, GL30.GL_LESS, GL30.GL_REPEAT, GL30.GL_REPEAT, null, floatBuffer);
        MemoryUtil.memFree(floatBuffer);
        return textureProgram;
    }

    private TextureProgram createSSAOBuffer(Vector2i windowSize) {
        TextureProgram textureProgram = new TextureProgram();
        textureProgram.createTexture(windowSize, GL30.GL_RGBA16F, GL30.GL_RGBA, GL30.GL_LINEAR, GL30.GL_LINEAR, GL30.GL_NONE, GL30.GL_LESS, GL30.GL_CLAMP_TO_EDGE, GL30.GL_CLAMP_TO_EDGE, null);
        return textureProgram;
    }

    private Vector3i getSSAOParams(Vector2i windowSize) {
        switch (JGems3D.get().getGameSettings().ssao.getValue()) {
            case 1: {
                return new Vector3i((int) (windowSize.x * 0.5f), (int) (windowSize.y * 0.5f), 4);
            }
            case 2: {
                return new Vector3i((int) (windowSize.x * 0.5f), (int) (windowSize.y * 0.5f), 6);
            }
            case 3: {
                return new Vector3i((int) (windowSize.x * 0.5f), (int) (windowSize.y * 0.5f), 8);
            }
            case 0:
            default: {
                return null;
            }
        }
    }

    private void writeBufferInFile(Vector2i windowSize) {
        int w = windowSize.x;
        int h = windowSize.y;
        int i1 = w * h;
        ByteBuffer p = ByteBuffer.allocateDirect(i1 * 4);
        GL30.glReadPixels(0, 0, w, h, GL30.GL_RGBA, GL30.GL_UNSIGNED_BYTE, p);
        try {
            BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
            int[] pArray = new int[i1];
            p.asIntBuffer().get(pArray);
            for (int y = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    int i = (x + (w * y)) * 4;
                    int r = p.get(i) & 0xFF;
                    int g = p.get(i + 1) & 0xFF;
                    int b = p.get(i + 2) & 0xFF;
                    int a = p.get(i + 3) & 0xFF;
                    int rgb = (a << 24) | (r << 16) | (g << 8) | b;
                    image.setRGB(x, windowSize.y - y - 1, rgb);
                }
            }
            Path scrPath = Paths.get(JGems3D.getGameFilesFolder() + "/screenshots/");
            if (!Files.exists(scrPath)) {
                Files.createDirectories(scrPath);
            }
            String builder = scrPath + "/screen_" + JGems3D.systemTime() + ".png";
            ImageIO.write(image, "PNG", new File(builder));
        } catch (IOException e) {
            JGemsHelper.getLogger().warn(e.getMessage());
        }
    }

    public static final class SceneRenderBaseContainer {
        private final Set<SceneRenderBase> forwardRenderSet;
        private final Set<SceneRenderBase> deferredRenderSet;
        private final Set<SceneRenderBase> transparencyRenderSet;
        private final Set<SceneRenderBase> guiRenderSet;
        private final Set<SceneRenderBase> inventoryRenderSet;

        public SceneRenderBaseContainer() {
            this.forwardRenderSet = new TreeSet<>(Comparator.comparingInt(SceneRenderBase::getRenderOrder));
            this.deferredRenderSet = new TreeSet<>(Comparator.comparingInt(SceneRenderBase::getRenderOrder));
            this.transparencyRenderSet = new TreeSet<>(Comparator.comparingInt(SceneRenderBase::getRenderOrder));
            this.guiRenderSet = new TreeSet<>(Comparator.comparingInt(SceneRenderBase::getRenderOrder));
            this.inventoryRenderSet = new TreeSet<>(Comparator.comparingInt(SceneRenderBase::getRenderOrder));
        }

        public static void renderSceneRenderSet(FrameTicking frameTicking, Set<SceneRenderBase> sceneRenderBases) {
            sceneRenderBases.forEach(e -> e.onRender(frameTicking));
        }

        public void endAll() {
            this.getForwardRenderSet().forEach(SceneRenderBase::onStopRender);
            this.getDeferredRenderSet().forEach(SceneRenderBase::onStopRender);
            this.getTransparencyRenderSet().forEach(SceneRenderBase::onStopRender);
            this.getGuiRenderSet().forEach(SceneRenderBase::onStopRender);
            this.getInventoryRenderSet().forEach(SceneRenderBase::onStopRender);
        }

        public void startAll() {
            this.getForwardRenderSet().forEach(SceneRenderBase::onStartRender);
            this.getDeferredRenderSet().forEach(SceneRenderBase::onStartRender);
            this.getTransparencyRenderSet().forEach(SceneRenderBase::onStartRender);
            this.getGuiRenderSet().forEach(SceneRenderBase::onStartRender);
            this.getInventoryRenderSet().forEach(SceneRenderBase::onStartRender);
        }

        public void addBaseInGUIContainer(SceneRenderBase base) {
            this.getGuiRenderSet().add(base);
        }

        public void addBaseInInventoryForwardContainer(SceneRenderBase base) {
            this.getInventoryRenderSet().add(base);
        }

        public void addBaseInForwardContainer(SceneRenderBase base) {
            this.getForwardRenderSet().add(base);
        }

        public void addBaseInDeferredContainer(SceneRenderBase base) {
            this.getDeferredRenderSet().add(base);
        }

        public void addBaseInTransparencyContainer(SceneRenderBase base) {
            this.getTransparencyRenderSet().add(base);
        }

        public Set<SceneRenderBase> getInventoryRenderSet() {
            return this.inventoryRenderSet;
        }

        public Set<SceneRenderBase> getGuiRenderSet() {
            return this.guiRenderSet;
        }

        public Set<SceneRenderBase> getTransparencyRenderSet() {
            return this.transparencyRenderSet;
        }

        public Set<SceneRenderBase> getDeferredRenderSet() {
            return this.deferredRenderSet;
        }

        public Set<SceneRenderBase> getForwardRenderSet() {
            return this.forwardRenderSet;
        }
    }
}