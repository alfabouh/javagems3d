package ru.alfabouh.jgems3d.engine.graphics.opengl.rendering;

import org.joml.Vector2i;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;
import ru.alfabouh.jgems3d.engine.JGems;
import ru.alfabouh.jgems3d.engine.graphics.opengl.rendering.items.objects.AbstractSceneEntity;
import ru.alfabouh.jgems3d.engine.physics.world.basic.WorldItem;
import ru.alfabouh.jgems3d.engine.physics.world.thread.PhysicsThread;
import ru.alfabouh.jgems3d.engine.graphics.opengl.frustum.FrustumCulling;
import ru.alfabouh.jgems3d.engine.graphics.opengl.rendering.imgui.ImmediateUI;
import ru.alfabouh.jgems3d.engine.graphics.opengl.rendering.utils.JGemsSceneUtils;
import ru.alfabouh.jgems3d.engine.graphics.opengl.world.SceneWorld;
import ru.alfabouh.jgems3d.engine.graphics.opengl.camera.AttachedCamera;
import ru.alfabouh.jgems3d.engine.graphics.opengl.camera.FreeCamera;
import ru.alfabouh.jgems3d.engine.graphics.opengl.camera.ICamera;
import ru.alfabouh.jgems3d.engine.graphics.opengl.screen.JGemsScreen;
import ru.alfabouh.jgems3d.engine.graphics.opengl.screen.window.Window;
import ru.alfabouh.jgems3d.engine.graphics.transformation.TransformationUtils;
import ru.alfabouh.jgems3d.engine.system.controller.objects.IController;
import ru.alfabouh.jgems3d.engine.system.synchronizing.SyncManager;
import ru.alfabouh.jgems3d.logger.SystemLogging;

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
        SystemLogging.get().getLogManager().log("Starting scene rendering!");
        this.getSceneRender().onStartRender();
        SystemLogging.get().getLogManager().log("Scene rendering started!");
    }

    @SuppressWarnings("all")
    public void renderScene(float deltaTime) throws InterruptedException {
        if (JGemsSceneUtils.isSceneActive()) {
            JGems.get().getScreen().normalizeViewPort();
            if (this.getCurrentCamera() != null) {
                if (this.requestDestroyMap) {
                    JGems.get().destroyMap();
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
                this.getSceneRender().onRender(deltaTime);
            }
        }
    }

    @SuppressWarnings("all")
    public void renderSceneInterpolated(final float partialTicks, final float deltaTicks) throws InterruptedException {
        this.getFrustumCulling().refreshFrustumCullingState(this.getTransformationUtils().getPerspectiveMatrix(), this.getTransformationUtils().getMainCameraViewMatrix());
        this.getSceneWorld().updateWorldObjects(this.refresh, partialTicks);
        this.getSceneWorld().onWorldUpdate();
        this.getCurrentCamera().updateCamera(deltaTicks);
        this.getTransformationUtils().updateCamera(this.getCurrentCamera());
        this.getSceneRender().onRender(partialTicks);
        this.refresh = false;
    }

    public void postRender() {
        SystemLogging.get().getLogManager().log("Stopping scene rendering!");
        this.getSceneRender().onStopRender();
        SystemLogging.get().getLogManager().log("Destroying resources!");
        this.UI().destroyUI();
        SystemLogging.get().getLogManager().log("Scene rendering stopped");
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

    public JGemsOpenGLRenderer getSceneRender() {
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

        public SceneWorld getSceneWorld() {
            return this.sceneWorld;
        }

        public void setSceneWorld(SceneWorld sceneWorld) {
            this.sceneWorld = sceneWorld;
        }

        public ICamera getCamera() {
            return this.camera;
        }

        public void setCamera(ICamera camera) {
            this.camera = camera;
        }
    }
}