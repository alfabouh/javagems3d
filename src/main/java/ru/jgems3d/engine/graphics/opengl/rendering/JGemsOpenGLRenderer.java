package ru.jgems3d.engine.graphics.opengl.rendering;

import org.joml.*;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL43;
import org.lwjgl.opengl.GL45;
import org.lwjgl.system.MemoryUtil;
import ru.jgems3d.engine.JGems3D;
import ru.jgems3d.engine.graphics.opengl.rendering.programs.textures.TextureProgram;
import ru.jgems3d.engine.graphics.opengl.rendering.scene.SceneData;
import ru.jgems3d.engine.graphics.opengl.dear_imgui.DIMGuiRenderJGems;
import ru.jgems3d.engine.graphics.opengl.environment.Environment;
import ru.jgems3d.engine.graphics.opengl.environment.shadow.ShadowScene;
import ru.jgems3d.engine.graphics.opengl.rendering.scene.SceneRenderBase;
import ru.jgems3d.engine.graphics.opengl.rendering.scene.groups.*;
import ru.jgems3d.engine.graphics.opengl.rendering.debug.GlobalRenderDebugConstants;
import ru.jgems3d.engine.graphics.opengl.rendering.items.IModeledSceneObject;
import ru.jgems3d.engine.graphics.opengl.rendering.items.objects.LiquidObject;
import ru.jgems3d.engine.graphics.opengl.rendering.programs.fbo.FBOTexture2DProgram;
import ru.jgems3d.engine.graphics.opengl.rendering.utils.JGemsSceneUtils;
import ru.jgems3d.engine.graphics.opengl.world.SceneWorld;
import ru.jgems3d.engine.graphics.opengl.camera.ICamera;
import ru.jgems3d.engine.physics.world.triggers.liquids.base.Liquid;
import ru.jgems3d.engine.JGemsHelper;
import ru.jgems3d.engine.system.resources.manager.JGemsResourceManager;
import ru.jgems3d.engine.system.resources.assets.models.Model;
import ru.jgems3d.engine.system.resources.assets.models.basic.MeshHelper;
import ru.jgems3d.engine.system.resources.assets.models.formats.Format2D;
import ru.jgems3d.engine.system.resources.assets.models.formats.Format3D;
import ru.jgems3d.engine.system.resources.assets.shaders.manager.JGemsShaderManager;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.Math;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public final class JGemsOpenGLRenderer {
    public static final int SSAO_NOISE_SIZE = 4;

    private final DIMGuiRenderJGems dearImGuiRender;
    private final GuiRender guiRender;
    private final InventoryRender inventoryRender;
    private final SceneData sceneData;
    private final FBOTexture2DProgram fboPsx;
    private final FBOTexture2DProgram fboBlur;
    private final FBOTexture2DProgram sceneFbo;
    private final FBOTexture2DProgram gBuffer;
    private final FBOTexture2DProgram ssaoBuffer;
    private final FBOTexture2DProgram finalRenderedSceneFbo;

    private final FBOTexture2DProgram transparencyFBO;

    private final ShadowScene shadowScene;
    private boolean wantsToTakeScreenshot;
    private List<SceneRenderBase> sceneRenderBases_forward;
    private List<SceneRenderBase> sceneRenderBases_deferred;
    private TextureProgram ssaoNoiseTexture;
    private TextureProgram ssaoKernelTexture;
    private TextureProgram ssaoBufferTexture;

    public JGemsOpenGLRenderer(SceneData sceneData) {
        this.sceneRenderBases_deferred = new ArrayList<>();
        this.sceneRenderBases_forward = new ArrayList<>();

        this.guiRender = new GuiRender(sceneData);
        this.inventoryRender = new InventoryRender(sceneData);

        this.sceneData = sceneData;

        this.shadowScene = new ShadowScene(this.getSceneData().getSceneWorld());

        this.transparencyFBO = new FBOTexture2DProgram(true);

        this.fboBlur = new FBOTexture2DProgram(true);
        this.sceneFbo = new FBOTexture2DProgram(true);
        this.fboPsx = new FBOTexture2DProgram(true);
        this.gBuffer = new FBOTexture2DProgram(true);
        this.ssaoBuffer = new FBOTexture2DProgram(true);
        this.finalRenderedSceneFbo = new FBOTexture2DProgram(true);

        this.createResources(this.getWindowDimensions());

        this.dearImGuiRender = new DIMGuiRenderJGems(JGems3D.get().getScreen().getWindow(), JGemsResourceManager.getGlobalGameResources().getResourceCache());
        this.wantsToTakeScreenshot = false;
    }

    // section ResourcesSetup
    public void createResources(Vector2i dim) {
        this.clearResources();

        FBOTexture2DProgram.FBOTextureInfo[] transparencyFBOs = new FBOTexture2DProgram.FBOTextureInfo[]
                {
                        new FBOTexture2DProgram.FBOTextureInfo(GL30.GL_COLOR_ATTACHMENT0, GL43.GL_RGBA16F, GL30.GL_RGBA),
                        new FBOTexture2DProgram.FBOTextureInfo(GL30.GL_COLOR_ATTACHMENT1, GL43.GL_R8, GL30.GL_RED)
                };
        this.transparencyFBO.createFrameBuffer2DTexture(dim, transparencyFBOs, true, GL30.GL_NEAREST, GL30.GL_COMPARE_REF_TO_TEXTURE, GL30.GL_LESS, GL30.GL_CLAMP_TO_EDGE, null);

        FBOTexture2DProgram.FBOTextureInfo[] psxFBOs = new FBOTexture2DProgram.FBOTextureInfo[]
                {
                        new FBOTexture2DProgram.FBOTextureInfo(GL30.GL_COLOR_ATTACHMENT0, GL30.GL_RGBA, GL30.GL_RGBA),
                        new FBOTexture2DProgram.FBOTextureInfo(GL30.GL_COLOR_ATTACHMENT0, GL30.GL_RGBA, GL30.GL_RGBA),
                        new FBOTexture2DProgram.FBOTextureInfo(GL30.GL_COLOR_ATTACHMENT0, GL30.GL_RGBA, GL30.GL_RGBA)
                };
        this.fboPsx.createFrameBuffer2DTexture(dim, psxFBOs, true, GL30.GL_NEAREST, GL30.GL_COMPARE_REF_TO_TEXTURE, GL30.GL_LESS, GL30.GL_CLAMP_TO_BORDER, null);

        FBOTexture2DProgram.FBOTextureInfo[] blurFBOs = new FBOTexture2DProgram.FBOTextureInfo[]
                {
                        new FBOTexture2DProgram.FBOTextureInfo(GL30.GL_COLOR_ATTACHMENT0, GL30.GL_RGB, GL30.GL_RGB)
                };
        this.fboBlur.createFrameBuffer2DTexture(dim, blurFBOs, false, GL30.GL_NEAREST, GL30.GL_COMPARE_REF_TO_TEXTURE, GL30.GL_LESS, GL30.GL_CLAMP_TO_EDGE, null);

        FBOTexture2DProgram.FBOTextureInfo[] sceneFBOs = new FBOTexture2DProgram.FBOTextureInfo[]
                {
                        new FBOTexture2DProgram.FBOTextureInfo(GL30.GL_COLOR_ATTACHMENT0, GL43.GL_RGB16F, GL30.GL_RGB),
                        new FBOTexture2DProgram.FBOTextureInfo(GL30.GL_COLOR_ATTACHMENT1, GL43.GL_RGB16F, GL30.GL_RGB)
                };
        this.sceneFbo.createFrameBuffer2DTexture(dim, sceneFBOs, true, GL30.GL_NEAREST, GL30.GL_COMPARE_REF_TO_TEXTURE, GL30.GL_LESS, GL30.GL_CLAMP_TO_EDGE, null);

        FBOTexture2DProgram.FBOTextureInfo[] gBufferFBOs = new FBOTexture2DProgram.FBOTextureInfo[]
                {
                        new FBOTexture2DProgram.FBOTextureInfo(GL30.GL_COLOR_ATTACHMENT0, GL43.GL_RGB32F, GL30.GL_RGB),
                        new FBOTexture2DProgram.FBOTextureInfo(GL30.GL_COLOR_ATTACHMENT1, GL43.GL_RGB32F, GL30.GL_RGB),
                        new FBOTexture2DProgram.FBOTextureInfo(GL30.GL_COLOR_ATTACHMENT2, GL43.GL_RGBA, GL30.GL_RGBA),
                        new FBOTexture2DProgram.FBOTextureInfo(GL30.GL_COLOR_ATTACHMENT3, GL43.GL_RGB, GL30.GL_RGB),
                        new FBOTexture2DProgram.FBOTextureInfo(GL30.GL_COLOR_ATTACHMENT4, GL43.GL_RGB, GL30.GL_RGB),
                        new FBOTexture2DProgram.FBOTextureInfo(GL30.GL_COLOR_ATTACHMENT5, GL43.GL_RGB, GL30.GL_RGB)
                };
        this.gBuffer.createFrameBuffer2DTexture(new Vector2i(dim), gBufferFBOs, true, GL30.GL_NEAREST, GL30.GL_COMPARE_REF_TO_TEXTURE, GL30.GL_LESS, GL30.GL_CLAMP_TO_EDGE, null);

        FBOTexture2DProgram.FBOTextureInfo[] finalRenderedSceneFBOs = new FBOTexture2DProgram.FBOTextureInfo[]
                {
                        new FBOTexture2DProgram.FBOTextureInfo(GL30.GL_COLOR_ATTACHMENT0, GL30.GL_RGB, GL30.GL_RGB)
                };
        this.finalRenderedSceneFbo.createFrameBuffer2DTexture(dim, finalRenderedSceneFBOs, false, GL30.GL_NEAREST, GL30.GL_COMPARE_REF_TO_TEXTURE, GL30.GL_LESS, GL30.GL_CLAMP_TO_EDGE, null);

        FBOTexture2DProgram.FBOTextureInfo[] ssaoRenderedSceneFBOs = new FBOTexture2DProgram.FBOTextureInfo[]
                {
                        new FBOTexture2DProgram.FBOTextureInfo(GL30.GL_COLOR_ATTACHMENT0, GL30.GL_RGB, GL30.GL_RGB)
                };
        this.ssaoBuffer.createFrameBuffer2DTexture(dim, ssaoRenderedSceneFBOs, true, GL30.GL_LINEAR, GL30.GL_COMPARE_REF_TO_TEXTURE, GL30.GL_LESS, GL30.GL_CLAMP_TO_EDGE, null);

        this.createSSAOResources(this.getSSAOParams());
    }

    private void createSSAOResources(Vector3i ssaoParams) {
        if (ssaoParams == null) {
            return;
        }
        this.ssaoKernelTexture = this.calcSSAOKernel(ssaoParams.z * ssaoParams.z);
        this.ssaoNoiseTexture = this.calcSSAONoise(JGemsOpenGLRenderer.SSAO_NOISE_SIZE * JGemsOpenGLRenderer.SSAO_NOISE_SIZE);
        this.ssaoBufferTexture = this.createSSAOBuffer(new Vector2i(ssaoParams.x, ssaoParams.y));
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

    private TextureProgram createSSAOBuffer(Vector2i dim) {
        TextureProgram textureProgram = new TextureProgram();
        textureProgram.createTexture(dim, GL30.GL_RGBA16F, GL30.GL_RGBA, GL30.GL_LINEAR, GL30.GL_LINEAR, GL30.GL_NONE, GL30.GL_LESS, GL30.GL_CLAMP_TO_EDGE, GL30.GL_CLAMP_TO_EDGE, null);
        return textureProgram;
    }

    public void takeScreenShot() {
        this.wantsToTakeScreenshot = true;
    }

    private void fillScene() {
        this.sceneRenderBases_forward.add(new WorldForwardRender(this.getSceneData()));
        this.sceneRenderBases_forward.add(new WorldTransparentRender(this.getSceneData()));
        this.sceneRenderBases_forward.add(new SkyRender(this.getSceneData()));
        this.sceneRenderBases_forward.add(new DebugRender(this.getSceneData()));

        this.sceneRenderBases_deferred.add(new LiquidsRender(this.getSceneData()));
        this.sceneRenderBases_deferred.add(new WorldDeferredRender(this.getSceneData()));
        this.sceneRenderBases_deferred.add(new ParticlesRender(this.getSceneData()));
    }

    public void onStartRender() {
        this.fillScene();
        this.sceneRenderBases_forward = this.sceneRenderBases_forward.stream().sorted(Comparator.comparingInt(SceneRenderBase::getRenderOrder)).collect(Collectors.toList());
        this.sceneRenderBases_deferred = this.sceneRenderBases_deferred.stream().sorted(Comparator.comparingInt(SceneRenderBase::getRenderOrder)).collect(Collectors.toList());

        this.sceneRenderBases_forward.forEach(SceneRenderBase::onStartRender);
        this.sceneRenderBases_deferred.forEach(SceneRenderBase::onStartRender);

        this.inventoryRender.onStartRender();
        this.guiRender.onStartRender();
    }

    public void onStopRender() {
        this.sceneRenderBases_forward.forEach(SceneRenderBase::onStopRender);
        this.sceneRenderBases_deferred.forEach(SceneRenderBase::onStopRender);

        this.inventoryRender.onStopRender();
        this.guiRender.onStopRender();

        this.getDearImguiRender().cleanUp();
        this.clearResources();
    }

    private void clearResources() {
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

        this.getTransparencyFBO().clearFBO();
        this.getSsaoBuffer().clearFBO();
        this.getFboBlur().clearFBO();
        this.getSceneFbo().clearFBO();
        this.getFboPsx().clearFBO();
        this.getGBuffer().clearFBO();
        this.getFinalRenderedSceneFbo().clearFBO();
    }

    // section Render
    public void onRender(float partialTicks) {
        if (this.getSceneData().getCamera() == null) {
            GL30.glClear(GL30.GL_COLOR_BUFFER_BIT);
            this.guiRender.onRender(partialTicks);
            return;
        }

        try (Model<Format2D> model = MeshHelper.generatePlane2DModelInverted(new Vector2f(0.0f), new Vector2f(this.getWindowDimensions().x, this.getWindowDimensions().y), 0)) {
            this.updateUBOs();
            this.getSceneData().getSceneWorld().getEnvironment().onUpdate(this.getSceneData().getSceneWorld());

            if (this.checkPlayerCameraInWater()) {
                this.getSceneData().getSceneWorld().getEnvironment().setFog(this.getSceneData().getSceneWorld().getEnvironment().getWaterFog());
            } else {
                this.getSceneData().getSceneWorld().getEnvironment().setFog(this.getSceneData().getSceneWorld().getEnvironment().getWorldFog());
            }

            if (JGems3D.get().isPaused()) {
                GL30.glClear(GL30.GL_COLOR_BUFFER_BIT);
                this.guiRender.onRender(partialTicks);
            } else {
                GL30.glClear(GL30.GL_COLOR_BUFFER_BIT | GL30.GL_DEPTH_BUFFER_BIT | GL30.GL_STENCIL_BUFFER_BIT);
                this.renderScene(partialTicks, model);
                this.bloomPostProcessing(partialTicks, model);
                this.renderSceneWithBloomAndHDR(partialTicks, model);
                this.postFXAA(model);

                this.renderFinalScene(model);
                //TODO
                this.renderTransparentElements();

                this.inventoryRender.onRender(partialTicks);
                this.guiRender.onRender(partialTicks);
            }
        }

        this.getDearImguiRender().render(partialTicks);

        if (this.wantsToTakeScreenshot) {
            JGemsHelper.getLogger().log("Took screenshot!");
            this.writeBufferInFile(this.getWindowDimensions());
            this.wantsToTakeScreenshot = false;
        }
    }

    // section Transparency_OI
    private void renderTransparentElements() {
        this.getGBuffer().copyFBOtoFBODepth(this.getTransparencyFBO().getFrameBufferId(), this.getWindowDimensions());

        GL30.glEnable(GL30.GL_DEPTH_TEST);
        GL30.glDepthMask(false);
        GL30.glEnable(GL30.GL_BLEND);
        GL45.glBlendFunci(0, GL45.GL_ONE, GL45.GL_ONE);
        GL45.glBlendFunci(1, GL45.GL_ZERO, GL45.GL_ONE_MINUS_SRC_COLOR);
        GL45.glBlendEquation(GL30.GL_FUNC_ADD);

        this.getTransparencyFBO().bindFBO();
        GL45.glClearBufferfv(GL30.GL_COLOR, 0, new float[] {0.0f, 0.0f, 0.0f, 0.0f});
        GL45.glClearBufferfv(GL30.GL_COLOR, 1, new float[] {1.0f, 1.0f, 1.0f, 1.0f});
        List<IModeledSceneObject> modeledSceneObjects = this.getSceneData().getSceneWorld().getModeledSceneEntities().stream().filter(e -> e.getMeshRenderData().getRenderAttributes().isHasTransparency()).collect(Collectors.toList());
        for (IModeledSceneObject modeledSceneObject : modeledSceneObjects) {
            this.getOITShader().bind();
            this.getOITShader().getUtils().performPerspectiveMatrix();
            this.getOITShader().getUtils().performViewAndModelMatricesSeparately(modeledSceneObject.getModel());

            Vector3f vector3f = modeledSceneObject.getModel().getFormat().getPosition();
            Vector3f vector3f2 = modeledSceneObject.getModel().getFormat().getRotation();

            this.getOITShader().performUniform("color", new Vector4f(new Vector3f((vector3f.x % 10) * 0.1f, (vector3f.y % 10) * 0.1f, (vector3f.z % 10) * 0.1f), 0.5f));

            JGemsSceneUtils.renderModel(modeledSceneObject.getModel(), GL30.GL_TRIANGLES);

            this.getOITShader().unBind();
        }
        this.getTransparencyFBO().unBindFBO();

        GL30.glDisable(GL30.GL_BLEND);
        GL30.glDepthMask(true);
        GL30.glDisable(GL30.GL_DEPTH_TEST);
    }

    // section FinalScene
    private void renderFinalScene(Model<Format2D> model) {
        JGemsShaderManager imgShader = JGemsResourceManager.globalShaderAssets.gui_image;
        imgShader.bind();
        imgShader.performUniform("texture_sampler", 0);
        GL30.glActiveTexture(GL30.GL_TEXTURE0);
        this.getFinalRenderedSceneFbo().bindTexture(0);
        imgShader.getUtils().performOrthographicMatrix(model);
        JGemsSceneUtils.renderModel(model, GL30.GL_TRIANGLES);
        imgShader.unBind();
    }

    // section GeometryPass
    private void geometryPass(float partialTicks) {
        this.getGBuffer().bindFBO();
        GL30.glClear(GL30.GL_COLOR_BUFFER_BIT | GL30.GL_DEPTH_BUFFER_BIT | GL30.GL_STENCIL_BUFFER_BIT);
        for (SceneRenderBase sceneRenderBase : this.sceneRenderBases_deferred) {
            sceneRenderBase.onRender(partialTicks);
        }
        this.getGBuffer().unBindFBO();
    }

    // section SSAO
    private void renderSSAO(Model<Format2D> model, Vector3i ssaoParams) {
        if (ssaoParams == null) {
            return;
        }
        this.getSSAOShader().startComputing();
        this.getSSAOShader().performUniform("noiseScale", new Vector2f(this.getWindowDimensions()).div((float) JGemsOpenGLRenderer.SSAO_NOISE_SIZE));
        this.getSSAOShader().performUniform("projection_matrix", JGemsSceneUtils.getMainPerspectiveMatrix());
        this.getSSAOShader().performUniform("gPositions", 0);
        this.getSSAOShader().performUniform("gNormals", 1);
        this.getSSAOShader().performUniform("ssaoNoise", 2);
        this.getSSAOShader().performUniform("ssaoKernel", 3);

        GL30.glActiveTexture(GL30.GL_TEXTURE0);
        this.getGBuffer().bindTexture(0);
        GL30.glActiveTexture(GL30.GL_TEXTURE1);
        this.getGBuffer().bindTexture(1);
        GL30.glActiveTexture(GL30.GL_TEXTURE2);
        this.getSsaoNoiseTexture().bindTexture(GL30.GL_TEXTURE_2D);
        GL30.glActiveTexture(GL30.GL_TEXTURE3);
        this.getSsaoKernelTexture().bindTexture(GL30.GL_TEXTURE_2D);

        GL43.glBindImageTexture(4, this.getSsaoBufferTexture().getTextureId(), 0, false, 0, GL30.GL_WRITE_ONLY, GL30.GL_RGBA16F);
        this.getSSAOShader().dispatchComputeShader(this.getWindowDimensions().x / 16, this.getWindowDimensions().y / 16, 1, GL43.GL_SHADER_IMAGE_ACCESS_BARRIER_BIT);
        this.getSSAOShader().endComputing();

        this.getSsaoBuffer().bindFBO();
        JGemsShaderManager jGemsShaderManager = JGemsResourceManager.globalShaderAssets.blur_box;
        jGemsShaderManager.bind();
        jGemsShaderManager.performUniform("blur", 3.0f);
        jGemsShaderManager.performUniform("texture_sampler", 0);
        jGemsShaderManager.getUtils().performOrthographicMatrix(model);
        GL30.glActiveTexture(GL30.GL_TEXTURE0);
        this.getSsaoBufferTexture().bindTexture(GL30.GL_TEXTURE_2D);
        JGemsSceneUtils.renderModel(model, GL30.GL_TRIANGLES);
        jGemsShaderManager.unBind();
        this.getSsaoBuffer().unBindFBO();
    }

    private Vector3i getSSAOParams() {
        if (this.getWindowDimensions().y <= 0) {
            return null;
        }
        int ssaoSetting = JGems3D.get().getGameSettings().ssao.getValue();
        float aspect = (float) this.getWindowDimensions().y / this.getWindowDimensions().x;
        Vector3i dim;
        switch (ssaoSetting) {
            case 1: {
                dim = new Vector3i((int) (this.getWindowDimensions().x * 0.5f), (int) (this.getWindowDimensions().y * 0.5f), 4);
                break;
            }
            case 2: {
                dim = new Vector3i((int) (this.getWindowDimensions().x * 0.5f), (int) (this.getWindowDimensions().y * 0.5f), 6);
                break;
            }
            case 3: {
                dim = new Vector3i((int) (this.getWindowDimensions().x * 0.5f), (int) (this.getWindowDimensions().y * 0.5f), 8);
                break;
            }
            case 0:
            default: {
               return null;
            }
        }
        return dim;
    }

    // section LightPass
    private void lightPass(float partialTicks, Model<Format2D> model) {
      this.getDeferredWorldShader().bind();
      this.getDeferredWorldShader().performUniform("view_matrix", JGemsSceneUtils.getMainCameraViewMatrix());
      this.getDeferredWorldShader().performUniform("gPositions", 0);
      this.getDeferredWorldShader().performUniform("gNormals", 1);
      this.getDeferredWorldShader().performUniform("gTexture", 2);
      this.getDeferredWorldShader().performUniform("gEmission", 3);
      this.getDeferredWorldShader().performUniform("gSpecular", 4);
      this.getDeferredWorldShader().performUniform("gMetallic", 5);
      this.getDeferredWorldShader().performUniform("ssaoSampler", 6);
      this.getDeferredWorldShader().performUniform("isSsaoValid", this.getSsaoBufferTexture() != null);
      this.getDeferredWorldShader().getUtils().performShadowsInfo();

      GL30.glActiveTexture(GL30.GL_TEXTURE0);
      this.getGBuffer().bindTexture(0);
      GL30.glActiveTexture(GL30.GL_TEXTURE1);
      this.getGBuffer().bindTexture(1);
      GL30.glActiveTexture(GL30.GL_TEXTURE2);
      this.getGBuffer().bindTexture(2);
      GL30.glActiveTexture(GL30.GL_TEXTURE3);
      this.getGBuffer().bindTexture(3);
      GL30.glActiveTexture(GL30.GL_TEXTURE4);
      this.getGBuffer().bindTexture(4);
      GL30.glActiveTexture(GL30.GL_TEXTURE5);
      this.getGBuffer().bindTexture(5);
      GL30.glActiveTexture(GL30.GL_TEXTURE6);
      this.getSsaoBuffer().bindTexture(0);

      this.getDeferredWorldShader().getUtils().performOrthographicMatrix(model);
      JGemsSceneUtils.renderModel(model, GL30.GL_TRIANGLES);
      this.getDeferredWorldShader().unBind();
    }

    // section RenderScene
    public void renderScene(float partialTicks, Model<Format2D> model) {
        this.getShadowScene().renderAllModelsInShadowMap(this.getModelsToRenderInShadows(this.getSceneData().getSceneWorld()));
        JGems3D.get().getScreen().normalizeViewPort();

        GL30.glEnable(GL30.GL_DEPTH_TEST);
        this.geometryPass(partialTicks);

        GL30.glDisable(GL30.GL_DEPTH_TEST);
        this.renderSSAO(model, this.getSSAOParams());
        GL30.glEnable(GL30.GL_DEPTH_TEST);

        this.getSceneFbo().bindFBO();
        GL30.glClear(GL30.GL_COLOR_BUFFER_BIT | GL30.GL_DEPTH_BUFFER_BIT);
        this.lightPass(partialTicks, model);
        this.getSceneFbo().unBindFBO();

        this.getGBuffer().copyFBOtoFBODepth(this.getSceneFbo().getFrameBufferId(), this.getWindowDimensions());

        this.getSceneFbo().bindFBO();
        this.renderForwardScene(partialTicks);
        this.getSceneFbo().unBindFBO();

        GL30.glDisable(GL30.GL_DEPTH_TEST);
    }

    private List<Model<Format3D>> getModelsToRenderInShadows(SceneWorld sceneWorld) {
        List<Model<Format3D>> models = new ArrayList<>();
        models.addAll(sceneWorld.getModeledSceneEntities().stream().filter(e -> e.hasRender() && e.getMeshRenderData().getRenderAttributes().isShadowCaster()).map(IModeledSceneObject::getModel).collect(Collectors.toList()));
        models.addAll(sceneWorld.getLiquids().stream().map(LiquidObject::getModel).collect(Collectors.toList()));
        return models;
    }

    // section ForwardRender
    private void renderForwardScene(float partialTicks) {
        GL30.glEnable(GL30.GL_BLEND);
        GL30.glBlendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);
        for (SceneRenderBase sceneRenderBase : this.sceneRenderBases_forward) {
            sceneRenderBase.onRender(partialTicks);
        }
        GL30.glDisable(GL30.GL_BLEND);
    }

    // section FXAA
    public void postFXAA(Model<Format2D> model) {
        this.getFinalRenderedSceneFbo().bindFBO();
        this.getFXAAShader().bind();
        this.getFXAAShader().performUniform("resolution", new Vector2f(this.getWindowDimensions().x, this.getWindowDimensions().y));
        this.getFXAAShader().performUniform("texture_sampler", 0);
        this.getFXAAShader().performUniform("FXAA_SPAN_MAX", (float) Math.pow(JGems3D.get().getGameSettings().fxaa.getValue(), 2));
        this.getFXAAShader().getUtils().performOrthographicMatrix(model);
        GL30.glActiveTexture(GL30.GL_TEXTURE0);
        this.getFinalRenderedSceneFbo().bindTexture(0);
        JGemsSceneUtils.renderModel(model, GL30.GL_TRIANGLES);
        this.getFXAAShader().unBind();
        this.getFinalRenderedSceneFbo().unBindFBO();
    }

    // section HDR
    public void renderSceneWithBloomAndHDR(float partialTicks, Model<Format2D> model) {
        GL30.glEnable(GL30.GL_BLEND);
        GL30.glBlendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);

        this.getFinalRenderedSceneFbo().bindFBO();
        GL30.glClear(GL30.GL_COLOR_BUFFER_BIT | GL30.GL_DEPTH_BUFFER_BIT);

        this.getPostProcessingShader().bind();
        this.getPostProcessingShader().performUniform("texture_sampler", 0);
        this.getPostProcessingShader().performUniform("blur_sampler", 1);

        GL30.glActiveTexture(GL30.GL_TEXTURE0);
        this.getSceneFbo().bindTexture(0);
        GL30.glActiveTexture(GL30.GL_TEXTURE1);
        this.getFboBlur().bindTexture(0);

        this.getPostProcessingShader().performUniform("accumulated_alpha", 2);
        this.getPostProcessingShader().performUniform("reveal_alpha", 3);

        GL30.glActiveTexture(GL30.GL_TEXTURE2);
        this.getTransparencyFBO().bindTexture(0);
        GL30.glActiveTexture(GL30.GL_TEXTURE3);
        this.getTransparencyFBO().bindTexture(1);

        this.getPostProcessingShader().getUtils().performOrthographicMatrix(model);
        JGemsSceneUtils.renderModel(model, GL30.GL_TRIANGLES);
        this.getPostProcessingShader().unBind();

        this.getFinalRenderedSceneFbo().unBindFBO();
        GL30.glDisable(GL30.GL_BLEND);
    }

    // section BLOOM
    private void bloomPostProcessing(float partialTicks, Model<Format2D> model) {
        if (JGems3D.get().getGameSettings().bloom.getValue() == 0) {
            this.getFboBlur().bindFBO();
            GL30.glClear(GL30.GL_COLOR_BUFFER_BIT);
            this.getFboBlur().unBindFBO();
            return;
        }
        FBOTexture2DProgram startFbo = this.getSceneFbo();
        int startBinding = 1;
        int steps = 6;

        // this.getFboBlur().bindFBO();
        // JGemsShaderManager jGemsShaderManager = JGemsResourceManager.globalShaderAssets.blur_box;
        // jGemsShaderManager.bind();
        // jGemsShaderManager.performUniform("blur", 3.0f);
        // jGemsShaderManager.performUniform("texture_sampler", 0);
        // jGemsShaderManager.getUtils().performOrthographicMatrix(model);
        // GL30.glActiveTexture(GL30.GL_TEXTURE0);
        // startFbo.bindTexture(1);
        // JGemsSceneUtils.renderModel(model, GL30.GL_TRIANGLES);
        // jGemsShaderManager.unBind();
        // this.getFboBlur().unBindFBO();

        this.getBlurShader().bind();
        this.getBlurShader().performUniform("resolution", new Vector2f(this.getWindowDimensions().x, this.getWindowDimensions().y).div(2.0f));
        for (int i = 0; i < steps; i++) {
            this.getFboBlur().bindFBO();
            GL30.glActiveTexture(GL30.GL_TEXTURE0);
            startFbo.bindTexture(startBinding);
            this.getBlurShader().performUniform("texture_sampler", 0);
            this.getBlurShader().performUniform("direction", i % 2 == 0 ? new Vector2f(1.0f, 0.0f) : new Vector2f(0.0f, 1.0f));
            this.getBlurShader().getUtils().performOrthographicMatrix(model);
            JGemsSceneUtils.renderModel(model, GL30.GL_TRIANGLES);
            this.getFboBlur().unBindFBO();
            startFbo = this.getFboBlur();
            startBinding = 0;
        }
        this.getBlurShader().unBind();
    }

    public void onWindowResize(Vector2i dim) {
        this.createResources(dim);
        this.getDearImguiRender().onResize(dim);
    }

    private void updateUBOs() {
        JGemsOpenGLRenderer.getGameUboShader().performUniformBuffer(JGemsResourceManager.globalShaderAssets.Misc, new float[]{JGems3D.get().getScreen().getRenderTicks()});
        Environment environment = this.getSceneData().getSceneWorld().getEnvironment();
        FloatBuffer value1Buffer = MemoryUtil.memAllocFloat(4);
        value1Buffer.put(!GlobalRenderDebugConstants.FULL_BRIGHT ? environment.getFog().getDensity() : 0.0f);
        value1Buffer.put(environment.getFog().getColor().x);
        value1Buffer.put(environment.getFog().getColor().y);
        value1Buffer.put(environment.getFog().getColor().z);
        value1Buffer.flip();
        JGemsOpenGLRenderer.getGameUboShader().performUniformBuffer(JGemsResourceManager.globalShaderAssets.Fog, value1Buffer);
        MemoryUtil.memFree(value1Buffer);
    }

    private boolean checkPlayerCameraInWater() {
        for (Liquid liquid : this.getSceneData().getSceneWorld().getLiquids().stream().map(LiquidObject::getLiquid).collect(Collectors.toList())) {
            ICamera camera = this.getSceneData().getCamera();
            Vector3f left = new Vector3f(liquid.getSimpleTriggerZone().getZone().getLocation()).sub(liquid.getSimpleTriggerZone().getZone().getSize().mul(0.5f));
            Vector3f right = new Vector3f(liquid.getSimpleTriggerZone().getZone().getLocation()).add(liquid.getSimpleTriggerZone().getZone().getSize().mul(0.5f));
            if (camera.getCamPosition().x >= left.x && camera.getCamPosition().y >= left.y && camera.getCamPosition().z >= left.z && camera.getCamPosition().x <= right.x && camera.getCamPosition().y <= right.y && camera.getCamPosition().z <= right.z) {
                return true;
            }
        }
        return false;
    }

    private void writeBufferInFile(Vector2i dim) {
        int w = dim.x;
        int h = dim.y;
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
                    image.setRGB(x, dim.y - y - 1, rgb);
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

    public TextureProgram getSsaoBufferTexture() {
        return this.ssaoBufferTexture;
    }

    public TextureProgram getSsaoKernelTexture() {
        return this.ssaoKernelTexture;
    }

    public TextureProgram getSsaoNoiseTexture() {
        return this.ssaoNoiseTexture;
    }

    public static JGemsShaderManager getGameUboShader() {
        return JGemsResourceManager.globalShaderAssets.gameUbo;
    }

    public FBOTexture2DProgram getSceneFbo() {
        return this.sceneFbo;
    }

    public FBOTexture2DProgram getGBuffer() {
        return this.gBuffer;
    }

    public FBOTexture2DProgram getFinalRenderedSceneFbo() {
        return this.finalRenderedSceneFbo;
    }

    public FBOTexture2DProgram getFboPsx() {
        return this.fboPsx;
    }

    public FBOTexture2DProgram getSsaoBuffer() {
        return this.ssaoBuffer;
    }

    public FBOTexture2DProgram getTransparencyFBO() {
        return this.transparencyFBO;
    }

    public FBOTexture2DProgram getFboBlur() {
        return this.fboBlur;
    }

    public DIMGuiRenderJGems getDearImguiRender() {
        return this.dearImGuiRender;
    }

    public ShadowScene getShadowScene() {
        return this.shadowScene;
    }

    public Vector2i getWindowDimensions() {
        return JGems3D.get().getScreen().getWindowDimensions();
    }

    public SceneData getSceneData() {
        return this.sceneData;
    }

    public JGemsShaderManager getBlurShader() {
        return JGemsResourceManager.globalShaderAssets.blur13;
    }

    public JGemsShaderManager getPostProcessingShader() {
        return JGemsResourceManager.globalShaderAssets.hdr;
    }

    public JGemsShaderManager getSSAOShader() {
        return JGemsResourceManager.globalShaderAssets.world_ssao;
    }

    public JGemsShaderManager getOITShader() {
        return JGemsResourceManager.globalShaderAssets.weighted_oit;
    }

    public JGemsShaderManager getDeferredWorldShader() {
        return JGemsResourceManager.globalShaderAssets.world_deferred;
    }

    public JGemsShaderManager getFXAAShader() {
        return JGemsResourceManager.globalShaderAssets.fxaa;
    }
}