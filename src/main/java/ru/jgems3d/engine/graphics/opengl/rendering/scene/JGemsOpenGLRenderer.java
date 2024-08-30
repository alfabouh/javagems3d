/*
 * *
 *  * @author alfabouh
 *  * @since 2024
 *  * @link https://github.com/alfabouh/JavaGems3D
 *  *
 *  * This software is provided 'as-is', without any express or implied warranty.
 *  * In no event will the authors be held liable for any damages arising from the use of this software.
 *
 */

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
import ru.jgems3d.engine.api_bridge.events.APIEventsLauncher;
import ru.jgems3d.engine.graphics.opengl.dear_imgui.DIMGuiRenderJGems;
import ru.jgems3d.engine.graphics.opengl.environment.Environment;
import ru.jgems3d.engine.graphics.opengl.environment.light.LightManager;
import ru.jgems3d.engine.graphics.opengl.environment.shadow.ShadowManager;
import ru.jgems3d.engine.graphics.opengl.rendering.JGemsDebugGlobalConstants;
import ru.jgems3d.engine.graphics.opengl.rendering.JGemsSceneGlobalConstants;
import ru.jgems3d.engine.graphics.opengl.rendering.JGemsSceneUtils;
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
import ru.jgems3d.engine.system.resources.assets.models.formats.Format2D;
import ru.jgems3d.engine.system.resources.assets.shaders.UniformString;
import ru.jgems3d.engine.system.resources.assets.shaders.manager.JGemsShaderManager;
import ru.jgems3d.engine.system.resources.manager.JGemsResourceManager;
import ru.jgems3d.engine_api.events.bus.Events;

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

public class JGemsOpenGLRenderer implements ISceneRenderer {
    private final SceneData sceneData;
    private final SceneRenderBaseContainer sceneRenderBaseContainer;
    private final DIMGuiRenderJGems dearImGuiRender;
    private final Set<FBOTexture2DProgram> fboSet;
    private boolean wantToTakeScreenshot;
    private GuiRender guiRender;
    private InventoryRender inventoryRender;
    private WorldTransparentRender worldTransparentRender;

    private FBOTexture2DProgram bloomBlurredBuffer;
    private FBOTexture2DProgram forwardAndDeferredScenesBuffer;
    private FBOTexture2DProgram gBuffer;
    private FBOTexture2DProgram ssaoBuffer;
    private FBOTexture2DProgram sceneGluingBuffer;
    private FBOTexture2DProgram fxaaBuffer;
    private FBOTexture2DProgram hdrBuffer;
    private FBOTexture2DProgram transparencySceneBuffer;
    private FBOTexture2DProgram finalizingBuffer;

    private TextureProgram ssaoNoiseTexture;
    private TextureProgram ssaoKernelTexture;
    private TextureProgram ssaoBufferTexture;

    private Model<Format2D> screenModel;

    public JGemsOpenGLRenderer(Window window, SceneData sceneData) {
        this.fboSet = new HashSet<>();

        this.sceneData = sceneData;
        this.sceneRenderBaseContainer = new SceneRenderBaseContainer();
        this.createFBOObjects();

        this.createResources(window.getWindowDimensions());

        this.dearImGuiRender = new DIMGuiRenderJGems(window, JGemsResourceManager.getGlobalGameResources().getResourceCache());
    }

