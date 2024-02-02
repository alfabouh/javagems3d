package ru.BouH.engine.render.scene;

import org.joml.*;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL43;
import ru.BouH.engine.game.Game;
import ru.BouH.engine.game.controller.IController;
import ru.BouH.engine.game.resources.ResourceManager;
import ru.BouH.engine.game.resources.assets.models.Model;
import ru.BouH.engine.game.resources.assets.models.basic.MeshHelper;
import ru.BouH.engine.game.resources.assets.models.formats.Format2D;
import ru.BouH.engine.game.resources.assets.models.formats.Format3D;
import ru.BouH.engine.game.resources.assets.models.mesh.ModelNode;
import ru.BouH.engine.game.resources.assets.shaders.ShaderManager;
import ru.BouH.engine.game.synchronizing.SyncManger;
import ru.BouH.engine.physics.world.object.WorldItem;
import ru.BouH.engine.physics.world.timer.PhysicThreadManager;
import ru.BouH.engine.proxy.LocalPlayer;
import ru.BouH.engine.render.RenderManager;
import ru.BouH.engine.render.environment.shadow.ShadowScene;
import ru.BouH.engine.render.frustum.FrustumCulling;
import ru.BouH.engine.render.scene.fabric.constraints.ModelRenderConstraints;
import ru.BouH.engine.render.scene.fabric.models.base.IRenderSceneModel;
import ru.BouH.engine.render.scene.objects.items.PhysicsObject;
import ru.BouH.engine.render.scene.programs.FBOTexture2DProgram;
import ru.BouH.engine.render.scene.world.SceneWorld;
import ru.BouH.engine.render.scene.world.camera.AttachedCamera;
import ru.BouH.engine.render.scene.world.camera.FreeCamera;
import ru.BouH.engine.render.scene.world.camera.ICamera;
import ru.BouH.engine.render.screen.Screen;
import ru.BouH.engine.render.screen.window.Window;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.Math;
import java.nio.ByteBuffer;
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

    public static void renderModelWithMaterials(Model<?> model, ShaderManager shaderManager, ModelRenderConstraints modelRenderConstraints, int code) {
        Scene.renderModelWithMaterials(model, shaderManager, modelRenderConstraints, code, null);
    }

    @SuppressWarnings("all")
    public static void renderModelWithMaterials(Model<?> model, ShaderManager shaderManager, ModelRenderConstraints modelRenderConstraints, int code, Matrix4d view) {
        if (model == null || model.getMeshDataGroup() == null) {
            return;
        }
        if (model.getFormat() instanceof Format3D) {
            Model<Format3D> format3DModel = (Model<Format3D>) model;
            Matrix4d modelMatrix = RenderManager.instance.getModelMatrix(format3DModel);
            if (view == null) {
                shaderManager.getUtils().passViewAndModelMatrices(format3DModel);
            } else {
                shaderManager.getUtils().passViewAndModelMatrices(view, format3DModel);
            }
        }
        for (ModelNode modelNode : model.getMeshDataGroup().getModelNodeList()) {
            shaderManager.getUtils().performConstraintsOnShader(modelRenderConstraints);
            shaderManager.getUtils().performModelMaterialOnShader(modelNode.getMaterial(), modelRenderConstraints.isShadowReceiver());
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

    public static void renderEntity(PhysicsObject physicsObject) {
        Scene scene = Game.getGame().getScreen().getScene();
        if (physicsObject != null) {
            Model<Format3D> model = physicsObject.getModel3D();
            if (model == null || model.getMeshDataGroup() == null) {
                return;
            }
            ShaderManager shaderManager = physicsObject.getShaderManager();
            shaderManager.getUtils().passViewAndModelMatrices(model);
            shaderManager.getUtils().performConstraintsOnShader(physicsObject.getRenderData().getModelRenderConstraints());
            shaderManager.performUniform("texture_scaling", physicsObject.getRenderData().getModelTextureScaling());
            for (ModelNode modelNode : model.getMeshDataGroup().getModelNodeList()) {
                shaderManager.getUtils().performModelMaterialOnShader(physicsObject.getRenderData().getOverObjectMaterial() != null ? physicsObject.getRenderData().getOverObjectMaterial() : modelNode.getMaterial(), physicsObject.getRenderData().getModelRenderConstraints().isShadowReceiver());
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

    public static void glClear() {
        GL30.glClear(GL30.GL_COLOR_BUFFER_BIT | GL30.GL_DEPTH_BUFFER_BIT | GL30.GL_STENCIL_BUFFER_BIT);
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

    public Vector2d getWindowDimensions() {
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
        this.getFrustumCulling().refreshFrustumCullingState(RenderManager.instance.getProjectionMatrix(), RenderManager.instance.getViewMatrix());
        this.getSceneWorld().onWorldEntityUpdate(this.refresh, partialTicks);
        this.getCurrentCamera().updateCamera(partialTicks);
        RenderManager.instance.updateViewMatrix(this.getCurrentCamera());
        this.getSceneRender().onRender(partialTicks, this.mainGroup, this.sideGroup);
        this.refresh = false;
    }

    public SceneRenderConveyor getSceneRender() {
        return this.sceneRenderConveyor;
    }

    public void takeScreenshot() {
        this.getSceneRender().takeScreenshot();
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
        private final ShadowScene shadowScene;
        private final float[] blurKernel;
        private int CURRENT_POST_RENDER = 0;
        private int CURRENT_DEBUG_MODE;
        private boolean wantsTakeScreenshot;

        public SceneRenderConveyor() {
            this.shadowScene = new ShadowScene(Scene.this);
            this.fboBlur = new FBOTexture2DProgram(true, false);
            this.sceneFbo = new FBOTexture2DProgram(true, false);
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
            this.fboBlur.createFrameBuffer2DTexture(dim, new int[]{GL30.GL_COLOR_ATTACHMENT0, GL30.GL_COLOR_ATTACHMENT1}, false, GL30.GL_SRGB_ALPHA, GL30.GL_RGBA, GL30.GL_LINEAR, GL30.GL_COMPARE_REF_TO_TEXTURE, GL30.GL_LESS, GL30.GL_CLAMP_TO_EDGE, null);
            this.sceneFbo.createFrameBuffer2DTexture(dim, new int[]{GL30.GL_COLOR_ATTACHMENT0, GL30.GL_COLOR_ATTACHMENT1}, true, GL43.GL_RGB16F, GL30.GL_RGBA, GL30.GL_LINEAR, GL30.GL_COMPARE_REF_TO_TEXTURE, GL30.GL_LESS, GL30.GL_CLAMP_TO_EDGE, null);
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

        public void onRender(double partialTicks, List<SceneRenderBase> mainGroup, List<SceneRenderBase> sideGroup) {
            Scene.getGameUboShader().performUniformBuffer(ResourceManager.shaderAssets.Misc, new float[]{SceneWorld.elapsedRenderTicks});
            this.getRenderWorld().getEnvironment().onUpdate(this.getRenderWorld());

            Scene.getGameUboShader().bind();
            Model<Format2D> model = MeshHelper.generatePlane2DModelInverted(new Vector2d(0.0d), new Vector2d(this.getWindowDimensions().x, this.getWindowDimensions().y), 0);

            GL30.glEnable(GL30.GL_STENCIL_TEST);
            this.renderSceneInFbo(partialTicks, mainGroup);
            this.twoPassBlurShader(partialTicks, model);
            this.renderMixedScene(partialTicks, model);
            this.renderDebugScreen(partialTicks);
            this.renderSideGroup(partialTicks, sideGroup);
            GL30.glDisable(GL30.GL_STENCIL_TEST);

            if (this.wantsTakeScreenshot) {
                this.writeBufferInFile();
                this.wantsTakeScreenshot = false;
            }

            model.clean();
            Scene.getGameUboShader().unBind();
        }

        private void renderSideGroup(double partialTicks, List<SceneRenderBase> sideGroup) {
            for (SceneRenderBase sceneRenderBase : sideGroup) {
                sceneRenderBase.onRender(partialTicks);
            }
        }

        private void renderSceneInFbo(double partialTicks, List<SceneRenderBase> mainList) {
            GL30.glEnable(GL30.GL_DEPTH_TEST);
            this.getShadowScene().concatenateListsAndRender(this.getRenderWorld().getSceneModelsList(), this.getRenderWorld().getEntityList());
            Screen.setViewport(Scene.this.getWindowDimensions());
            this.sceneFbo.bindFBO();
            Scene.glClear();
            this.renderMainScene(partialTicks, mainList);
            this.renderSceneModels(partialTicks);
            this.sceneFbo.unBindFBO();
            GL30.glDisable(GL30.GL_DEPTH_TEST);
        }


        private void twoPassBlurShader(double partialTicks, Model<Format2D> model) {
            this.fboBlur.bindFBO();
            this.getBlurShader().bind();
            this.getBlurShader().performArrayUniform("kernel", this.blurKernel);
            GL30.glActiveTexture(GL30.GL_TEXTURE0);
            this.sceneFbo.bindTexture(1);
            this.getBlurShader().performUniform("texture_sampler", 0);
            Scene.glClear();
            this.getBlurShader().getUtils().performProjectionMatrix2d(model);
            Scene.renderModel(model, GL30.GL_TRIANGLES);
            this.sceneFbo.unBindTexture();
            this.getBlurShader().unBind();
            this.fboBlur.unBindFBO();
        }

        private void renderDebugScreen(double partialTicks) {
            if (this.getCurrentDebugMode() == 1) {
                Model<Format2D> model = MeshHelper.generatePlane2DModelInverted(new Vector2d(0.0d), new Vector2d(400.0d, 300.0d), 0);
                ResourceManager.shaderAssets.guiShader.bind();
                this.getBlurShader().performUniform("texture_sampler", 0);
                GL30.glActiveTexture(GL30.GL_TEXTURE0);
                this.sceneFbo.bindTexture(1);
                ResourceManager.shaderAssets.guiShader.getUtils().performProjectionMatrix2d(model);
                Scene.renderModel(model, GL30.GL_TRIANGLES);
                ResourceManager.shaderAssets.guiShader.unBind();

                model.clean();
                model = MeshHelper.generatePlane2DModelInverted(new Vector2d(0.0d, 350.0d), new Vector2d(400.0d, 650.0d), 0);
                ResourceManager.shaderAssets.depth_test.bind();
                ResourceManager.shaderAssets.depth_test.performUniform("texture_sampler", 0);
                GL30.glActiveTexture(GL30.GL_TEXTURE0);
                this.getShadowScene().getFrameBufferObjectProgram().bindTexture(0);
                ResourceManager.shaderAssets.depth_test.getUtils().performProjectionMatrix2d(model);
                Scene.renderModel(model, GL30.GL_TRIANGLES);
                this.getShadowScene().getFrameBufferObjectProgram().unBindTexture();
                ResourceManager.shaderAssets.depth_test.unBind();
                model.clean();
            }
        }

        private void renderMixedScene(double partialTicks, Model<Format2D> model) {
            this.getPostProcessingShader().bind();
            this.getPostProcessingShader().performUniform("texture_sampler", 0);
            this.getPostProcessingShader().performUniform("blur_sampler", 1);
            this.getPostProcessingShader().performUniform("post_mode", Scene.testTrigger ? 1 : this.getCurrentRenderPostMode());
            GL30.glActiveTexture(GL30.GL_TEXTURE0);
            this.sceneFbo.bindTexture(0);
            GL30.glActiveTexture(GL30.GL_TEXTURE1);
            this.fboBlur.bindTexture(1);
            Scene.glClear();
            this.getPostProcessingShader().getUtils().performProjectionMatrix2d(model);
            Scene.renderModel(model, GL30.GL_TRIANGLES);
            this.fboBlur.unBindTexture();
            this.sceneFbo.unBindTexture();
            this.getPostProcessingShader().unBind();
        }

        private void renderSceneModels(double partialTicks) {
            for (IRenderSceneModel renderSceneModel : this.getRenderWorld().getSceneModelsList()) {
                renderSceneModel.onRender(partialTicks);
            }
        }

        public ShadowScene getShadowScene() {
            return this.shadowScene;
        }

        public Vector2d getWindowDimensions() {
            return Scene.this.getWindowDimensions();
        }

        public SceneWorld getRenderWorld() {
            return Scene.this.getSceneWorld();
        }

        private void renderMainScene(double partialTicks, List<SceneRenderBase> mainList) {
            for (SceneRenderBase sceneRenderBase : mainList) {
                sceneRenderBase.onRender(partialTicks);
            }
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

        private void writeBufferInFile() {
            Vector2d vector2d = Game.getGame().getScreen().getDimensions();
            int w = (int) vector2d.x;
            int h = (int) vector2d.y;
            int i1 = w * h;
            ByteBuffer p = ByteBuffer.allocateDirect(i1 * 4);
            GL30.glReadBuffer(GL30.GL_COLOR_ATTACHMENT0);
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
                        image.setRGB(x, (int) (vector2d.y - y - 1), rgb);
                    }
                }
                String builder = "screen.png";
                ImageIO.write(image, "PNG", new File(builder));
            } catch (IOException e) {
                Game.getGame().getLogManager().error(e.getMessage());
            }
        }

        public void takeScreenshot() {
            this.wantsTakeScreenshot = true;
        }
    }
}
