package ru.alfabouh.engine.render.scene;

import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3d;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL43;
import org.lwjgl.system.MemoryUtil;
import ru.alfabouh.engine.audio.sound.data.SoundType;
import ru.alfabouh.engine.game.Game;
import ru.alfabouh.engine.game.resources.ResourceManager;
import ru.alfabouh.engine.game.resources.assets.models.Model;
import ru.alfabouh.engine.game.resources.assets.models.basic.MeshHelper;
import ru.alfabouh.engine.game.resources.assets.models.formats.Format2D;
import ru.alfabouh.engine.game.resources.assets.models.formats.Format3D;
import ru.alfabouh.engine.game.resources.assets.shaders.ShaderManager;
import ru.alfabouh.engine.physics.entities.player.KinematicPlayerSP;
import ru.alfabouh.engine.physics.liquids.ILiquid;
import ru.alfabouh.engine.render.environment.Environment;
import ru.alfabouh.engine.render.environment.shadow.ShadowScene;
import ru.alfabouh.engine.render.imgui.IMGUIRender;
import ru.alfabouh.engine.render.scene.debug.constants.GlobalRenderDebugConstants;
import ru.alfabouh.engine.render.scene.objects.IModeledSceneObject;
import ru.alfabouh.engine.render.scene.objects.items.LiquidObject;
import ru.alfabouh.engine.render.scene.programs.FBOTexture2DProgram;
import ru.alfabouh.engine.render.scene.components.groups.*;
import ru.alfabouh.engine.render.scene.world.SceneWorld;
import ru.alfabouh.engine.render.scene.world.camera.ICamera;
import ru.alfabouh.engine.render.transformation.TransformationManager;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class SceneRender {
    public static float PSX_SCREEN_OFFSET = 160.0f;

    private List<SceneRenderBase> sceneRenderBases_forward;
    private List<SceneRenderBase> sceneRenderBases_deferred;

    private final IMGUIRender imguiRender;

    private final GuiRender guiRender;
    private final InventoryRender inventoryRender;

    private final Scene.SceneData sceneData;

    private final FBOTexture2DProgram fboPsx;
    private final FBOTexture2DProgram fboBlur;
    private final FBOTexture2DProgram sceneFbo;
    private final FBOTexture2DProgram gBuffer;
    private final FBOTexture2DProgram finalRenderedSceneFbo;

    private final ShadowScene shadowScene;

    private double lastUpdate = Game.glfwTime();
    private int glitchTicks;

    public SceneRender(Scene.SceneData sceneData) {
        this.sceneRenderBases_deferred = new ArrayList<>();
        this.sceneRenderBases_forward = new ArrayList<>();

        this.guiRender = new GuiRender(this);
        this.inventoryRender = new InventoryRender(this);

        this.sceneData = sceneData;

        this.shadowScene = new ShadowScene(this.getSceneData().getSceneWorld());
        this.fboBlur = new FBOTexture2DProgram(true);
        this.sceneFbo = new FBOTexture2DProgram(true);
        this.fboPsx = new FBOTexture2DProgram(true);
        this.gBuffer = new FBOTexture2DProgram(true);
        this.finalRenderedSceneFbo = new FBOTexture2DProgram(true);

        this.createFBOs(this.getWindowDimensions());

        this.imguiRender = new IMGUIRender(Game.getGame().getScreen().getWindow(), Game.getGame().getResourceManager().getGameCache());
    }

    public void createFBOs(Vector2i dim) {
        this.clearAllFBOs();

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
        this.gBuffer.createFrameBuffer2DTexture(dim, gBufferFBOs, true, GL30.GL_NEAREST, GL30.GL_COMPARE_REF_TO_TEXTURE, GL30.GL_LESS, GL30.GL_CLAMP_TO_EDGE, null);

        FBOTexture2DProgram.FBOTextureInfo[] finalRenderedSceneFBOs = new FBOTexture2DProgram.FBOTextureInfo[]
                {
                        new FBOTexture2DProgram.FBOTextureInfo(GL30.GL_COLOR_ATTACHMENT0, GL30.GL_RGB, GL30.GL_RGB)
                };
        this.finalRenderedSceneFbo.createFrameBuffer2DTexture(dim, finalRenderedSceneFBOs, false, GL30.GL_NEAREST, GL30.GL_COMPARE_REF_TO_TEXTURE, GL30.GL_LESS, GL30.GL_CLAMP_TO_EDGE, null);
    }

    private void fillScene() {
        this.sceneRenderBases_forward.add(new WorldForwardRender(this));
        this.sceneRenderBases_forward.add(new WorldTransparentRender(this));
        this.sceneRenderBases_forward.add(new SkyRender(this));
        this.sceneRenderBases_forward.add(new DebugRender(this));

        this.sceneRenderBases_deferred.add(new WorldRenderLiquids(this));
        this.sceneRenderBases_deferred.add(new WorldDeferredRender(this));
    }

    public void onStartRender() {
        this.fillScene();
        this.sceneRenderBases_forward = this.sceneRenderBases_forward.stream().sorted(Comparator.comparingInt(SceneRenderBase::getRenderPriority)).collect(Collectors.toList());
        this.sceneRenderBases_deferred = this.sceneRenderBases_deferred.stream().sorted(Comparator.comparingInt(SceneRenderBase::getRenderPriority)).collect(Collectors.toList());

        this.sceneRenderBases_forward.forEach(SceneRenderBase::onStartRender);
        this.sceneRenderBases_deferred.forEach(SceneRenderBase::onStartRender);

        this.inventoryRender.onStartRender();
        this.guiRender.onStartRender();
    }

    public void onStopRender() {
        this.clearAllFBOs();

        this.sceneRenderBases_forward.forEach(SceneRenderBase::onStopRender);
        this.sceneRenderBases_deferred.forEach(SceneRenderBase::onStopRender);

        this.inventoryRender.onStopRender();
        this.guiRender.onStopRender();

        this.getImguiRender().cleanUp();
    }

    private void clearAllFBOs() {
        this.getFboBlur().clearFBO();
        this.getSceneFbo().clearFBO();
        this.getFboPsx().clearFBO();
        this.getGBuffer().clearFBO();
        this.getFinalRenderedSceneFbo().clearFBO();
    }

    public void onRender(double partialTicks) {
        if (this.getSceneData().getCamera() == null) {
            GL30.glEnable(GL30.GL_DEPTH_TEST);
            this.guiRender.onRender(partialTicks);
            GL30.glDisable(GL30.GL_DEPTH_TEST);
            return;
        }

        final Model<Format2D> model = MeshHelper.generatePlane2DModelInverted(new Vector2f(0.0f), new Vector2f(this.getWindowDimensions().x, this.getWindowDimensions().y), 0);
        this.updateUbo();
        this.getSceneData().getSceneWorld().getEnvironment().onUpdate(this.getSceneData().getSceneWorld());

        if (this.checkPlayerCameraInWater()) {
            this.getSceneData().getSceneWorld().getEnvironment().setFog(this.getSceneData().getSceneWorld().getEnvironment().getWaterFog());
        } else {
            this.getSceneData().getSceneWorld().getEnvironment().setFog(this.getSceneData().getSceneWorld().getEnvironment().getWorldFog());
        }

        SceneRender.getGameUboShader().bind();

        if (Game.getGame().isPaused()) {
            GL30.glClear(GL30.GL_COLOR_BUFFER_BIT);
            this.guiRender.onRender(partialTicks);
        } else {
            this.renderScene(partialTicks, model);
            this.bloomPostProcessing(partialTicks, model);
            this.renderSceneWithBloomAndHDR(partialTicks, model);
            this.postFXAA(model);
            if (GlobalRenderDebugConstants.ENABLE_PSX) {
                GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

                GL30.glEnable(GL30.GL_BLEND);
                GL30.glBlendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);
                this.getFboPsx().bindFBO();
                this.getFboPsx().connectTextureToBuffer(GL30.GL_COLOR_ATTACHMENT0, 0);
                this.renderFinalScene(model);

                this.getFboPsx().connectTextureToBuffer(GL30.GL_COLOR_ATTACHMENT0, 2);
                GL30.glClear(GL30.GL_COLOR_BUFFER_BIT);

                this.inventoryRender.onRender(partialTicks);

                this.getFboPsx().connectTextureToBuffer(GL30.GL_COLOR_ATTACHMENT0, 1);
                GL30.glClear(GL30.GL_COLOR_BUFFER_BIT);
                this.guiRender.onRender(partialTicks);
                this.getFboPsx().unBindFBO();
                this.renderSceneWithPSXPostProcessing(model);

                GL30.glDisable(GL30.GL_BLEND);
            } else {
                this.renderFinalScene(model);
                this.renderDebugScreen(partialTicks);
                this.inventoryRender.onRender(partialTicks);
                this.guiRender.onRender(partialTicks);
            }
        }

        model.clean();
        SceneRender.getGameUboShader().unBind();

        this.getImguiRender().render(partialTicks);
    }

    private void renderFinalScene(Model<Format2D> model) {
       ShaderManager imgShader = ResourceManager.shaderAssets.gui_image;
       imgShader.bind();
       imgShader.performUniform("texture_sampler", 0);
       GL30.glActiveTexture(GL30.GL_TEXTURE0);
       this.getFinalRenderedSceneFbo().bindTexture(0);
       imgShader.getUtils().performProjectionMatrix2d(model);
       Scene.renderModel(model, GL30.GL_TRIANGLES);
       imgShader.unBind();
    }

    private void geometryPass(double partialTicks) {
        this.getGBuffer().bindFBO();
        GL30.glClear(GL30.GL_COLOR_BUFFER_BIT | GL30.GL_DEPTH_BUFFER_BIT | GL30.GL_STENCIL_BUFFER_BIT);
        for (SceneRenderBase sceneRenderBase : this.sceneRenderBases_deferred) {
            sceneRenderBase.onRender(partialTicks);
        }
        this.getGBuffer().unBindFBO();
    }

    private void lightPass(double partialTicks, Model<Format2D> model) {
        this.getDeferredWorldShader().bind();
        this.getDeferredWorldShader().performUniform("view_matrix", TransformationManager.instance.getMainCameraViewMatrix());
        this.getDeferredWorldShader().performUniform("gPositions", 0);
        this.getDeferredWorldShader().performUniform("gNormals", 1);
        this.getDeferredWorldShader().performUniform("gTexture", 2);
        this.getDeferredWorldShader().performUniform("gEmission", 3);
        this.getDeferredWorldShader().performUniform("gSpecular", 4);
        this.getDeferredWorldShader().performUniform("gMetallic", 5);
        this.getDeferredWorldShader().getUtils().passShadowsInfo();

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

        this.getDeferredWorldShader().getUtils().performProjectionMatrix2d(model);
        Scene.renderModel(model, GL30.GL_TRIANGLES);
        this.getDeferredWorldShader().unBind();
    }

    public void renderScene(double partialTicks, Model<Format2D> model) {
        this.getShadowScene().renderAllModelsInShadowMap(this.getModelsToRenderInShadows(this.getSceneData().getSceneWorld()));
        Game.getGame().getScreen().normalizeViewPort();

        GL30.glEnable(GL30.GL_DEPTH_TEST);
        this.geometryPass(partialTicks);

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
        models.addAll(sceneWorld.getToRenderList().stream().filter(e -> e.hasRender() && e.getModelRenderParams().isShadowCaster()).map(IModeledSceneObject::getModel3D).collect(Collectors.toList()));
        models.addAll(sceneWorld.getLiquids().stream().map(LiquidObject::getModel).collect(Collectors.toList()));
        return models;
    }

    private void renderForwardScene(double partialTicks) {
        GL30.glEnable(GL30.GL_BLEND);
        GL30.glBlendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);
        for (SceneRenderBase sceneRenderBase : this.sceneRenderBases_forward) {
            sceneRenderBase.onRender(partialTicks);
        }
        GL30.glDisable(GL30.GL_BLEND);
    }

    public void postFXAA(Model<Format2D> model) {
        this.getFinalRenderedSceneFbo().bindFBO();
        this.getFXAAShader().bind();
        this.getFXAAShader().performUniform("resolution", new Vector2f(this.getWindowDimensions().x, this.getWindowDimensions().y));
        this.getFXAAShader().performUniform("texture_sampler", 0);
        this.getFXAAShader().performUniform("FXAA_SPAN_MAX", (float) Math.pow(Game.getGame().getGameSettings().fxaa.getValue(), 2));
        this.getFXAAShader().getUtils().performProjectionMatrix2d(model);
        GL30.glActiveTexture(GL30.GL_TEXTURE0);
        this.getFinalRenderedSceneFbo().bindTexture(0);
        Scene.renderModel(model, GL30.GL_TRIANGLES);
        this.getFXAAShader().unBind();
        this.getFinalRenderedSceneFbo().unBindFBO();
    }

    public void renderSceneWithBloomAndHDR(double partialTicks, Model<Format2D> model) {
        this.getFinalRenderedSceneFbo().bindFBO();
        GL30.glClear(GL30.GL_COLOR_BUFFER_BIT | GL30.GL_DEPTH_BUFFER_BIT);

        this.getPostProcessingShader().bind();
        this.getPostProcessingShader().performUniform("texture_sampler", 0);
        this.getPostProcessingShader().performUniform("blur_sampler", 1);
        GL30.glActiveTexture(GL30.GL_TEXTURE1);
        this.getFboBlur().bindTexture(0);
        GL30.glActiveTexture(GL30.GL_TEXTURE0);
        this.getSceneFbo().bindTexture(0);
        this.getPostProcessingShader().getUtils().performProjectionMatrix2d(model);
        Scene.renderModel(model, GL30.GL_TRIANGLES);
        this.getPostProcessingShader().unBind();

        this.getFinalRenderedSceneFbo().unBindFBO();
    }

    public void renderSceneWithPSXPostProcessing(Model<Format2D> model) {
        if (!Game.getGame().isValidPlayer()) {
           return;
        }
        KinematicPlayerSP kinematicPlayerSP = (KinematicPlayerSP) Game.getGame().getPlayerSP();

        this.getPostProcessingShader2().bind();
        this.getPostProcessingShader2().performUniform("texture_sampler", 0);
        this.getPostProcessingShader2().performUniform("texture_sampler_gui", 1);
        this.getPostProcessingShader2().performUniform("texture_sampler_inventory", 2);
        this.getPostProcessingShader2().performUniform("texture_screen", 3);
        this.getPostProcessingShader2().performUniform("texture_blood", 4);
        this.getPostProcessingShader2().performUniform("kill", kinematicPlayerSP.isKilled());
        this.getPostProcessingShader2().performUniform("victory", kinematicPlayerSP.isVictory());

        this.getPostProcessingShader2().performUniform("e_lsd", 0);

        float panic = 1.0f - kinematicPlayerSP.getMind();
        double curr = Game.glfwTime();
        if (curr - this.lastUpdate > 1.0d) {
            if (this.glitchTicks <= 0) {
                if (!Game.getGame().isPaused() && panic > 0.3f && Game.random.nextFloat() <= panic * 0.1f) {
                    this.glitchTicks = 1;
                    Game.getGame().getSoundManager().playLocalSound(ResourceManager.soundAssetsLoader.glitch, SoundType.BACKGROUND_SOUND, 1.5f, 1.0f);
                }
            } else {
                this.glitchTicks = 0;
            }
            this.lastUpdate = curr;
        }

        this.getPostProcessingShader2().performUniform("glitch_tick", this.glitchTicks);
        this.getPostProcessingShader2().performUniform("panic", panic);
        this.getPostProcessingShader2().performUniform("offset", SceneRender.PSX_SCREEN_OFFSET);
        GL30.glActiveTexture(GL30.GL_TEXTURE0);
        this.getFboPsx().bindTexture(0);
        GL30.glActiveTexture(GL30.GL_TEXTURE1);
        this.getFboPsx().bindTexture(1);
        GL30.glActiveTexture(GL30.GL_TEXTURE2);
        this.getFboPsx().bindTexture(2);

        GL30.glActiveTexture(GL30.GL_TEXTURE3);
        ResourceManager.renderAssets.screen.bindTexture();

        GL30.glActiveTexture(GL30.GL_TEXTURE4);
        ResourceManager.renderAssets.blood.bindTexture();

        this.getPostProcessingShader2().getUtils().performProjectionMatrix2d(model);
        Scene.renderModel(model, GL30.GL_TRIANGLES);
        this.getPostProcessingShader2().unBind();
    }

    private void bloomPostProcessing(double partialTicks, Model<Format2D> model) {
        if (Game.getGame().getGameSettings().bloom.getValue() == 0) {
            this.getFboBlur().bindFBO();
            GL30.glClear(GL30.GL_COLOR_BUFFER_BIT);
            this.getFboBlur().unBindFBO();
            return;
        }
        FBOTexture2DProgram startFbo = this.getSceneFbo();
        int startBinding = 1;
        int steps = 12;

        this.getBlurShader().bind();
        this.getBlurShader().performUniform("resolution", new Vector2f(this.getWindowDimensions().x, this.getWindowDimensions().y));
        for (int i = 0; i < steps; i++) {
            this.getFboBlur().bindFBO();
            GL30.glActiveTexture(GL30.GL_TEXTURE0);
            startFbo.bindTexture(startBinding);
            this.getBlurShader().performUniform("texture_sampler", 0);
            this.getBlurShader().performUniform("direction", i % 2 == 0 ? new Vector2f(1.0f, 0.0f) : new Vector2f(0.0f, 1.0f));
            this.getBlurShader().getUtils().performProjectionMatrix2d(model);
            Scene.renderModel(model, GL30.GL_TRIANGLES);
            this.getFboBlur().unBindFBO();
            startFbo = this.getFboBlur();
            startBinding = 0;
        }
        this.getBlurShader().unBind();
    }

    private void renderDebugScreen(double partialTicks) {
        if (GlobalRenderDebugConstants.RENDER_FBO_TEXTURES_ON_SCREEN) {
            Model<Format2D> model = MeshHelper.generatePlane2DModelInverted(new Vector2f(0.0f), new Vector2f(300.0f, 200.0f), 0);
            ResourceManager.shaderAssets.gui_image.bind();
            ResourceManager.shaderAssets.gui_image.performUniform("texture_sampler", 0);
            GL30.glActiveTexture(GL30.GL_TEXTURE0);
            this.gBuffer.bindTexture(0);
            ResourceManager.shaderAssets.gui_image.getUtils().performProjectionMatrix2d(model);
            Scene.renderModel(model, GL30.GL_TRIANGLES);
            ResourceManager.shaderAssets.gui_image.unBind();
            model.clean();


            model = MeshHelper.generatePlane2DModelInverted(new Vector2f(0.0f, 200.0f), new Vector2f(300.0f, 400.0f), 0);
            ResourceManager.shaderAssets.gui_image.bind();
            ResourceManager.shaderAssets.gui_image.performUniform("texture_sampler", 0);
            GL30.glActiveTexture(GL30.GL_TEXTURE0);
            this.gBuffer.bindTexture(1);
            ResourceManager.shaderAssets.gui_image.getUtils().performProjectionMatrix2d(model);
            Scene.renderModel(model, GL30.GL_TRIANGLES);
            ResourceManager.shaderAssets.gui_image.unBind();
            model.clean();

            model = MeshHelper.generatePlane2DModelInverted(new Vector2f(0.0f, 400.0f), new Vector2f(300.0f, 600.0f), 0);
            ResourceManager.shaderAssets.gui_image.bind();
            ResourceManager.shaderAssets.gui_image.performUniform("texture_sampler", 0);
            GL30.glActiveTexture(GL30.GL_TEXTURE0);
            this.gBuffer.bindTexture(2);
            ResourceManager.shaderAssets.gui_image.getUtils().performProjectionMatrix2d(model);
            Scene.renderModel(model, GL30.GL_TRIANGLES);
            ResourceManager.shaderAssets.gui_image.unBind();
            model.clean();

            model = MeshHelper.generatePlane2DModelInverted(new Vector2f(0.0f, 600.0f), new Vector2f(300.0f, 800.0f), 0);
            ResourceManager.shaderAssets.gui_image.bind();
            ResourceManager.shaderAssets.gui_image.performUniform("texture_sampler", 0);
            GL30.glActiveTexture(GL30.GL_TEXTURE0);
            this.gBuffer.bindTexture(3);
            ResourceManager.shaderAssets.gui_image.getUtils().performProjectionMatrix2d(model);
            Scene.renderModel(model, GL30.GL_TRIANGLES);
            ResourceManager.shaderAssets.gui_image.unBind();
            model.clean();

            model = MeshHelper.generatePlane2DModelInverted(new Vector2f(300.0f, 400.0f), new Vector2f(600.0f, 600.0f), 0);
            ResourceManager.shaderAssets.gui_image.bind();
            ResourceManager.shaderAssets.gui_image.performUniform("texture_sampler", 0);
            GL30.glActiveTexture(GL30.GL_TEXTURE0);
            this.gBuffer.bindTexture(4);
            ResourceManager.shaderAssets.gui_image.getUtils().performProjectionMatrix2d(model);
            Scene.renderModel(model, GL30.GL_TRIANGLES);
            ResourceManager.shaderAssets.gui_image.unBind();
            model.clean();

            model = MeshHelper.generatePlane2DModelInverted(new Vector2f(300.0f, 600.0f), new Vector2f(600.0f, 800.0f), 0);
            ResourceManager.shaderAssets.gui_image.bind();
            ResourceManager.shaderAssets.gui_image.performUniform("texture_sampler", 0);
            GL30.glActiveTexture(GL30.GL_TEXTURE0);
            this.gBuffer.bindTexture(5);
            ResourceManager.shaderAssets.gui_image.getUtils().performProjectionMatrix2d(model);
            Scene.renderModel(model, GL30.GL_TRIANGLES);
            ResourceManager.shaderAssets.gui_image.unBind();
            model.clean();

            model = MeshHelper.generatePlane2DModelInverted(new Vector2f(300.0f, 0.0f), new Vector2f(600.0f, 200.0f), 0);
            ResourceManager.shaderAssets.gui_image.bind();
            ResourceManager.shaderAssets.gui_image.performUniform("texture_sampler", 0);
            GL30.glActiveTexture(GL30.GL_TEXTURE0);
            this.getShadowScene().getFrameBufferObjectProgram().bindTexture(0);
            ResourceManager.shaderAssets.gui_image.getUtils().performProjectionMatrix2d(model);
            Scene.renderModel(model, GL30.GL_TRIANGLES);
            ResourceManager.shaderAssets.gui_image.unBind();
            model.clean();

            model = MeshHelper.generatePlane2DModelInverted(new Vector2f(300.0f, 200.0f), new Vector2f(600.0f, 400.0f), 0);
            ResourceManager.shaderAssets.gui_image.bind();
            ResourceManager.shaderAssets.gui_image.performUniform("texture_sampler", 0);
            GL30.glActiveTexture(GL30.GL_TEXTURE0);
            this.sceneFbo.bindTexture(1);
            ResourceManager.shaderAssets.gui_image.getUtils().performProjectionMatrix2d(model);
            Scene.renderModel(model, GL30.GL_TRIANGLES);
            ResourceManager.shaderAssets.gui_image.unBind();
            model.clean();
        }
    }

    public void onWindowResize(Vector2i dim) {
        this.createFBOs(dim);
        this.getImguiRender().onResize(dim);
    }

    private void updateUbo() {
        SceneRender.getGameUboShader().performUniformBuffer(ResourceManager.shaderAssets.Misc, new float[]{Game.getGame().getScreen().getRenderTicks()});
        Environment environment = this.getSceneData().getSceneWorld().getEnvironment();
        FloatBuffer value1Buffer = MemoryUtil.memAllocFloat(4);
        value1Buffer.put(!GlobalRenderDebugConstants.FULL_BRIGHT ? environment.getFog().getDensity() : 0.0f);
        value1Buffer.put((float) environment.getFog().getColor().x);
        value1Buffer.put((float) environment.getFog().getColor().y);
        value1Buffer.put((float) environment.getFog().getColor().z);
        value1Buffer.flip();
        SceneRender.getGameUboShader().performUniformBuffer(ResourceManager.shaderAssets.Fog, value1Buffer);
        MemoryUtil.memFree(value1Buffer);
    }

    private boolean checkPlayerCameraInWater() {
        for (ILiquid liquid : this.getSceneData().getSceneWorld().getWorld().getLiquids()) {
            ICamera camera = this.getSceneData().getCamera();
            Vector3d left = new Vector3d(liquid.getZone().getLocation()).sub(liquid.getZone().getSize().mul(0.5d));
            Vector3d right = new Vector3d(liquid.getZone().getLocation()).add(liquid.getZone().getSize().mul(0.5d));
            if (camera.getCamPosition().x >= left.x && camera.getCamPosition().y >= left.y && camera.getCamPosition().z >= left.z && camera.getCamPosition().x <= right.x && camera.getCamPosition().y <= right.y && camera.getCamPosition().z <= right.z) {
                return true;
            }
        }
        return false;
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

    public FBOTexture2DProgram getFboBlur() {
        return this.fboBlur;
    }

    public IMGUIRender getImguiRender() {
        return this.imguiRender;
    }

    public ShadowScene getShadowScene() {
        return this.shadowScene;
    }

    public Vector2i getWindowDimensions() {
        return Game.getGame().getScreen().getDimensions();
    }

    public static ShaderManager getGameUboShader() {
        return ResourceManager.shaderAssets.gameUbo;
    }

    public Scene.SceneData getSceneData() {
        return this.sceneData;
    }

    public ShaderManager getBlurShader() {
        return ResourceManager.shaderAssets.blur13;
    }

    public ShaderManager getPostProcessingShader() {
        return ResourceManager.shaderAssets.hdr;
    }

    public ShaderManager getPostProcessingShader2() {
        return ResourceManager.shaderAssets.post_psx;
    }

    public ShaderManager getDeferredWorldShader() {
        return ResourceManager.shaderAssets.world_deferred;
    }

    public ShaderManager getFXAAShader() {
        return ResourceManager.shaderAssets.fxaa;
    }

    public enum RenderPass {
        DEFERRED,
        FORWARD;
    }
}