    public static JGemsShaderManager getGameUboShader() {
        return JGemsResourceManager.globalShaderAssets.gameUbo;
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

    public FBOTexture2DProgram getHdrBuffer() {
        return this.hdrBuffer;
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

    public FBOTexture2DProgram getFinalizingBuffer() {
        return this.finalizingBuffer;
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
        JGemsOpenGLRenderer.getGameUboShader().performUniformBuffer(JGemsResourceManager.globalShaderAssets.Misc, new float[]{JGemsHelper.getScreen().getRenderTicks()});
        Environment environment = this.getSceneData().getSceneWorld().getEnvironment();
        try (MemoryStack memoryStack = MemoryStack.stackPush()) {
            FloatBuffer value1Buffer = memoryStack.mallocFloat(4);
            value1Buffer.put(!JGemsDebugGlobalConstants.FULL_BRIGHT ? environment.getFog().getDensity() : 0.0f);
            value1Buffer.put(environment.getFog().getColor().x * environment.getSky().getSunBrightness());
            value1Buffer.put(environment.getFog().getColor().y * environment.getSky().getSunBrightness());
            value1Buffer.put(environment.getFog().getColor().z * environment.getSky().getSunBrightness());
            value1Buffer.flip();
            JGemsOpenGLRenderer.getGameUboShader().performUniformBuffer(JGemsResourceManager.globalShaderAssets.Fog, value1Buffer);
        }
    }

    public JGemsShaderManager getBasicOITShader() {
        return JGemsResourceManager.globalShaderAssets.weighted_oit;
    }

    protected void createFBOObjects() {
        this.transparencySceneBuffer = this.addFBOInSet(new FBOTexture2DProgram(true));
        this.bloomBlurredBuffer = this.addFBOInSet(new FBOTexture2DProgram(true));
        this.forwardAndDeferredScenesBuffer = this.addFBOInSet(new FBOTexture2DProgram(true));
        this.gBuffer = this.addFBOInSet(new FBOTexture2DProgram(true));
        this.ssaoBuffer = this.addFBOInSet(new FBOTexture2DProgram(true));
        this.hdrBuffer = this.addFBOInSet(new FBOTexture2DProgram(true));
        this.sceneGluingBuffer = this.addFBOInSet(new FBOTexture2DProgram(true));
        this.fxaaBuffer = this.addFBOInSet(new FBOTexture2DProgram(true));
        this.finalizingBuffer = this.addFBOInSet(new FBOTexture2DProgram(true));
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
        this.getShadowScene().createResources();
        this.createSSAOResources(this.getSSAOParams(windowSize));

        T2DAttachmentContainer transparency = new T2DAttachmentContainer() {{
            add(GL30.GL_COLOR_ATTACHMENT0, GL43.GL_RGBA16F, GL30.GL_RGBA);
            add(GL30.GL_COLOR_ATTACHMENT1, GL43.GL_R8, GL30.GL_RED);
            add(GL30.GL_COLOR_ATTACHMENT2, GL43.GL_RGBA16F, GL30.GL_RGBA);
        }};
        this.transparencySceneBuffer.createFrameBuffer2DTexture(windowSize, transparency, true, GL30.GL_NEAREST, GL30.GL_COMPARE_REF_TO_TEXTURE, GL30.GL_LESS, GL30.GL_CLAMP_TO_EDGE, null);

        T2DAttachmentContainer blur = new T2DAttachmentContainer() {{
            add(GL30.GL_COLOR_ATTACHMENT0, GL30.GL_RGB, GL30.GL_RGB);
        }};
        this.bloomBlurredBuffer.createFrameBuffer2DTexture(windowSize, blur, false, GL30.GL_LINEAR, GL30.GL_COMPARE_REF_TO_TEXTURE, GL30.GL_LESS, GL30.GL_CLAMP_TO_EDGE, null);

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

        T2DAttachmentContainer hdr = new T2DAttachmentContainer() {{
            add(GL30.GL_COLOR_ATTACHMENT0, GL30.GL_RGB, GL30.GL_RGB);
        }};
        this.hdrBuffer.createFrameBuffer2DTexture(windowSize, hdr, false, GL30.GL_NEAREST, GL30.GL_COMPARE_REF_TO_TEXTURE, GL30.GL_LESS, GL30.GL_CLAMP_TO_EDGE, null);

        T2DAttachmentContainer ssao = new T2DAttachmentContainer() {{
            add(GL30.GL_COLOR_ATTACHMENT0, GL30.GL_R16F, GL30.GL_RED);
        }};
        this.ssaoBuffer.createFrameBuffer2DTexture(windowSize, ssao, false, GL30.GL_NEAREST, GL30.GL_COMPARE_REF_TO_TEXTURE, GL30.GL_LESS, GL30.GL_CLAMP_TO_EDGE, null);

        T2DAttachmentContainer fxaa = new T2DAttachmentContainer() {{
            add(GL30.GL_COLOR_ATTACHMENT0, GL30.GL_RGB, GL30.GL_RGB);
        }};
        this.fxaaBuffer.createFrameBuffer2DTexture(windowSize, fxaa, false, GL30.GL_NEAREST, GL30.GL_COMPARE_REF_TO_TEXTURE, GL30.GL_LESS, GL30.GL_CLAMP_TO_EDGE, null);

        T2DAttachmentContainer gluing = new T2DAttachmentContainer() {{
            add(GL30.GL_COLOR_ATTACHMENT0, GL30.GL_RGB16F, GL30.GL_RGB);
            add(GL30.GL_COLOR_ATTACHMENT1, GL30.GL_RGB, GL30.GL_RGB);
        }};
        this.sceneGluingBuffer.createFrameBuffer2DTexture(windowSize, gluing, false, GL30.GL_NEAREST, GL30.GL_COMPARE_REF_TO_TEXTURE, GL30.GL_LESS, GL30.GL_CLAMP_TO_EDGE, null);

        T2DAttachmentContainer finalB = new T2DAttachmentContainer() {{
            add(GL30.GL_COLOR_ATTACHMENT0, GL30.GL_RGB, GL30.GL_RGB);
        }};
        this.finalizingBuffer.createFrameBuffer2DTexture(windowSize, finalB, false, GL30.GL_NEAREST, GL30.GL_COMPARE_REF_TO_TEXTURE, GL30.GL_LESS, GL30.GL_CLAMP_TO_EDGE, null);
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
        this.getShadowScene().destroyResources();
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
        this.screenModel = JGemsSceneUtils.createScreenModel();
        this.fillScene();
        this.getSceneRenderBaseContainer().startAll();
    }

    @Override
    public void onStopRender() {
        this.getDearImGuiRender().cleanUp();
        this.getSceneRenderBaseContainer().endAll();
        this.destroyResources();
        this.screenModel.clean();
    }

    //section HDR
    public void screenBloomHDRCorrection(Model<Format2D> model) {
        GL30.glEnable(GL30.GL_BLEND);
        GL30.glBlendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);
        this.getHdrBuffer().bindFBO();
        GL30.glClear(GL30.GL_COLOR_BUFFER_BIT | GL30.GL_DEPTH_BUFFER_BIT);
        JGemsShaderManager hdr = JGemsResourceManager.globalShaderAssets.hdr;
        hdr.bind();
        hdr.performUniform(new UniformString("exposure"), JGemsSceneGlobalConstants.HDR_EXPOSURE);
        hdr.performUniform(new UniformString("gamma"), JGemsSceneGlobalConstants.HDR_GAMMA);
        hdr.performUniform(new UniformString("use_hdr"), JGemsSceneGlobalConstants.USE_HDR);
        hdr.performUniformTexture(new UniformString("texture_sampler"), this.getSceneGluingBuffer().getTextureIDByIndex(0), GL30.GL_TEXTURE_2D);
        hdr.performUniformTexture(new UniformString("bloom_sampler"), this.getBloomBlurredBuffer().getTextureIDByIndex(0), GL30.GL_TEXTURE_2D);
        hdr.getUtils().performOrthographicMatrix(model);
        JGemsSceneUtils.renderModel(model, GL30.GL_TRIANGLES);
        hdr.unBind();
        this.getHdrBuffer().unBindFBO();
        GL30.glDisable(GL30.GL_BLEND);
    }

    //section Gluing
    public void sceneGluing(Model<Format2D> model) {
        GL30.glEnable(GL30.GL_BLEND);
        GL30.glBlendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);
        this.getSceneGluingBuffer().bindFBO();
        GL30.glClear(GL30.GL_COLOR_BUFFER_BIT | GL30.GL_DEPTH_BUFFER_BIT);
        JGemsShaderManager gluing = JGemsResourceManager.globalShaderAssets.scene_gluing;
        gluing.bind();
        gluing.performUniformTexture(new UniformString("texture_sampler"), this.getForwardAndDeferredScenesBuffer().getTextureIDByIndex(0), GL30.GL_TEXTURE_2D);
        gluing.performUniformTexture(new UniformString("bloom_sampler"), this.getForwardAndDeferredScenesBuffer().getTextureIDByIndex(1), GL30.GL_TEXTURE_2D);

        gluing.performUniformTexture(new UniformString("bloom_sampler2"), this.getTransparencySceneBuffer().getTextureIDByIndex(2), GL30.GL_TEXTURE_2D);
        gluing.performUniformTexture(new UniformString("accumulated_alpha"), this.getTransparencySceneBuffer().getTextureIDByIndex(0), GL30.GL_TEXTURE_2D);
        gluing.performUniformTexture(new UniformString("reveal_alpha"), this.getTransparencySceneBuffer().getTextureIDByIndex(1), GL30.GL_TEXTURE_2D);
        gluing.getUtils().performOrthographicMatrix(model);
        JGemsSceneUtils.renderModel(model, GL30.GL_TRIANGLES);
        gluing.unBind();
        this.getSceneGluingBuffer().unBindFBO();
        GL30.glDisable(GL30.GL_BLEND);
    }

