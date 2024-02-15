package ru.BouH.engine.render.scene;

import org.joml.*;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL43;
import org.lwjgl.system.MemoryUtil;
import ru.BouH.engine.game.Game;
import ru.BouH.engine.game.controller.input.IController;
import ru.BouH.engine.game.resources.ResourceManager;
import ru.BouH.engine.game.resources.assets.materials.Material;
import ru.BouH.engine.game.resources.assets.models.Model;
import ru.BouH.engine.game.resources.assets.models.basic.MeshHelper;
import ru.BouH.engine.game.resources.assets.models.formats.Format2D;
import ru.BouH.engine.game.resources.assets.models.formats.Format3D;
import ru.BouH.engine.game.resources.assets.models.mesh.ModelNode;
import ru.BouH.engine.game.resources.assets.shaders.ShaderManager;
import ru.BouH.engine.game.synchronizing.SyncManger;
import ru.BouH.engine.physics.liquids.ILiquid;
import ru.BouH.engine.physics.world.object.WorldItem;
import ru.BouH.engine.physics.world.timer.PhysicThreadManager;
import ru.BouH.engine.proxy.LocalPlayer;
import ru.BouH.engine.render.environment.Environment;
import ru.BouH.engine.render.scene.scene_render.groups.DebugRender;
import ru.BouH.engine.render.transformation.TransformationManager;
import ru.BouH.engine.render.environment.shadow.ShadowScene;
import ru.BouH.engine.render.frustum.FrustumCulling;
import ru.BouH.engine.render.scene.objects.IModeledSceneObject;
import ru.BouH.engine.render.scene.objects.items.PhysicsObject;
import ru.BouH.engine.render.scene.programs.FBOTexture2DProgram;
import ru.BouH.engine.render.scene.world.SceneWorld;
import ru.BouH.engine.render.scene.world.camera.AttachedCamera;
import ru.BouH.engine.render.scene.world.camera.FreeCamera;
import ru.BouH.engine.render.scene.world.camera.ICamera;
import ru.BouH.engine.render.screen.Screen;
import ru.BouH.engine.render.screen.window.Window;

