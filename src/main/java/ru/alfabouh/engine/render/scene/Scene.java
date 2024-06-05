package ru.alfabouh.engine.render.scene;

import org.joml.Vector2i;
import org.joml.Vector3d;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;
import ru.alfabouh.engine.JGems;
import ru.alfabouh.engine.render.scene.immediate_gui.ImmediateUI;
import ru.alfabouh.engine.system.controller.input.IController;
import ru.alfabouh.engine.system.resources.assets.materials.Material;
import ru.alfabouh.engine.system.resources.assets.models.Model;
import ru.alfabouh.engine.system.resources.assets.models.formats.Format3D;
import ru.alfabouh.engine.system.resources.assets.models.mesh.ModelNode;
import ru.alfabouh.engine.system.resources.assets.shaders.ShaderManager;
import ru.alfabouh.engine.system.synchronizing.SyncManager;
import ru.alfabouh.engine.physics.world.object.WorldItem;
import ru.alfabouh.engine.physics.world.timer.PhysicThreadManager;
import ru.alfabouh.engine.render.frustum.FrustumCulling;
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
    private final Window window;
    private final FrustumCulling frustumCulling;
    private final SceneRender sceneRender;
    private final ImmediateUI immediateUI;
    private final SceneData sceneData;

    private double elapsedTime;
    private boolean refresh;
    private boolean requestDestroyMap;

    public Scene(Screen screen, SceneWorld sceneWorld) {
        this.sceneData = new SceneData(sceneWorld, null);
        this.window = screen.getWindow();
        this.frustumCulling = new FrustumCulling();
        this.immediateUI = new ImmediateUI();
        this.sceneRender = new SceneRender(this.getSceneData());
    }

    public void preRender() {
        JGems.get().getPhysicThreadManager().getPhysicsTimer().jbDebugDraw.setupBuffers();
        this.getSceneWorld().setFrustumCulling(this.getFrustumCulling());
        JGems.get().getLogManager().log("Starting scene rendering!");
        this.getSceneRender().onStartRender();
        JGems.get().getLogManager().log("Scene rendering started!");
    }

    @SuppressWarnings("all")
    public void renderScene(double deltaTime) throws InterruptedException {
        if (Scene.isSceneActive()) {
            JGems.get().getScreen().normalizeViewPort();
            if (this.getCurrentCamera() != null) {
                if (this.requestDestroyMap) {
                    JGems.get().destroyMap();
                    this.requestDestroyMap = false;
                    return;
                }
                this.elapsedTime += deltaTime / PhysicThreadManager.getFrameTime();
                if (this.elapsedTime > 1.0d) {
                    SyncManager.SyncPhysics.free();
                    this.refresh = true;
                    this.elapsedTime %= 1.0d;
                }
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
        this.getSceneWorld().onWorldUpdate();
        this.getCurrentCamera().updateCamera(partialTicks);
        TransformationManager.instance.updateViewMatrix(this.getCurrentCamera());
        this.getSceneRender().onRender(partialTicks);
        this.refresh = false;
    }

    public void postRender() {
        JGems.get().getPhysicThreadManager().getPhysicsTimer().jbDebugDraw.cleanup();
        JGems.get().getLogManager().log("Stopping scene rendering!");
        this.getSceneRender().onStopRender();
        JGems.get().getLogManager().log("Destroying resources!");
        this.UI().destroyUI();
        JGems.get().getLogManager().log("Scene rendering stopped");
    }

    public static void activeGlTexture(int code) {
        GL30.glActiveTexture(GL13.GL_TEXTURE0 + code);
    }

    public void setRenderCamera(ICamera camera) {
        this.getSceneData().setCamera(camera);
    }

    public void requestDestroyMap() {
        this.requestDestroyMap = true;
    }

    public void onWindowResize(Vector2i dim) {
        this.getSceneRender().onWindowResize(dim);
        this.UI().onWindowResize(dim);
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
        return JGems.get().getScreen().getWindow().isActive();
    }

    public static void setCamera(ICamera camera) {
        JGems.get().getScreen().getScene().setRenderCamera(camera);
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
            JGems.get().getLogManager().warn("GL ERROR: " + error);
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

    public ImmediateUI UI() {
        return this.immediateUI;
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