    //section FXAA
    public void postFXAA(Model<Format2D> model, Vector2i windowSize) {
        JGemsShaderManager fxaaFilter = JGemsResourceManager.globalShaderAssets.fxaa;
        this.getFxaaBuffer().bindFBO();
        GL30.glClear(GL30.GL_COLOR_BUFFER_BIT | GL30.GL_DEPTH_BUFFER_BIT);
        fxaaFilter.bind();
        fxaaFilter.performUniform(new UniformString("use_fxaa"), JGemsSceneGlobalConstants.USE_FXAA);
        fxaaFilter.performUniform(new UniformString("resolution"), new Vector2f(windowSize));
        fxaaFilter.performUniformTexture(new UniformString("texture_sampler"), this.getHdrBuffer().getTextureIDByIndex(0), GL30.GL_TEXTURE_2D);
        fxaaFilter.performUniform(new UniformString("FXAA_SPAN_MAX"), (float) Math.pow(JGems3D.get().getGameSettings().fxaa.getValue(), 2));
        fxaaFilter.getUtils().performOrthographicMatrix(model);
        JGemsSceneUtils.renderModel(model, GL30.GL_TRIANGLES);
        fxaaFilter.unBind();
        this.getFxaaBuffer().unBindFBO();
    }

    //section onRender
    @Override
    public void onRender(FrameTicking frameTicking, Vector2i windowSize) {
        JGemsOpenGLRenderer.getGameUboShader().performUniformBuffer(JGemsResourceManager.globalShaderAssets.Misc, new float[]{JGemsHelper.getScreen().getRenderTicks()});
        if (!APIEventsLauncher.pushEvent(new Events.RenderScenePre(frameTicking, windowSize, this)).isCancelled()) {
            if (this.getSceneData().getCamera() == null) {
                GL30.glClear(GL30.GL_COLOR_BUFFER_BIT);
                this.getGuiRender().onRender(frameTicking);
                this.takeScreenShotIfNeeded(windowSize);
                return;
            }
            if (JGems3D.get().isPaused()) {
                GL30.glClear(GL30.GL_COLOR_BUFFER_BIT);
                this.getGuiRender().onRender(frameTicking);
            } else {
                GL30.glClear(GL30.GL_COLOR_BUFFER_BIT | GL30.GL_DEPTH_BUFFER_BIT | GL30.GL_STENCIL_BUFFER_BIT);
                this.getSceneData().getSceneWorld().getEnvironment().onUpdate(this.getSceneData().getSceneWorld());
                JGems3D.get().getScreen().normalizeViewPort();
                this.renderForwardAndDeferredScenes(frameTicking, windowSize, this.screenModel);
                this.renderTransparentObjects(frameTicking, windowSize);
                this.sceneGluing(this.screenModel);
                this.blurBloomBuffer(this.screenModel, windowSize);
                this.screenBloomHDRCorrection(this.screenModel);
                this.postFXAA(this.screenModel, windowSize);
                this.postProcessing(frameTicking, windowSize);
                this.renderFinalSceneInMainBuffer(this.screenModel);

                this.getInventoryRender().onRender(frameTicking);
                this.getGuiRender().onRender(frameTicking);
            }
        }
        APIEventsLauncher.pushEvent(new Events.RenderScenePost(frameTicking, windowSize, this));
        this.getDearImGuiRender().onRender(windowSize, frameTicking);
        this.takeScreenShotIfNeeded(windowSize);
    }

