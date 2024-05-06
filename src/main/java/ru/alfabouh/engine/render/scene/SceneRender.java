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
import ru.alfabouh.engine.game.resources.assets.shaders.ShaderManager;
import ru.alfabouh.engine.physics.entities.player.KinematicPlayerSP;
import ru.alfabouh.engine.physics.liquids.ILiquid;
import ru.alfabouh.engine.render.environment.Environment;
import ru.alfabouh.engine.render.environment.shadow.ShadowScene;
import ru.alfabouh.engine.render.scene.programs.FBOTexture2DProgram;
import ru.alfabouh.engine.render.scene.scene_render.groups.*;
import ru.alfabouh.engine.render.scene.world.SceneWorld;
import ru.alfabouh.engine.render.scene.world.camera.ICamera;
import ru.alfabouh.engine.render.screen.Screen;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class SceneRender {
    public static float PSX_SCREEN_OFFSET = 160.0f;
    public static int CURRENT_DEBUG_MODE;

    private List<SceneRenderBase> sceneRenderBases;
    private final GuiRender guiRender;
    private final InventoryRender inventoryRender;

    private final Scene.SceneData sceneData;
    private final FBOTexture2DProgram fboPsx;
    private final FBOTexture2DProgram fboBlur;
    private final FBOTexture2DProgram sceneFbo;
    private final FBOTexture2DProgram mixFbo;
    private final ShadowScene shadowScene;

    private double lastUpdate = Game.glfwTime();
    private int glitchTicks;

    public SceneRender(Scene.SceneData sceneData) {
        this.sceneRenderBases = new ArrayList<>();
        this.guiRender = new GuiRender(this);
        this.inventoryRender = new InventoryRender(this);
        this.sceneData = sceneData;

        this.shadowScene = new ShadowScene(this.getSceneData().getSceneWorld());
        this.fboBlur = new FBOTexture2DProgram(true);
        this.sceneFbo = new FBOTexture2DProgram(true);
        this.fboPsx = new FBOTexture2DProgram(true);
        this.mixFbo = new FBOTexture2DProgram(true);

        this.createFBOs(this.getWindowDimensions());
    }

    public void createFBOs(Vector2i dim) {
        boolean msaa = Game.getGame().getGameSettings().msaa.getValue() != 0;

        this.fboBlur.clearFBO();
        this.sceneFbo.clearFBO();
        this.mixFbo.clearFBO();
        this.fboPsx.clearFBO();

        this.fboPsx.createFrameBuffer2DTexture(dim, new int[]{GL30.GL_COLOR_ATTACHMENT0, GL30.GL_COLOR_ATTACHMENT0, GL30.GL_COLOR_ATTACHMENT0}, true, GL30.GL_RGBA, GL30.GL_RGBA, GL30.GL_LINEAR, GL30.GL_COMPARE_REF_TO_TEXTURE, GL30.GL_LESS, GL30.GL_CLAMP_TO_BORDER, null);
        this.fboBlur.createFrameBuffer2DTexture(dim, new int[]{GL30.GL_COLOR_ATTACHMENT0}, false, GL30.GL_RGB, GL30.GL_RGB, GL30.GL_LINEAR, GL30.GL_COMPARE_REF_TO_TEXTURE, GL30.GL_LESS, GL30.GL_CLAMP_TO_EDGE, null);
        if (msaa) {
            this.sceneFbo.createFrameBuffer2DTextureMSAA(dim, new int[]{GL30.GL_COLOR_ATTACHMENT0, GL30.GL_COLOR_ATTACHMENT1}, GL43.GL_RGB16F);
        } else {
            this.sceneFbo.createFrameBuffer2DTexture(dim, new int[]{GL30.GL_COLOR_ATTACHMENT0, GL30.GL_COLOR_ATTACHMENT1}, true, GL43.GL_RGB16F, GL30.GL_RGB, GL30.GL_LINEAR, GL30.GL_COMPARE_REF_TO_TEXTURE, GL30.GL_LESS, GL30.GL_CLAMP_TO_EDGE, null);
        }
        this.mixFbo.createFrameBuffer2DTexture(dim, new int[]{GL30.GL_COLOR_ATTACHMENT0, GL30.GL_COLOR_ATTACHMENT1}, false, GL43.GL_RGB16F, GL30.GL_RGB, GL30.GL_LINEAR, GL30.GL_COMPARE_REF_TO_TEXTURE, GL30.GL_LESS, GL30.GL_CLAMP_TO_EDGE, null);
    }

    private void fillScene() {
        this.sceneRenderBases.add(new WorldRender(this));
        this.sceneRenderBases.add(new WorldTransparentRender(this));
        this.sceneRenderBases.add(new WorldRenderLiquids(this));
        this.sceneRenderBases.add(new SkyRender(this));
        this.sceneRenderBases.add(new DebugRender(this));
    }

    public void onStartRender() {
        this.fillScene();
        this.sceneRenderBases = this.sceneRenderBases.stream().sorted(Comparator.comparingInt(SceneRenderBase::getRenderPriority)).collect(Collectors.toList());
        this.sceneRenderBases.forEach(SceneRenderBase::onStartRender);
        this.inventoryRender.onStartRender();
        this.guiRender.onStartRender();
    }

    public void onStopRender() {
        this.fboBlur.clearFBO();
        this.sceneFbo.clearFBO();
        this.mixFbo.clearFBO();
        this.fboPsx.clearFBO();

        this.sceneRenderBases.forEach(SceneRenderBase::onStopRender);
        this.inventoryRender.onStopRender();
        this.guiRender.onStopRender();
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

        this.renderScene(partialTicks);
        this.bloomPostProcessing(partialTicks, model);

        if (SceneRender.CURRENT_DEBUG_MODE == 0) {
            GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

            GL30.glEnable(GL30.GL_BLEND);
            GL30.glBlendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);
            this.fboPsx.bindFBO();
            this.fboPsx.connectTextureToBuffer(GL30.GL_COLOR_ATTACHMENT0, 0);
            this.renderSceneWithBloomAndHDR(partialTicks, model);

            this.fboPsx.connectTextureToBuffer(GL30.GL_COLOR_ATTACHMENT0, 2);
            GL30.glClear(GL30.GL_COLOR_BUFFER_BIT);

            this.inventoryRender.onRender(partialTicks);

            this.fboPsx.connectTextureToBuffer(GL30.GL_COLOR_ATTACHMENT0, 1);
            GL30.glClear(GL30.GL_COLOR_BUFFER_BIT);
            this.guiRender.onRender(partialTicks);

            this.fboPsx.unBindFBO();

            this.renderSceneWithPSXPostProcessing(model);

            GL30.glDisable(GL30.GL_BLEND);
        } else {
            this.renderSceneWithBloomAndHDR(partialTicks, model);
            this.renderDebugScreen(partialTicks);
            this.inventoryRender.onRender(partialTicks);
            this.guiRender.onRender(partialTicks);
        }

        model.clean();
        SceneRender.getGameUboShader().unBind();
    }

    public void renderScene(double partialTicks) {
        GL30.glEnable(GL30.GL_DEPTH_TEST);
        this.renderSceneInFbo(partialTicks);
        GL30.glDisable(GL30.GL_DEPTH_TEST);

        this.sceneFbo.copyFBOtoFBO(this.mixFbo.getFrameBufferId(), new int[]{GL30.GL_COLOR_ATTACHMENT0, GL30.GL_COLOR_ATTACHMENT1}, this.getWindowDimensions());
    }

    public void renderSceneWithBloomAndHDR(double partialTicks, Model<Format2D> model) {
        this.getPostProcessingShader().bind();
        this.getPostProcessingShader().performUniform("texture_sampler", 0);
        this.getPostProcessingShader().performUniform("blur_sampler", 1);
        GL30.glActiveTexture(GL30.GL_TEXTURE1);
        this.fboBlur.bindTexture(0);
        GL30.glActiveTexture(GL30.GL_TEXTURE0);
        this.mixFbo.bindTexture(0);
        this.getPostProcessingShader().getUtils().performProjectionMatrix2d(model);
        Scene.renderModel(model, GL30.GL_TRIANGLES);
        this.getPostProcessingShader().unBind();
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
        this.getPostProcessingShader2().performUniform("psx_gui_shake", Game.getGame().isPaused() ? 0 : 1);

        float panic = 1.0f - kinematicPlayerSP.getMind();
        double curr = Game.glfwTime();
        if (curr - this.lastUpdate > 1.0d) {
            if (this.glitchTicks <= 0) {
                if (!Game.getGame().isPaused() && panic > 0.3f && Game.random.nextFloat() <= panic * 0.1f) {
                    this.glitchTicks = 1;
                    Game.getGame().getSoundManager().playLocalSound(ResourceManager.soundAssetsLoader.glitch, SoundType.BACKGROUND_SOUND, 1.0f, 1.0f);
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
        this.fboPsx.bindTexture(0);
        GL30.glActiveTexture(GL30.GL_TEXTURE1);
        this.fboPsx.bindTexture(1);
        GL30.glActiveTexture(GL30.GL_TEXTURE2);
        this.fboPsx.bindTexture(2);

        GL30.glActiveTexture(GL30.GL_TEXTURE3);
        ResourceManager.renderAssets.screen.bindTexture();

        GL30.glActiveTexture(GL30.GL_TEXTURE4);
        ResourceManager.renderAssets.blood.bindTexture();

        this.getPostProcessingShader2().getUtils().performProjectionMatrix2d(model);
        Scene.renderModel(model, GL30.GL_TRIANGLES);
        this.getPostProcessingShader2().unBind();
    }

    private void renderSceneInFbo(double partialTicks) {
        this.getShadowScene().renderAllModelsInShadowMap(this.getSceneData().getSceneWorld().getToRenderList());
        Screen.setViewport(this.getWindowDimensions());
        this.sceneFbo.bindFBO();
        GL30.glClear(GL30.GL_COLOR_BUFFER_BIT | GL30.GL_DEPTH_BUFFER_BIT | GL30.GL_STENCIL_BUFFER_BIT);
        GL30.glEnable(GL30.GL_BLEND);
        GL30.glBlendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);
        for (SceneRenderBase sceneRenderBase : this.sceneRenderBases) {
            sceneRenderBase.onRender(partialTicks);
        }
        GL30.glDisable(GL30.GL_BLEND);
        this.sceneFbo.unBindFBO();
    }

    private void bloomPostProcessing(double partialTicks, Model<Format2D> model) {
        if (Game.getGame().getGameSettings().bloom.getValue() == 0) {
            this.fboBlur.bindFBO();
            GL30.glClear(GL30.GL_COLOR_BUFFER_BIT);
            this.fboBlur.unBindFBO();
            return;
        }
        FBOTexture2DProgram startFbo = this.mixFbo;
        int startBinding = 1;
        int steps = 12;

        this.getBlurShader().bind();
        this.getBlurShader().performUniform("resolution", new Vector2f(this.getWindowDimensions().x, this.getWindowDimensions().y));
        for (int i = 0; i < steps; i++) {
            this.fboBlur.bindFBO();
            GL30.glActiveTexture(GL30.GL_TEXTURE0);
            startFbo.bindTexture(startBinding);
            this.getBlurShader().performUniform("texture_sampler", 0);
            this.getBlurShader().performUniform("direction", i % 2 == 0 ? new Vector2f(1.0f, 0.0f) : new Vector2f(0.0f, 1.0f));
            this.getBlurShader().getUtils().performProjectionMatrix2d(model);
            Scene.renderModel(model, GL30.GL_TRIANGLES);
            this.fboBlur.unBindFBO();
            startFbo = this.fboBlur;
            startBinding = 0;
        }
        this.getBlurShader().unBind();
    }

    private void renderDebugScreen(double partialTicks) {
        if (SceneRender.CURRENT_DEBUG_MODE == 1) {
            Model<Format2D> model = MeshHelper.generatePlane2DModelInverted(new Vector2f(0.0f), new Vector2f(400.0f, 300.0f), 0);
            ResourceManager.shaderAssets.gui_image.bind();
            ResourceManager.shaderAssets.gui_image.performUniform("texture_sampler", 0);
            GL30.glActiveTexture(GL30.GL_TEXTURE0);
            this.mixFbo.bindTexture(1);
            ResourceManager.shaderAssets.gui_image.getUtils().performProjectionMatrix2d(model);
            Scene.renderModel(model, GL30.GL_TRIANGLES);
            ResourceManager.shaderAssets.gui_image.unBind();
            model.clean();

            model = MeshHelper.generatePlane2DModelInverted(new Vector2f(0.0f, 350.0f), new Vector2f(400.0f, 650.0f), 0);
            ResourceManager.shaderAssets.gui_image.bind();
            ResourceManager.shaderAssets.gui_image.performUniform("texture_sampler", 0);
            GL30.glActiveTexture(GL30.GL_TEXTURE0);
            this.getShadowScene().getFrameBufferObjectProgram().bindTexture(0);
            ResourceManager.shaderAssets.gui_image.getUtils().performProjectionMatrix2d(model);
            Scene.renderModel(model, GL30.GL_TRIANGLES);
            this.getShadowScene().getFrameBufferObjectProgram().unBindTexture();
            ResourceManager.shaderAssets.gui_image.unBind();
            model.clean();
        }
    }

    public void onWindowResize(Vector2i dim) {
        this.createFBOs(dim);
    }

    private void updateUbo() {
        SceneRender.getGameUboShader().performUniformBuffer(ResourceManager.shaderAssets.Misc, new float[]{Game.getGame().getScreen().getRenderTicks()});
        Environment environment = this.getSceneData().getSceneWorld().getEnvironment();
        FloatBuffer value1Buffer = MemoryUtil.memAllocFloat(4);
        value1Buffer.put(SceneRender.CURRENT_DEBUG_MODE == 0 ? environment.getFog().getDensity() : 0.0f);
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
}