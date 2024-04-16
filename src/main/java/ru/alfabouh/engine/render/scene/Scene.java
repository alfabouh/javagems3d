package ru.alfabouh.engine.render.scene;

import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3d;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL43;
import org.lwjgl.system.MemoryUtil;
import ru.alfabouh.engine.audio.sound.data.SoundType;
import ru.alfabouh.engine.game.Game;
import ru.alfabouh.engine.game.controller.input.IController;
import ru.alfabouh.engine.game.resources.ResourceManager;
import ru.alfabouh.engine.game.resources.assets.materials.Material;
import ru.alfabouh.engine.game.resources.assets.models.Model;
import ru.alfabouh.engine.game.resources.assets.models.basic.MeshHelper;
import ru.alfabouh.engine.game.resources.assets.models.formats.Format2D;
import ru.alfabouh.engine.game.resources.assets.models.formats.Format3D;
import ru.alfabouh.engine.game.resources.assets.models.mesh.ModelNode;
import ru.alfabouh.engine.game.resources.assets.shaders.ShaderManager;
import ru.alfabouh.engine.game.synchronizing.SyncManger;
import ru.alfabouh.engine.physics.entities.player.KinematicPlayerSP;
import ru.alfabouh.engine.physics.liquids.ILiquid;
import ru.alfabouh.engine.physics.world.object.WorldItem;
import ru.alfabouh.engine.physics.world.timer.PhysicThreadManager;
import ru.alfabouh.engine.render.environment.Environment;
import ru.alfabouh.engine.render.environment.shadow.ShadowScene;
import ru.alfabouh.engine.render.frustum.FrustumCulling;
import ru.alfabouh.engine.render.scene.gui.base.GUI;
import ru.alfabouh.engine.render.scene.gui.base.GameGUI;
import ru.alfabouh.engine.render.scene.gui.font.GuiFont;
import ru.alfabouh.engine.render.scene.objects.IModeledSceneObject;
import ru.alfabouh.engine.render.scene.objects.items.PhysicsObject;
import ru.alfabouh.engine.render.scene.programs.FBOTexture2DProgram;
import ru.alfabouh.engine.render.scene.scene_render.groups.GuiRender;
import ru.alfabouh.engine.render.scene.scene_render.groups.InventoryRender;
import ru.alfabouh.engine.render.scene.world.SceneWorld;
import ru.alfabouh.engine.render.scene.world.camera.AttachedCamera;
import ru.alfabouh.engine.render.scene.world.camera.FreeCamera;
import ru.alfabouh.engine.render.scene.world.camera.ICamera;
import ru.alfabouh.engine.render.screen.Screen;
import ru.alfabouh.engine.render.screen.window.Window;
import ru.alfabouh.engine.render.transformation.TransformationManager;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Scene implements IScene {
    private final List<SceneRenderBase> sceneRenderBases;
    private final Screen screen;
    private final Window window;
    private final SceneWorld sceneWorld;
    private final FrustumCulling frustumCulling;
    private final SceneRenderConveyor sceneRenderConveyor;
    private final GameGUI gui;
    private double elapsedTime;
    private List<SceneRenderBase> mainGroup;
    private boolean refresh;
    private ICamera currentCamera;
    private GuiRender guiRender;
    private InventoryRender inventoryRender;
    private boolean requestDestroyMap;

    public Scene(Screen screen, SceneWorld sceneWorld) {
        this.sceneWorld = sceneWorld;
        this.screen = screen;
        this.mainGroup = new ArrayList<>();
        this.window = this.getScreen().getWindow();
        this.frustumCulling = new FrustumCulling();
        this.sceneRenderBases = new ArrayList<>();
        this.sceneRenderConveyor = new SceneRenderConveyor();
        this.gui = new GameGUI();
        this.currentCamera = null;
    }

    public static boolean isSceneActive() {
        return Screen.isScreenActive();
    }

    public static void setCamera(ICamera camera) {
        Game.getGame().getScreen().getScene().setRenderCamera(camera);
    }

    public static int getDebugMode() {
        return Game.getGame().getScreen().getScene().getSceneRender().getCurrentDebugMode();
    }

    public void setDebugMode(int a) {
        this.getSceneRender().CURRENT_DEBUG_MODE = a;
        if (this.getSceneRender().CURRENT_DEBUG_MODE > 1) {
            this.getSceneRender().CURRENT_DEBUG_MODE = 0;
        }
    }

    public static void setSceneDebugMode(int a) {
        Game.getGame().getScreen().getScene().setDebugMode(a);
    }

    @SuppressWarnings("all")
    public static void renderModel(Model<?> model, int code) {
        for (ModelNode modelNode : model.getMeshDataGroup().getModelNodeList()) {
            GL30.glBindVertexArray(modelNode.getMesh().getVao());
            for (int a : modelNode.getMesh().getAttributePointers()) {
                GL30.glEnableVertexAttribArray(a);
            }
            GL30.glDrawElements(code, modelNode.getMesh().getTotalVertices(), GL30.GL_UNSIGNED_INT, 0);
            for (int a : modelNode.getMesh().getAttributePointers()) {
                GL30.glDisableVertexAttribArray(a);
            }
            GL30.glBindVertexArray(0);
        }
    }

    public static void renderSceneObject(IModeledSceneObject sceneObject) {
        Scene.renderSceneObject(sceneObject, null);
    }

    public static void renderSceneObject(IModeledSceneObject sceneObject, Material overMaterial) {
        Scene scene = Game.getGame().getScreen().getScene();
        if (sceneObject != null) {
            Model<Format3D> model = sceneObject.getModel3D();
            if (model == null || model.getMeshDataGroup() == null) {
                return;
            }
            ShaderManager shaderManager = sceneObject.getModelRenderParams().getShaderManager();
            shaderManager.getUtils().passViewAndModelMatrices(TransformationManager.instance.getMainCameraViewMatrix(), model);
            shaderManager.getUtils().performConstraintsOnShader(sceneObject.getModelRenderParams());
            if (shaderManager.checkUniformInGroup("texture_scaling")) {
                shaderManager.performUniform("texture_scaling", sceneObject.getModelRenderParams().getTextureScaling());
            }
            if (shaderManager.checkUniformInGroup("alpha_discard")) {
                shaderManager.performUniform("alpha_discard", sceneObject.getModelRenderParams().getAlphaDiscardValue());
            }
            if (sceneObject.getModelRenderParams().getAlphaDiscardValue() > 0) {
                GL30.glDisable(GL30.GL_BLEND);
            }
            for (ModelNode modelNode : model.getMeshDataGroup().getModelNodeList()) {
                shaderManager.getUtils().performModelMaterialOnShader(overMaterial != null ? overMaterial : modelNode.getMaterial(), sceneObject.getModelRenderParams().isShadowReceiver());
                GL30.glBindVertexArray(modelNode.getMesh().getVao());
                for (int a : modelNode.getMesh().getAttributePointers()) {
                    GL30.glEnableVertexAttribArray(a);
                }
                GL30.glDrawElements(GL30.GL_TRIANGLES, modelNode.getMesh().getTotalVertices(), GL30.GL_UNSIGNED_INT, 0);
                for (int a : modelNode.getMesh().getAttributePointers()) {
                    GL30.glDisableVertexAttribArray(a);
                }
                GL30.glBindVertexArray(0);
            }
            GL30.glEnable(GL30.GL_BLEND);
        }
    }

    public static void activeGlTexture(int code) {
        GL30.glActiveTexture(GL13.GL_TEXTURE0 + code);
    }

    public static ShaderManager getGameUboShader() {
        return ResourceManager.shaderAssets.gameUbo;
    }

    public void removeGui() {
        this.getGui().setCurrentGui(null);
    }

    public GameGUI getGui() {
        return this.gui;
    }

    public void setGui(GUI gui) {
        this.getGui().setCurrentGui(gui);
    }

    public void addSceneRenderBase(SceneRenderBase sceneRenderBase) {
        this.sceneRenderBases.add(sceneRenderBase);
    }

    public void setRenderCamera(ICamera camera) {
        this.currentCamera = camera;
    }

    public ICamera getCurrentCamera() {
        return this.currentCamera;
    }

    public SceneWorld getSceneWorld() {
        return this.sceneWorld;
    }

    public List<SceneRenderBase> getRenderQueueContainer() {
        return this.sceneRenderBases;
    }

    public Screen getScreen() {
        return this.screen;
    }

    public Vector2i getWindowDimensions() {
        return this.getWindow().getWindowDimensions();
    }

    public Window getWindow() {
        return this.window;
    }

    private void collectRenderBases() {
        Game.getGame().getLogManager().log("Creating render groups...");
        this.mainGroup = this.getRenderQueueContainer().stream().sorted(Comparator.comparingInt(SceneRenderBase::getRenderPriority)).collect(Collectors.toList());
    }

    public GuiRender getGuiRender() {
        return this.guiRender;
    }

    public InventoryRender getInventoryRender() {
        return this.inventoryRender;
    }

    public void preRender() {
        this.getGui().initMainMenu();
        Game.getGame().getPhysicThreadManager().getPhysicsTimer().jbDebugDraw.setupBuffers();
        this.collectRenderBases();
        this.getSceneWorld().setFrustumCulling(this.getFrustumCulling());
        Game.getGame().getLogManager().log("Starting scene rendering: ");
        this.getSceneRender().onStartRender();
        this.guiRender = new GuiRender(this.getSceneRender());
        this.inventoryRender = new InventoryRender(this.getSceneRender());
        this.getGuiRender().onStartRender();
        this.getInventoryRender().onStartRender();
        for (SceneRenderBase sceneRenderBase : this.sceneRenderBases) {
            Game.getGame().getLogManager().log("Starting " + sceneRenderBase.getRenderGroup().getId() + " scene");
            sceneRenderBase.onStartRender();
            Game.getGame().getLogManager().log("Scene " + sceneRenderBase.getRenderGroup().getId() + " successfully started!");
        }
    }

    public void postRender() {
        Game.getGame().getPhysicThreadManager().getPhysicsTimer().jbDebugDraw.cleanup();
        Game.getGame().getLogManager().log("Stopping scene rendering: ");
        this.getSceneRender().onStopRender();
        this.getGuiRender().onStopRender();
        for (SceneRenderBase sceneRenderBase : this.getRenderQueueContainer()) {
            Game.getGame().getLogManager().log("Stopping " + sceneRenderBase.getRenderGroup().getId() + " scene");
            sceneRenderBase.onStopRender();
            Game.getGame().getLogManager().log("Scene " + sceneRenderBase.getRenderGroup().getId() + " successfully stopped!");
        }
        this.getInventoryRender().onStopRender();
        this.getGuiRender().onStopRender();
        Game.getGame().getLogManager().log("Destroying resources!");
        this.removeGui();
        GuiFont.allCreatedFonts.forEach(GuiFont::cleanUp);
        Game.getGame().getResourceManager().destroy();
        Game.getGame().getLogManager().log("Scene rendering stopped");
    }

    public void onWindowResize(Vector2i dim) {
        this.getSceneRender().onWindowResize(dim);
    }

    public void enableFreeCamera(IController controller, Vector3d pos, Vector3d rot) {
        this.setRenderCamera(new FreeCamera(controller, pos, rot));
    }

    public void enableAttachedCamera(WorldItem worldItem) {
        this.setRenderCamera(new AttachedCamera(worldItem));
    }

    public void enableAttachedCamera(PhysicsObject physicsObject) {
        this.setRenderCamera(new AttachedCamera(physicsObject));
    }

    public boolean isCameraAttachedToItem(PhysicsObject physicsObject) {
        return this.getCurrentCamera() instanceof AttachedCamera && ((AttachedCamera) this.getCurrentCamera()).getPhysXObject() == physicsObject;
    }

    @SuppressWarnings("all")
    public void renderScene(double deltaTime) throws InterruptedException {
        if (Scene.isSceneActive()) {
            Screen.setViewport(this.getWindowDimensions());
            if (this.getCurrentCamera() != null) {
                if (this.requestDestroyMap) {
                    Game.getGame().destroyMap();
                    this.requestDestroyMap = false;
                    return;
                }
                this.elapsedTime += deltaTime / PhysicThreadManager.getFrameTime();
                if (this.elapsedTime > 1.0d) {
                    SyncManger.SyncPhysicsAndRender.mark();
                    this.refresh = true;
                    synchronized (PhysicThreadManager.locker) {
                        PhysicThreadManager.locker.notifyAll();
                    }
                    this.elapsedTime %= 1.0d;
                }
                SyncManger.SyncPhysicsAndRender.blockCurrentThread();
                this.renderSceneInterpolated(this.elapsedTime);
            } else {
                this.getGuiRender().onRender(deltaTime);
            }
        }
    }

    public void requestDestroyMap() {
        this.requestDestroyMap = true;
    }

    @SuppressWarnings("all")
    public void renderSceneInterpolated(final double partialTicks) throws InterruptedException {
        this.getFrustumCulling().refreshFrustumCullingState(TransformationManager.instance.getProjectionMatrix(), TransformationManager.instance.getMainCameraViewMatrix());
        this.getSceneWorld().onWorldEntityUpdate(this.refresh, partialTicks);
        this.getCurrentCamera().updateCamera(partialTicks);
        TransformationManager.instance.updateViewMatrix(this.getCurrentCamera());
        this.getSceneRender().onRender(partialTicks, this.mainGroup);
        this.refresh = false;
    }

    public SceneRenderConveyor getSceneRender() {
        return this.sceneRenderConveyor;
    }

    public FrustumCulling getFrustumCulling() {
        return this.frustumCulling;
    }

    public class SceneRenderConveyor {
        private final FBOTexture2DProgram fboPsx;
        private final FBOTexture2DProgram fboBlur;
        private final FBOTexture2DProgram sceneFbo;
        private final FBOTexture2DProgram mixFbo;
        private final ShadowScene shadowScene;
        private final float[] blurKernel;
        private int CURRENT_DEBUG_MODE;
        private int oldPanic;

        public SceneRenderConveyor() {
            this.shadowScene = new ShadowScene(Scene.this);
            this.fboBlur = new FBOTexture2DProgram(true, false);
            this.sceneFbo = new FBOTexture2DProgram(true, false);
            this.fboPsx = new FBOTexture2DProgram(true, false);
            this.mixFbo = new FBOTexture2DProgram(true, false);
            this.blurKernel = this.blurKernels(8.0f);
            this.initShaders();
        }

        private void initShaders() {
            this.attachFBO(new Vector2i(this.getWindowDimensions().x, this.getWindowDimensions().y));
        }

        private float[] blurKernels(float sigma) {
            float[] kernel = new float[5];
            float weight = 0.0f;

            for (int i = 0; i < 5; i++) {
                float x = i - (5 / 2.0f);
                kernel[i] = (float) Math.exp(-(x * x) / (2.0f * sigma * sigma));
                weight += kernel[i];
            }

            for (int i = 0; i < 5; i++) {
                kernel[i] /= weight;
            }

            return kernel;
        }

        private void attachFBO(Vector2i dim) {
            this.fboBlur.clearFBO();
            this.sceneFbo.clearFBO();
            this.mixFbo.clearFBO();
            this.fboPsx.clearFBO();

            this.fboPsx.createFrameBuffer2DTexture(dim, new int[]{GL30.GL_COLOR_ATTACHMENT0, GL30.GL_COLOR_ATTACHMENT0, GL30.GL_COLOR_ATTACHMENT0}, true, false, GL30.GL_SRGB, GL30.GL_RGB, GL30.GL_LINEAR, GL30.GL_COMPARE_REF_TO_TEXTURE, GL30.GL_LESS, GL30.GL_CLAMP_TO_BORDER, null);
            this.fboBlur.createFrameBuffer2DTexture(dim, new int[]{GL30.GL_COLOR_ATTACHMENT0}, false, false, GL30.GL_SRGB, GL30.GL_RGB, GL30.GL_LINEAR, GL30.GL_COMPARE_REF_TO_TEXTURE, GL30.GL_LESS, GL30.GL_CLAMP_TO_EDGE, null);
            this.sceneFbo.createFrameBuffer2DTexture(dim, new int[]{GL30.GL_COLOR_ATTACHMENT0, GL30.GL_COLOR_ATTACHMENT1}, true, true, GL43.GL_RGB16F, GL30.GL_RGB, GL30.GL_LINEAR, GL30.GL_COMPARE_REF_TO_TEXTURE, GL30.GL_LESS, GL30.GL_CLAMP_TO_EDGE, null);
            this.mixFbo.createFrameBuffer2DTexture(dim, new int[]{GL30.GL_COLOR_ATTACHMENT0, GL30.GL_COLOR_ATTACHMENT1}, false, false, GL43.GL_RGB16F, GL30.GL_RGB, GL30.GL_LINEAR, GL30.GL_COMPARE_REF_TO_TEXTURE, GL30.GL_LESS, GL30.GL_CLAMP_TO_EDGE, null);
        }

        public void onWindowResize(Vector2i dim) {
            this.attachFBO(dim);
        }

        public int getCurrentDebugMode() {
            return this.CURRENT_DEBUG_MODE;
        }

        private void updateUbo() {
            Scene.getGameUboShader().performUniformBuffer(ResourceManager.shaderAssets.Misc, new float[]{Game.getGame().getScreen().getRenderTicks()});
            Environment environment = this.getRenderWorld().getEnvironment();
            FloatBuffer value1Buffer = MemoryUtil.memAllocFloat(4);
            value1Buffer.put(Game.getGame().getScreen().getScene().getSceneRender().getCurrentDebugMode() == 0 ? environment.getFog().getDensity() : 0.0f);
            value1Buffer.put((float) environment.getFog().getColor().x);
            value1Buffer.put((float) environment.getFog().getColor().y);
            value1Buffer.put((float) environment.getFog().getColor().z);
            value1Buffer.flip();
            Scene.getGameUboShader().performUniformBuffer(ResourceManager.shaderAssets.Fog, value1Buffer);
            MemoryUtil.memFree(value1Buffer);
        }

        public void onRender(double partialTicks, List<SceneRenderBase> mainGroup) {
            this.updateUbo();
            this.getRenderWorld().getEnvironment().onUpdate(this.getRenderWorld());

            if (this.checkPlayerCameraInWater()) {
                this.getRenderWorld().getEnvironment().setFog(this.getRenderWorld().getEnvironment().getWaterFog());
            } else {
                this.getRenderWorld().getEnvironment().setFog(this.getRenderWorld().getEnvironment().getWorldFog());
            }

            Scene.getGameUboShader().bind();
            Model<Format2D> model = MeshHelper.generatePlane2DModelInverted(new Vector2f(0.0f), new Vector2f(this.getWindowDimensions().x, this.getWindowDimensions().y), 0);
            GL30.glEnable(GL30.GL_DEPTH_TEST);
            this.renderSceneInFbo(partialTicks, mainGroup);
            GL30.glDisable(GL30.GL_DEPTH_TEST);

            this.mixFbo.bindFBO();
            this.sceneFbo.copyFBOtoFBO(this.mixFbo.getFrameBufferId(), new int[]{GL30.GL_COLOR_ATTACHMENT0, GL30.GL_COLOR_ATTACHMENT1}, this.getWindowDimensions());
            this.mixFbo.unBindFBO();

            this.blurShader(partialTicks, model);

            if (this.getCurrentDebugMode() == 0) {
                GL30.glEnable(GL30.GL_BLEND);
                GL30.glBlendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);
                this.fboPsx.bindFBO();
                this.fboPsx.connectTextureToBuffer(GL30.GL_COLOR_ATTACHMENT0, 0);
                this.renderMixedScene(partialTicks, model);
                this.renderDebugScreen(partialTicks);

                this.fboPsx.connectTextureToBuffer(GL30.GL_COLOR_ATTACHMENT0, 2);
                GL30.glClear(GL30.GL_COLOR_BUFFER_BIT);
                GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
                Scene.this.getInventoryRender().onRender(partialTicks);

                this.fboPsx.connectTextureToBuffer(GL30.GL_COLOR_ATTACHMENT0, 1);
                GL30.glClear(GL30.GL_COLOR_BUFFER_BIT);
                GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
                Scene.this.getGuiRender().onRender(partialTicks);

                this.fboPsx.unBindFBO();

                if (Scene.this.getCurrentCamera() != null) {
                    this.renderPsx(model);
                }

                GL30.glDisable(GL30.GL_BLEND);
            } else {
                this.renderMixedScene(partialTicks, model);
                this.renderDebugScreen(partialTicks);
                Scene.this.getInventoryRender().onRender(partialTicks);
                Scene.this.getGuiRender().onRender(partialTicks);
            }

            model.clean();
            Scene.getGameUboShader().unBind();
        }

        private void renderPsx(Model<Format2D> model) {
            KinematicPlayerSP kinematicPlayerSP = (KinematicPlayerSP) Game.getGame().getPlayerSP();

            this.getPostProcessingShader2().bind();
            this.getPostProcessingShader2().performUniform("texture_sampler", 0);
            this.getPostProcessingShader2().performUniform("texture_sampler_gui", 1);
            this.getPostProcessingShader2().performUniform("texture_sampler_inventory", 2);
            this.getPostProcessingShader2().performUniform("texture_screen", 3);
            this.getPostProcessingShader2().performUniform("texture_blood", 4);
            this.getPostProcessingShader2().performUniform("kill", kinematicPlayerSP.isKilled());
            this.getPostProcessingShader2().performUniform("victory", kinematicPlayerSP.isVictory());

            this.getPostProcessingShader2().performUniform("resolution", new Vector2f(this.getWindowDimensions().x, this.getWindowDimensions().y));
            this.getPostProcessingShader2().performUniform("e_lsd", 0);
            this.getPostProcessingShader2().performUniform("psx_gui_shake", Game.getGame().isPaused() ? 0 : 1);

            float panic = 1.0f - kinematicPlayerSP.getMind();
            int panic_i = (int) (Math.floor(panic * 10.0f));
            float panic_normalized = (panic_i) * 0.1f;

            if (panic_i > this.oldPanic) {
                Game.getGame().getSoundManager().playLocalSound(ResourceManager.soundAssetsLoader.crackling, SoundType.BACKGROUND_SOUND, 1.0f + panic_normalized * 0.5f, 1.0f);
            }

            this.oldPanic = panic_i;

            this.getPostProcessingShader2().performUniform("panic", panic_normalized);
            this.getPostProcessingShader2().performUniform("offset", 80.0f);
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

        private boolean checkPlayerCameraInWater() {
            for (ILiquid liquid : this.getRenderWorld().getWorld().getLiquids()) {
                ICamera camera = Scene.this.getCurrentCamera();
                Vector3d left = new Vector3d(liquid.getZone().getLocation()).sub(liquid.getZone().getSize().mul(0.5d));
                Vector3d right = new Vector3d(liquid.getZone().getLocation()).add(liquid.getZone().getSize().mul(0.5d));
                if (camera.getCamPosition().x >= left.x && camera.getCamPosition().y >= left.y && camera.getCamPosition().z >= left.z && camera.getCamPosition().x <= right.x && camera.getCamPosition().y <= right.y && camera.getCamPosition().z <= right.z) {
                    return true;
                }
            }
            return false;
        }

        private void renderSceneInFbo(double partialTicks, List<SceneRenderBase> mainList) {
            this.getShadowScene().renderAllModelsInShadowMap(this.getRenderWorld().getToRenderList());
            Screen.setViewport(Scene.this.getWindowDimensions());
            this.sceneFbo.bindFBO();
            GL30.glClear(GL30.GL_COLOR_BUFFER_BIT | GL30.GL_DEPTH_BUFFER_BIT | GL30.GL_STENCIL_BUFFER_BIT);
            GL30.glEnable(GL30.GL_BLEND);
            GL30.glBlendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);
            this.renderMainScene(partialTicks, mainList);
            GL30.glDisable(GL30.GL_BLEND);
            this.sceneFbo.unBindFBO();
        }

        private void renderMainScene(double partialTicks, List<SceneRenderBase> mainList) {
            for (SceneRenderBase sceneRenderBase : mainList) {
                sceneRenderBase.onRender(partialTicks);
            }
        }

        private void blurShader(double partialTicks, Model<Format2D> model) {
            this.fboBlur.bindFBO();
            this.getBlurShader().bind();
            this.getBlurShader().performArrayUniform("kernel", this.blurKernel);
            GL30.glActiveTexture(GL30.GL_TEXTURE0);
            this.mixFbo.bindTexture(1);
            this.getBlurShader().performUniform("texture_sampler", 0);
            this.getBlurShader().getUtils().performProjectionMatrix2d(model);
            Scene.renderModel(model, GL30.GL_TRIANGLES);
            this.getBlurShader().unBind();
            this.fboBlur.unBindFBO();
        }

        private void renderMixedScene(double partialTicks, Model<Format2D> model) {
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

        private void renderDebugScreen(double partialTicks) {
            if (this.getCurrentDebugMode() == 1) {
                Model<Format2D> model = MeshHelper.generatePlane2DModelInverted(new Vector2f(0.0f), new Vector2f(400.0f, 300.0f), 0);
                ResourceManager.shaderAssets.gui_image.bind();
                this.getBlurShader().performUniform("texture_sampler", 0);
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

        public ShadowScene getShadowScene() {
            return this.shadowScene;
        }

        public Vector2i getWindowDimensions() {
            return Scene.this.getWindowDimensions();
        }

        public SceneWorld getRenderWorld() {
            return Scene.this.getSceneWorld();
        }

        public void onStartRender() {
        }

        public void onStopRender() {
            this.fboBlur.clearFBO();
            this.sceneFbo.clearFBO();
            this.mixFbo.clearFBO();
            this.fboPsx.clearFBO();
        }

        public ShaderManager getBlurShader() {
            return ResourceManager.shaderAssets.post_blur;
        }

        public ShaderManager getPostProcessingShader() {
            return ResourceManager.shaderAssets.post_render_1;
        }

        public ShaderManager getPostProcessingShader2() {
            return ResourceManager.shaderAssets.post_psx;
        }
    }
}