    // section Post
    private void postProcessing(FrameTicking frameTicking, Vector2i size) {
        this.getFinalizingBuffer().bindFBO();
        GL30.glClear(GL30.GL_COLOR_BUFFER_BIT | GL30.GL_DEPTH_BUFFER_BIT);
        if (!APIEventsLauncher.pushEvent(new Events.RenderPostProcessing(frameTicking, size, this.getFxaaBuffer().getTextureIDByIndex(0), this)).isCancelled()) {
            this.getFxaaBuffer().copyFBOtoFBOColor(this.getFinalizingBuffer().getFrameBufferId(), new int[]{GL30.GL_COLOR_ATTACHMENT0}, size);
        }
        this.getFinalizingBuffer().unBindFBO();
    }

    //section FinalRender
    private void renderFinalSceneInMainBuffer(Model<Format2D> model) {
        JGemsShaderManager imgShader = JGemsResourceManager.globalShaderAssets.gui_image;
        imgShader.bind();
        imgShader.performUniformTexture(new UniformString("texture_sampler"), this.getFinalizingBuffer().getTextureIDByIndex(0), GL30.GL_TEXTURE_2D);
        imgShader.getUtils().performOrthographicMatrix(model);
        JGemsSceneUtils.renderModel(model, GL30.GL_TRIANGLES);
        imgShader.unBind();
    }