import java.lang.Math;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Scene implements IScene {
    public static boolean testTrigger = false;
    private final List<SceneRenderBase> sceneRenderBases;
    private final Screen screen;
    private final Window window;
    private final SceneWorld sceneWorld;
    private final FrustumCulling frustumCulling;
    private final SceneRenderConveyor sceneRenderConveyor;
    public static float elapsedRenderTicks;
    private double elapsedTime;
    private List<SceneRenderBase> mainGroup;
    private List<SceneRenderBase> sideGroup;
    private boolean refresh;
    private ICamera currentCamera;

    public Scene(Screen screen, SceneWorld sceneWorld) {
        this.sceneWorld = sceneWorld;
        this.screen = screen;
        this.mainGroup = new ArrayList<>();
        this.sideGroup = new ArrayList<>();
        this.window = this.getScreen().getWindow();
        this.frustumCulling = new FrustumCulling();
        this.sceneRenderBases = new ArrayList<>();
        this.sceneRenderConveyor = new SceneRenderConveyor();
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

    public static int getPostRender() {
        return Game.getGame().getScreen().getScene().getSceneRender().getCurrentRenderPostMode();
    }

    public static void setSceneDebugMode(int a) {
        Game.getGame().getScreen().getScene().setDebugMode(a);
    }

    public static void setScenePostRender(int a) {
        Game.getGame().getScreen().getScene().setRenderPostMode(a);
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
        }
    }

    public static void activeGlTexture(int code) {
        GL30.glActiveTexture(GL13.GL_TEXTURE0 + code);
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
        this.mainGroup = this.getRenderQueueContainer().stream().filter(e -> e.getRenderGroup().isMainSceneGroup()).sorted(Comparator.comparingInt(SceneRenderBase::getRenderPriority)).collect(Collectors.toList());
        this.sideGroup = this.getRenderQueueContainer().stream().filter(e -> !e.getRenderGroup().isMainSceneGroup()).sorted(Comparator.comparingInt(SceneRenderBase::getRenderPriority)).collect(Collectors.toList());
    }

    public void preRender() {
        this.getSceneWorld().updateToAddQueue();
        this.collectRenderBases();
        ResourceManager.shaderAssets.startShaders();
        this.getSceneWorld().setFrustumCulling(this.getFrustumCulling());
        if (LocalPlayer.VALID_PL) {
            this.enableAttachedCamera(Game.getGame().getPlayerSP());
        }
        Game.getGame().getLogManager().log("Starting scene rendering: ");
        this.getSceneRender().onStartRender();
        for (SceneRenderBase sceneRenderBase : this.sceneRenderBases) {
            Game.getGame().getLogManager().log("Starting " + sceneRenderBase.getRenderGroup().getId() + " scene");
            sceneRenderBase.onStartRender();
            Game.getGame().getLogManager().log("Scene " + sceneRenderBase.getRenderGroup().getId() + " successfully started!");
        }
    }

    public void onWindowResize(Vector2i dim) {
        this.getSceneRender().onWindowResize(dim);
    }

    public void setRenderPostMode(int a) {
        this.getSceneRender().CURRENT_POST_RENDER = a;
        if (this.getSceneRender().CURRENT_POST_RENDER > 3) {
            this.getSceneRender().CURRENT_POST_RENDER = 0;
        }
    }

    public static ShaderManager getGameUboShader() {
        return ResourceManager.shaderAssets.gameUbo;
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
            if (this.getCurrentCamera() != null) {
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
            }
        }
    }

    @SuppressWarnings("all")
    public void renderSceneInterpolated(final double partialTicks) throws InterruptedException {
        this.getFrustumCulling().refreshFrustumCullingState(TransformationManager.instance.getProjectionMatrix(), TransformationManager.instance.getMainCameraViewMatrix());
        this.getSceneWorld().onWorldEntityUpdate(this.refresh, partialTicks);
        this.getCurrentCamera().updateCamera(partialTicks);
        TransformationManager.instance.updateViewMatrix(this.getCurrentCamera());
        this.getSceneRender().onRender(partialTicks, this.mainGroup, this.sideGroup);
        this.refresh = false;
    }

    public SceneRenderConveyor getSceneRender() {
        return this.sceneRenderConveyor;
    }

    public FrustumCulling getFrustumCulling() {
        return this.frustumCulling;
    }

    public void postRender() {
        Game.getGame().getLogManager().log("Stopping scene rendering: ");
        this.getSceneRender().onStopRender();
        for (SceneRenderBase sceneRenderBase : this.getRenderQueueContainer()) {
            Game.getGame().getLogManager().log("Stopping " + sceneRenderBase.getRenderGroup().getId() + " scene");
            sceneRenderBase.onStopRender();
            Game.getGame().getLogManager().log("Scene " + sceneRenderBase.getRenderGroup().getId() + " successfully stopped!");
        }
        Game.getGame().getLogManager().log("Destroying resources!");
        Game.getGame().getResourceManager().destroy();
        Game.getGame().getLogManager().log("Scene rendering stopped");
    }

    public class SceneRenderConveyor {
        private final FBOTexture2DProgram fboBlur;
        private final FBOTexture2DProgram sceneFbo;
        private final FBOTexture2DProgram mixFbo;
        private final ShadowScene shadowScene;
        private final float[] blurKernel;
        private int CURRENT_POST_RENDER = 0;
        private int CURRENT_DEBUG_MODE;

        public SceneRenderConveyor() {
            this.shadowScene = new ShadowScene(Scene.this);
            this.fboBlur = new FBOTexture2DProgram(true, false);
            this.sceneFbo = new FBOTexture2DProgram(true, false);
            this.mixFbo = new FBOTexture2DProgram(true, false);
            this.blurKernel = this.blurKernels(8.0f);
            this.initShaders();
        }

        private void initShaders() {
            this.attachFBO(new Vector2i((int) this.getWindowDimensions().x, (int) this.getWindowDimensions().y));
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
            this.fboBlur.createFrameBuffer2DTexture(dim, new int[]{GL30.GL_COLOR_ATTACHMENT0}, false, false, GL30.GL_SRGB_ALPHA, GL30.GL_RGB, GL30.GL_LINEAR, GL30.GL_COMPARE_REF_TO_TEXTURE, GL30.GL_LESS, GL30.GL_CLAMP_TO_EDGE, null);
            this.sceneFbo.createFrameBuffer2DTexture(dim, new int[]{GL30.GL_COLOR_ATTACHMENT0, GL30.GL_COLOR_ATTACHMENT1}, true, true, GL43.GL_RGB16F, GL30.GL_RGB, GL30.GL_LINEAR, GL30.GL_COMPARE_REF_TO_TEXTURE, GL30.GL_LESS, GL30.GL_CLAMP_TO_EDGE, null);
            this.mixFbo.createFrameBuffer2DTexture(dim, new int[]{GL30.GL_COLOR_ATTACHMENT0, GL30.GL_COLOR_ATTACHMENT1}, false, false, GL43.GL_RGB16F, GL30.GL_RGB, GL30.GL_LINEAR, GL30.GL_COMPARE_REF_TO_TEXTURE, GL30.GL_LESS, GL30.GL_CLAMP_TO_EDGE, null);
        }

        public void onWindowResize(Vector2i dim) {
            this.attachFBO(dim);
        }

        public int getCurrentDebugMode() {
            return this.CURRENT_DEBUG_MODE;
        }

        public int getCurrentRenderPostMode() {
            return this.CURRENT_POST_RENDER;
        }

        private void updateUbo() {
            Scene.getGameUboShader().performUniformBuffer(ResourceManager.shaderAssets.Misc, new float[]{ this.getRenderWorld().getElapsedRenderTicks() });
            Environment environment = this.getRenderWorld().getEnvironment();
            FloatBuffer value1Buffer = MemoryUtil.memAllocFloat(4);
            value1Buffer.put(environment.getFog().getDensity());
            value1Buffer.put((float) environment.getFog().getColor().x);
            value1Buffer.put((float) environment.getFog().getColor().y);
            value1Buffer.put((float) environment.getFog().getColor().z);
            value1Buffer.flip();
            Scene.getGameUboShader().performUniformBuffer(ResourceManager.shaderAssets.Fog, value1Buffer);
            MemoryUtil.memFree(value1Buffer);
        }

        public void onRender(double partialTicks, List<SceneRenderBase> mainGroup, List<SceneRenderBase> sideGroup) {
            this.updateUbo();
            this.getRenderWorld().getEnvironment().onUpdate(this.getRenderWorld());

            if (this.checkPlayerCameraInWater()) {
                this.getRenderWorld().getEnvironment().setFog(this.getRenderWorld().getEnvironment().getWaterFog());
            } else {
                this.getRenderWorld().getEnvironment().setFog(this.getRenderWorld().getEnvironment().getWorldFog());
            }

            Scene.getGameUboShader().bind();
            Model<Format2D> model = MeshHelper.generatePlane2DModelInverted(new Vector2d(0.0d), new Vector2d(this.getWindowDimensions().x, this.getWindowDimensions().y), 0);
            GL30.glEnable(GL30.GL_DEPTH_TEST);
            this.renderSceneInFbo(partialTicks, mainGroup);
            GL30.glDisable(GL30.GL_DEPTH_TEST);

            this.mixFbo.bindFBO();
            this.sceneFbo.copyFBOtoFBO(this.mixFbo.getFrameBufferId(), new int[] {GL30.GL_COLOR_ATTACHMENT0, GL30.GL_COLOR_ATTACHMENT1}, this.getWindowDimensions());
            this.mixFbo.unBindFBO();

            this.twoPassBlurShader(partialTicks, model);
            this.renderMixedScene(partialTicks, model);
            this.renderDebugScreen(partialTicks);
            this.renderSideGroup(partialTicks, sideGroup);

            model.clean();
            Scene.getGameUboShader().unBind();
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

        private void renderSideGroup(double partialTicks, List<SceneRenderBase> sideGroup) {
            for (SceneRenderBase sceneRenderBase : sideGroup) {
                sceneRenderBase.onRender(partialTicks);
            }
        }

        private void renderSceneInFbo(double partialTicks, List<SceneRenderBase> mainList) {
            this.getShadowScene().renderAllModelsInShadowMap(this.getRenderWorld().getToRenderList());
            Screen.setViewport(Scene.this.getWindowDimensions());
            this.sceneFbo.bindFBO();
            GL30.glClear(GL30.GL_COLOR_BUFFER_BIT | GL30.GL_DEPTH_BUFFER_BIT);
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

        private void twoPassBlurShader(double partialTicks, Model<Format2D> model) {
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
            this.getPostProcessingShader().performUniform("post_mode", Scene.testTrigger ? 1 : this.getCurrentRenderPostMode());
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
                Model<Format2D> model = MeshHelper.generatePlane2DModelInverted(new Vector2d(0.0d), new Vector2d(400.0d, 300.0d), 0);
                ResourceManager.shaderAssets.guiShader.bind();
                this.getBlurShader().performUniform("texture_sampler", 0);
                GL30.glActiveTexture(GL30.GL_TEXTURE0);
                this.mixFbo.bindTexture(1);
                ResourceManager.shaderAssets.guiShader.getUtils().performProjectionMatrix2d(model);
                Scene.renderModel(model, GL30.GL_TRIANGLES);
                ResourceManager.shaderAssets.guiShader.unBind();
                model.clean();

                model = MeshHelper.generatePlane2DModelInverted(new Vector2d(0.0d, 350.0d), new Vector2d(400.0d, 650.0d), 0);
                ResourceManager.shaderAssets.guiShader.bind();
                ResourceManager.shaderAssets.guiShader.performUniform("texture_sampler", 0);
                GL30.glActiveTexture(GL30.GL_TEXTURE0);
                this.getShadowScene().getFrameBufferObjectProgram().bindTexture(0);
                ResourceManager.shaderAssets.guiShader.getUtils().performProjectionMatrix2d(model);
                Scene.renderModel(model, GL30.GL_TRIANGLES);
                this.getShadowScene().getFrameBufferObjectProgram().unBindTexture();
                ResourceManager.shaderAssets.guiShader.unBind();
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
        }

        public ShaderManager getBlurShader() {
            return ResourceManager.shaderAssets.post_blur;
        }

        public ShaderManager getPostProcessingShader() {
            return ResourceManager.shaderAssets.post_render_1;
        }
    }
}
