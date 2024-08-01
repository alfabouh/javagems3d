package ru.jgems3d.engine.graphics.opengl.rendering;

import org.joml.Vector2i;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;
import ru.jgems3d.engine.JGems3D;
import ru.jgems3d.engine.graphics.opengl.rendering.items.objects.AbstractSceneEntity;
import ru.jgems3d.engine.graphics.opengl.rendering.scene.SceneData;
import ru.jgems3d.engine.physics.world.basic.WorldItem;
import ru.jgems3d.engine.physics.world.thread.PhysicsThread;
import ru.jgems3d.engine.graphics.opengl.frustum.FrustumCulling;
import ru.jgems3d.engine.graphics.opengl.rendering.imgui.ImmediateUI;
import ru.jgems3d.engine.graphics.opengl.rendering.utils.JGemsSceneUtils;
import ru.jgems3d.engine.graphics.opengl.world.SceneWorld;
import ru.jgems3d.engine.graphics.opengl.camera.AttachedCamera;
import ru.jgems3d.engine.graphics.opengl.camera.FreeCamera;
import ru.jgems3d.engine.graphics.opengl.camera.ICamera;
import ru.jgems3d.engine.graphics.opengl.screen.JGemsScreen;
import ru.jgems3d.engine.graphics.opengl.screen.window.Window;
import ru.jgems3d.engine.graphics.transformation.TransformationUtils;
import ru.jgems3d.engine.JGemsHelper;
import ru.jgems3d.engine.system.controller.objects.IController;
import ru.jgems3d.engine.system.synchronizing.SyncManager;

public class JGemsScene implements IScene {
    private final TransformationUtils transformationUtils;
    private final Window window;
    private final FrustumCulling frustumCulling;
    private final JGemsOpenGLRenderer sceneRender;
    private final ImmediateUI immediateUI;
    private final SceneData sceneData;

    private float elapsedTime;
    private boolean refresh;
    private boolean requestDestroyMap;

    public JGemsScene(TransformationUtils transformationUtils, JGemsScreen screen, SceneWorld sceneWorld) {
        this.window = screen.getWindow();
        this.transformationUtils = transformationUtils;

        this.sceneData = new SceneData(sceneWorld, null);
        this.frustumCulling = new FrustumCulling();
        this.immediateUI = new ImmediateUI();
        this.sceneRender = new JGemsOpenGLRenderer(this.getSceneData());
    }

    public static void activeGlTexture(int code) {
        GL30.glActiveTexture(GL13.GL_TEXTURE0 + code);
    }

    public void preRender() {
        this.getSceneWorld().setFrustumCulling(this.getFrustumCulling());
        JGemsHelper.getLogger().log("Starting scene rendering!");
        this.getSceneRenderer().onStartRender();
        JGemsHelper.getLogger().log("Scene rendering started!");
    }

    @SuppressWarnings("all")
    public void renderScene(float deltaTime) throws InterruptedException {
        if (JGemsSceneUtils.isSceneActive()) {
            JGems3D.get().getScreen().normalizeViewPort();
            JGemsOpenGLRenderer.getGameUboShader().bind();
            if (this.getCurrentCamera() != null) {
                if (this.requestDestroyMap) {
                    JGems3D.get().destroyMap();
                    this.requestDestroyMap = false;
                    return;
                }
                this.elapsedTime += deltaTime / PhysicsThread.getFrameTime();
                if (this.elapsedTime > 1.0d) {
                    SyncManager.SyncPhysics.free();
                    this.refresh = true;
                    this.elapsedTime %= 1.0d;
                }
                this.renderSceneInterpolated(this.elapsedTime, deltaTime);
            } else {
                this.getSceneRenderer().onRender(deltaTime);
            }
            JGemsOpenGLRenderer.getGameUboShader().unBind();
        }
    }

    @SuppressWarnings("all")
    public void renderSceneInterpolated(final float partialTicks, final float deltaTime) throws InterruptedException {
        this.getFrustumCulling().refreshFrustumCullingState(this.getTransformationUtils().getPerspectiveMatrix(), this.getTransformationUtils().getMainCameraViewMatrix());
        this.getSceneWorld().updateWorldObjects(this.refresh, partialTicks, deltaTime);
        this.getSceneWorld().onWorldUpdate();
        this.getCurrentCamera().updateCamera(deltaTime);
        this.getTransformationUtils().updateCamera(this.getCurrentCamera());
        this.getSceneRenderer().onRender(partialTicks);
        this.refresh = false;
    }

    public void postRender() {
        JGemsHelper.getLogger().log("Stopping scene rendering!");
        this.getSceneRenderer().onStopRender();
        JGemsHelper.getLogger().log("Destroying resources!");
        this.UI().destroyUI();
        JGemsHelper.getLogger().log("Scene rendering stopped");
    }

    public void setRenderCamera(ICamera camera) {
        this.getSceneData().setCamera(camera);
    }

    public void requestDestroyMap() {
        this.requestDestroyMap = true;
    }

    public void onWindowResize(Vector2i dim) {
        this.getSceneRenderer().onWindowResize(dim);
        this.UI().onWindowResize(dim);
    }

    public void enableFreeCamera(IController controller, Vector3f pos, Vector3f rot) {
        this.setRenderCamera(new FreeCamera(controller, pos, rot));
    }

    public void enableAttachedCamera(WorldItem worldItem) {
        this.setRenderCamera(this.getSceneData().getSceneWorld().createAttachedCamera(worldItem));
    }

    public void enableAttachedCamera(AbstractSceneEntity abstractSceneEntity) {
        this.setRenderCamera(new AttachedCamera(abstractSceneEntity));
    }

    public TransformationUtils getTransformationUtils() {
        return this.transformationUtils;
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

    public boolean isCameraAttachedToItem(AbstractSceneEntity abstractSceneEntity) {
        return this.getCurrentCamera() instanceof AttachedCamera && ((AttachedCamera) this.getCurrentCamera()).getPhysXObject() == abstractSceneEntity;
    }

    public JGemsOpenGLRenderer getSceneRenderer() {
        return this.sceneRender;
    }

    public FrustumCulling getFrustumCulling() {
        return this.frustumCulling;
    }
}