    //section RenderForwardDeferred
    public void renderForwardAndDeferredScenes(FrameTicking frameTicking, Vector2i windowSize, Model<Format2D> model) {
        this.deferredGeometry(frameTicking);

        if (this.getSsaoNoiseTexture() != null) {
            GL30.glDisable(GL30.GL_DEPTH_TEST);
            this.calcSSAOValueOnGBuffer(model, windowSize);
            GL30.glEnable(GL30.GL_DEPTH_TEST);
        }

        this.getForwardAndDeferredScenesBuffer().bindFBO();
        GL30.glClear(GL30.GL_COLOR_BUFFER_BIT | GL30.GL_DEPTH_BUFFER_BIT);
        this.deferredLighting(model);
        this.getForwardAndDeferredScenesBuffer().unBindFBO();

        this.getGBuffer().copyFBOtoFBODepth(this.getForwardAndDeferredScenesBuffer().getFrameBufferId(), windowSize);

        this.getForwardAndDeferredScenesBuffer().bindFBO();
        this.renderForwardScene(frameTicking);
        this.getForwardAndDeferredScenesBuffer().unBindFBO();
    }

    //section DeferredGeom
    private void deferredGeometry(FrameTicking frameTicking) {
        this.getGBuffer().bindFBO();
        GL30.glClear(GL30.GL_COLOR_BUFFER_BIT | GL30.GL_DEPTH_BUFFER_BIT);
        SceneRenderBaseContainer.renderSceneRenderSet(frameTicking, this.getSceneRenderBaseContainer().getDeferredRenderSet());
        this.getGBuffer().unBindFBO();
    }

    //section DeferredLighting
    private void deferredLighting(Model<Format2D> model) {
        JGemsShaderManager deferredShader = JGemsResourceManager.globalShaderAssets.world_deferred;
        deferredShader.bind();
        deferredShader.performUniform(new UniformString("view_matrix"), JGemsSceneUtils.getMainCameraViewMatrix());
        deferredShader.performUniformTexture(new UniformString("gPositions"), this.getGBuffer().getTextureIDByIndex(0), GL30.GL_TEXTURE_2D);
        deferredShader.performUniformTexture(new UniformString("gNormals"), this.getGBuffer().getTextureIDByIndex(1), GL30.GL_TEXTURE_2D);
        deferredShader.performUniformTexture(new UniformString("gTexture"), this.getGBuffer().getTextureIDByIndex(2), GL30.GL_TEXTURE_2D);
        deferredShader.performUniformTexture(new UniformString("gEmission"), this.getGBuffer().getTextureIDByIndex(3), GL30.GL_TEXTURE_2D);
        deferredShader.performUniformTexture(new UniformString("gSpecular"), this.getGBuffer().getTextureIDByIndex(4), GL30.GL_TEXTURE_2D);
        deferredShader.performUniformTexture(new UniformString("gMetallic"), this.getGBuffer().getTextureIDByIndex(5), GL30.GL_TEXTURE_2D);
        deferredShader.performUniformTexture(new UniformString("ssaoSampler"), this.getSsaoBuffer().getTextureIDByIndex(0), GL30.GL_TEXTURE_2D);
        deferredShader.performUniform(new UniformString("isSsaoValid"), this.getSsaoBufferTexture() != null);
        deferredShader.getUtils().performShadowsInfo();
        deferredShader.getUtils().performOrthographicMatrix(model);
        JGemsSceneUtils.renderModel(model, GL30.GL_TRIANGLES);
        deferredShader.unBind();
    }

