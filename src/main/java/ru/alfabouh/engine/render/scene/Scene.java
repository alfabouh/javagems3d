package ru.alfabouh.engine.render.scene;

import org.joml.Vector2i;
import org.joml.Vector3d;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;
import ru.alfabouh.engine.game.Game;
import ru.alfabouh.engine.game.controller.input.IController;
import ru.alfabouh.engine.game.resources.assets.materials.Material;
import ru.alfabouh.engine.game.resources.assets.models.Model;
import ru.alfabouh.engine.game.resources.assets.models.formats.Format3D;
import ru.alfabouh.engine.game.resources.assets.models.mesh.ModelNode;
import ru.alfabouh.engine.game.resources.assets.shaders.ShaderManager;
import ru.alfabouh.engine.game.synchronizing.SyncManger;
import ru.alfabouh.engine.physics.world.object.WorldItem;
import ru.alfabouh.engine.physics.world.timer.PhysicThreadManager;
import ru.alfabouh.engine.render.frustum.FrustumCulling;
import ru.alfabouh.engine.render.scene.gui.base.GUI;
import ru.alfabouh.engine.render.scene.gui.base.GameGUI;
import ru.alfabouh.engine.render.scene.objects.IModeledSceneObject;
import ru.alfabouh.engine.render.scene.objects.items.PhysicsObject;
import ru.alfabouh.engine.render.scene.world.SceneWorld;
import ru.alfabouh.engine.render.scene.world.camera.AttachedCamera;
import ru.alfabouh.engine.render.scene.world.camera.FreeCamera;
import ru.alfabouh.engine.render.scene.world.camera.ICamera;
import ru.alfabouh.engine.render.screen.Screen;
import ru.alfabouh.engine.render.screen.window.Window;
import ru.alfabouh.engine.render.transformation.TransformationManager;

public class Scene implements IScene {
    private final Screen screen;
    private final Window window;
    private final FrustumCulling frustumCulling;
    private final SceneRender sceneRender;
    private final GameGUI gui;
    private final SceneData sceneData;

    private double elapsedTime;
    private boolean refresh;
    private boolean requestDestroyMap;

    public Scene(Screen screen, SceneWorld sceneWorld) {
        this.sceneData = new SceneData(sceneWorld, null);
        this.screen = screen;
        this.window = this.getScreen().getWindow();
        this.frustumCulling = new FrustumCulling();
        this.gui = new GameGUI();
        this.sceneRender = new SceneRender(this.getSceneData());
    }