    //section Forward
    private void renderForwardScene(FrameTicking frameTicking) {
        GL30.glEnable(GL30.GL_BLEND);
        GL30.glBlendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);
        SceneRenderBaseContainer.renderSceneRenderSet(frameTicking, this.getSceneRenderBaseContainer().getForwardRenderSet());
        GL30.glDisable(GL30.GL_BLEND);
    }

    //section Transient
    private void renderTransparentObjects(FrameTicking frameTicking, Vector2i windowSize) {
        this.getForwardAndDeferredScenesBuffer().copyFBOtoFBODepth(this.getTransparencySceneBuffer().getFrameBufferId(), windowSize);
        GL30.glDepthMask(false);
        GL30.glEnable(GL30.GL_BLEND);
        GL45.glBlendFunci(0, GL45.GL_ONE, GL45.GL_ONE);
        GL45.glBlendFunci(1, GL45.GL_ZERO, GL45.GL_ONE_MINUS_SRC_COLOR);
        GL45.glBlendFunci(2, GL45.GL_ONE, GL45.GL_ONE);
        GL45.glBlendEquation(GL30.GL_FUNC_ADD);
        this.getTransparencySceneBuffer().bindFBO();
        GL45.glClearBufferfv(GL30.GL_COLOR, 0, new float[]{0.0f, 0.0f, 0.0f, 0.0f});
        GL45.glClearBufferfv(GL30.GL_COLOR, 1, new float[]{1.0f, 1.0f, 1.0f, 1.0f});
        GL45.glClearBufferfv(GL30.GL_COLOR, 2, new float[]{0.0f, 0.0f, 0.0f, 0.0f});
        SceneRenderBaseContainer.renderSceneRenderSet(frameTicking, this.getSceneRenderBaseContainer().getTransparencyRenderSet());
        this.getTransparencySceneBuffer().unBindFBO();
        GL30.glDisable(GL30.GL_BLEND);
        GL30.glDepthMask(true);
    }

    //section SSAO
    private void calcSSAOValueOnGBuffer(Model<Format2D> model, Vector2i windowSize) {
        if (!JGemsSceneGlobalConstants.USE_SSAO) {
            this.getSsaoBuffer().bindFBO();
            GL30.glClearColor(1.0f, 0.0f, 0.0f, 0.0f);
            GL30.glClear(GL30.GL_COLOR_BUFFER_BIT);
            GL30.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
            this.getSsaoBuffer().unBindFBO();
            return;
        }
        JGemsShaderManager ssaoComputeShader = JGemsResourceManager.globalShaderAssets.world_ssao;
        ssaoComputeShader.startComputing();

        ssaoComputeShader.performUniform(new UniformString("ssao_bias"), JGemsSceneGlobalConstants.SSAO_BIAS);
        ssaoComputeShader.performUniform(new UniformString("ssao_radius"), JGemsSceneGlobalConstants.SSAO_RADIUS);
        ssaoComputeShader.performUniform(new UniformString("ssao_range"), JGemsSceneGlobalConstants.SSAO_RANGE);

        ssaoComputeShader.performUniform(new UniformString("noiseScale"), new Vector2f(windowSize).div((float) JGemsSceneGlobalConstants.SSAO_NOISE_SIZE));
        ssaoComputeShader.performUniform(new UniformString("projection_matrix"), JGemsSceneUtils.getMainPerspectiveMatrix());
        ssaoComputeShader.performUniformTexture(new UniformString("gPositions"), this.getGBuffer().getTextureIDByIndex(0), GL30.GL_TEXTURE_2D);
        ssaoComputeShader.performUniformTexture(new UniformString("gNormals"), this.getGBuffer().getTextureIDByIndex(1), GL30.GL_TEXTURE_2D);
        ssaoComputeShader.performUniformTexture(new UniformString("ssaoNoise"), this.getSsaoNoiseTexture().getTextureId(), GL30.GL_TEXTURE_2D);
        ssaoComputeShader.performUniformTexture(new UniformString("ssaoKernel"), this.getSsaoKernelTexture().getTextureId(), GL30.GL_TEXTURE_2D);
        GL43.glBindImageTexture(4, this.getSsaoBufferTexture().getTextureId(), 0, false, 0, GL30.GL_WRITE_ONLY, GL30.GL_RGBA16F);
        ssaoComputeShader.dispatchComputeShader(windowSize.x / 8, windowSize.y / 8, 1, GL43.GL_SHADER_IMAGE_ACCESS_BARRIER_BIT);
        ssaoComputeShader.endComputing();

        JGemsShaderManager ssaoBlur = JGemsResourceManager.globalShaderAssets.blur_ssao;
        this.getSsaoBuffer().bindFBO();
        ssaoBlur.bind();
        ssaoBlur.performUniformTexture(new UniformString("texture_sampler"), this.getSsaoBufferTexture().getTextureId(), GL30.GL_TEXTURE_2D);
        ssaoBlur.getUtils().performOrthographicMatrix(model);
        JGemsSceneUtils.renderModel(model, GL30.GL_TRIANGLES);
        ssaoBlur.unBind();
        this.getSsaoBuffer().unBindFBO();
    }

    //section BlurBloom
    private void blurBloomBuffer(Model<Format2D> model, Vector2i windowSize) {
        if (!JGemsSceneGlobalConstants.USE_BLOOM || JGems3D.get().getGameSettings().bloom.getValue() == 0) {
            this.getBloomBlurredBuffer().bindFBO();
            GL30.glClear(GL30.GL_COLOR_BUFFER_BIT);
            this.getBloomBlurredBuffer().unBindFBO();
            return;
        }
        JGemsShaderManager blurShader = JGemsResourceManager.globalShaderAssets.blur13;
        FBOTexture2DProgram startFbo = this.getSceneGluingBuffer();
        int startBinding = 1;
        int steps = 6;

        blurShader.bind();
        blurShader.performUniform(new UniformString("resolution"), new Vector2f(windowSize).div(2.0f));
        for (int i = 0; i < steps; i++) {
            this.getBloomBlurredBuffer().bindFBO();
            blurShader.performUniformTexture(new UniformString("texture_sampler"), startFbo.getTextureIDByIndex(startBinding), GL30.GL_TEXTURE_2D, 0);
            blurShader.performUniform(new UniformString("direction"), i % 2 == 0 ? new Vector2f(1.0f, 0.0f) : new Vector2f(0.0f, 1.0f));
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

    private void remakeScreenModel() {
        if (this.screenModel != null) {
            this.screenModel.clean();
            this.screenModel = JGemsSceneUtils.createScreenModel();
        }
    }

    //section WinResize
    @Override
    public void onWindowResize(Vector2i windowSize) {
        this.remakeScreenModel();
        this.recreateResources(windowSize);
        this.getDearImGuiRender().onResize(windowSize);
    }

    public void takeScreenShot() {
        this.wantToTakeScreenshot = true;
    }

    public LightManager getLightManager() {
        return this.getSceneData().getSceneWorld().getEnvironment().getLightManager();
    }

    public ShadowManager getShadowScene() {
        return this.getSceneData().getSceneWorld().getEnvironment().getShadowManager();
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
            scale = JGemsHelper.MATH.lerp(0.1f, 1.0f, scale * scale);
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
            this.forwardRenderSet = new TreeSet<>(Comparator.comparingInt(SceneRenderBase::getRenderOrder).thenComparingInt(System::identityHashCode));
            this.deferredRenderSet = new TreeSet<>(Comparator.comparingInt(SceneRenderBase::getRenderOrder).thenComparingInt(System::identityHashCode));
            this.transparencyRenderSet = new TreeSet<>(Comparator.comparingInt(SceneRenderBase::getRenderOrder).thenComparingInt(System::identityHashCode));
            this.guiRenderSet = new TreeSet<>(Comparator.comparingInt(SceneRenderBase::getRenderOrder).thenComparingInt(System::identityHashCode));
            this.inventoryRenderSet = new TreeSet<>(Comparator.comparingInt(SceneRenderBase::getRenderOrder).thenComparingInt(System::identityHashCode));
        }

        public static void renderSceneRenderSet(FrameTicking frameTicking, Set<SceneRenderBase> sceneRenderBases) {
            sceneRenderBases.forEach(e -> e.onBaseRender(frameTicking));
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