    public void preRender() {
        this.getGui().initMainMenu();
        Game.getGame().getPhysicThreadManager().getPhysicsTimer().jbDebugDraw.setupBuffers();
        this.getSceneWorld().setFrustumCulling(this.getFrustumCulling());
        Game.getGame().getLogManager().log("Starting scene rendering!");
        this.getSceneRender().onStartRender();
        Game.getGame().getLogManager().log("Scene rendering started!");
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
                this.getSceneRender().onRender(deltaTime);
            }
        }
    }

    @SuppressWarnings("all")
    public void renderSceneInterpolated(final double partialTicks) throws InterruptedException {
        this.getFrustumCulling().refreshFrustumCullingState(TransformationManager.instance.getProjectionMatrix(), TransformationManager.instance.getMainCameraViewMatrix());
        this.getSceneWorld().onWorldEntityUpdate(this.refresh, partialTicks);
        this.getCurrentCamera().updateCamera(partialTicks);
        TransformationManager.instance.updateViewMatrix(this.getCurrentCamera());
        this.getSceneRender().onRender(partialTicks);
        this.refresh = false;
    }

    public void postRender() {
        Game.getGame().getPhysicThreadManager().getPhysicsTimer().jbDebugDraw.cleanup();
        Game.getGame().getLogManager().log("Stopping scene rendering!");
        this.getSceneRender().onStopRender();
        Game.getGame().getLogManager().log("Destroying resources!");
        this.removeGui();
        Game.getGame().getLogManager().log("Scene rendering stopped");
    }

    public static void activeGlTexture(int code) {
        GL30.glActiveTexture(GL13.GL_TEXTURE0 + code);
    }

    public void removeGui() {
        this.getGui().setCurrentGui(null);
    }

    public void setGui(GUI gui) {
        this.getGui().setCurrentGui(gui);
    }

    public void setRenderCamera(ICamera camera) {
        this.getSceneData().setCamera(camera);
    }

    public void requestDestroyMap() {
        this.requestDestroyMap = true;
    }

    public void onWindowResize(Vector2i dim) {
        this.getSceneRender().onWindowResize(dim);
        this.getGui().onWindowResize(dim);
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

    public static boolean isSceneActive() {
        return Screen.isScreenActive();
    }

    public static void setCamera(ICamera camera) {
        Game.getGame().getScreen().getScene().setRenderCamera(camera);
    }

    public static void checkGLErrors() {
        int errorCode;
        while ((errorCode = GL11.glGetError()) != GL11.GL_NO_ERROR) {
            String error;
            switch (errorCode) {
                case GL11.GL_INVALID_ENUM:
                    error = "INVALID_ENUM";
                    break;
                case GL11.GL_INVALID_VALUE:
                    error = "INVALID_VALUE";
                    break;
                case GL11.GL_INVALID_OPERATION:
                    error = "INVALID_OPERATION";
                    break;
                case GL11.GL_STACK_OVERFLOW:
                    error = "STACK_OVERFLOW";
                    break;
                case GL11.GL_STACK_UNDERFLOW:
                    error = "STACK_UNDERFLOW";
                    break;
                case GL11.GL_OUT_OF_MEMORY:
                    error = "OUT_OF_MEMORY";
                    break;
                case GL30.GL_INVALID_FRAMEBUFFER_OPERATION:
                    error = "INVALID_FRAMEBUFFER_OPERATION";
                    break;
                default:
                    error = "UNKNOWN";
                    break;
            }
            Game.getGame().getLogManager().warn("GL ERROR: " + error);
        }
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
                if (sceneObject.getModelRenderParams().getAlphaDiscardValue() > 0) {
                    GL30.glDisable(GL30.GL_BLEND);
                }
            }
            for (ModelNode modelNode : model.getMeshDataGroup().getModelNodeList()) {
                shaderManager.getUtils().performModelMaterialOnShader(overMaterial != null ? overMaterial : modelNode.getMaterial(), sceneObject.getModelRenderParams().isPassShadowsInfoInRender());
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

    public GameGUI getGui() {
        return this.gui;
    }

    public SceneData getSceneData() {
        return this.sceneData;
    }

    public ICamera getCurrentCamera() {
        return this.getSceneData().getCamera();
    }

    public SceneWorld getSceneWorld() {
        return this.getSceneData().getSceneWorld();
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

    public boolean isCameraAttachedToItem(PhysicsObject physicsObject) {
        return this.getCurrentCamera() instanceof AttachedCamera && ((AttachedCamera) this.getCurrentCamera()).getPhysXObject() == physicsObject;
    }

    public SceneRender getSceneRender() {
        return this.sceneRender;
    }

    public FrustumCulling getFrustumCulling() {
        return this.frustumCulling;
    }

    public static class SceneData {
        private ICamera camera;
        private SceneWorld sceneWorld;

        public SceneData(SceneWorld sceneWorld, ICamera camera) {
            this.sceneWorld = sceneWorld;
            this.camera = camera;
        }

        public void setCamera(ICamera camera) {
            this.camera = camera;
        }

        public void setSceneWorld(SceneWorld sceneWorld) {
            this.sceneWorld = sceneWorld;
        }

        public SceneWorld getSceneWorld() {
            return this.sceneWorld;
        }

        public ICamera getCamera() {
            return this.camera;
        }
    }
}