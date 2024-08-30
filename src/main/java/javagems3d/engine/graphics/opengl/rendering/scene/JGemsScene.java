/*
 * *
 *  * @author alfabouh
 *  * @since 2024
 *  * @link https://github.com/alfabouh/JavaGems3D
 *  *
 *  * This software is provided 'as-is', without any express or implied warranty.
 *  * In no event will the authors be held liable for any damages arising from the use of this software.
 *
 */

package javagems3d.engine.graphics.opengl.rendering.scene;

import org.joml.Vector2i;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;
import javagems3d.engine.JGems3D;
import javagems3d.engine.JGemsHelper;
import javagems3d.engine.graphics.opengl.camera.AttachedCamera;
import javagems3d.engine.graphics.opengl.camera.FreeCamera;
import javagems3d.engine.graphics.opengl.camera.ICamera;
import javagems3d.engine.graphics.opengl.frustum.FrustumCulling;
import javagems3d.engine.graphics.opengl.rendering.imgui.ImmediateUI;
import javagems3d.engine.graphics.opengl.rendering.items.objects.AbstractSceneEntity;
import javagems3d.engine.graphics.opengl.rendering.scene.base.IScene;
import javagems3d.engine.graphics.opengl.rendering.scene.render_base.SceneData;
import javagems3d.engine.graphics.opengl.rendering.scene.tick.FrameTicking;
import javagems3d.engine.graphics.opengl.screen.window.Window;
import javagems3d.engine.graphics.opengl.world.SceneWorld;
import javagems3d.engine.graphics.transformation.TransformationUtils;
import javagems3d.engine.physics.world.basic.WorldItem;
import javagems3d.engine.physics.world.thread.PhysicsThread;
import javagems3d.engine.system.controller.objects.IController;
import javagems3d.engine.system.service.synchronizing.SyncManager;

public class JGemsScene implements IScene {
    private final TransformationUtils transformationUtils;
    private final FrustumCulling frustumCulling;
    private final ImmediateUI immediateUI;
    private final SceneData sceneData;
    private JGemsOpenGLRenderer sceneRenderer;
    private float elapsedTime;
    private boolean refresh;


    public JGemsScene(Window window, TransformationUtils transformationUtils, SceneWorld sceneWorld) {
        this.transformationUtils = transformationUtils;

        this.sceneData = new SceneData(sceneWorld, null);
        this.frustumCulling = new FrustumCulling();
        this.immediateUI = new ImmediateUI();

        this.setSceneRenderer(new JGemsOpenGLRenderer(window, this.getSceneData()));
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
    public void renderScene(float frameDeltaTime) throws InterruptedException {
        if (JGemsHelper.WINDOW.isWindowActive()) {
            JGems3D.get().getScreen().normalizeViewPort();
            JGemsOpenGLRenderer.getGameUboShader().bind();
            if (this.getCurrentCamera() != null) {
                this.elapsedTime += frameDeltaTime / PhysicsThread.getFrameTime();
                if (this.elapsedTime > 1.0d) {
                    SyncManager.SyncPhysics.free();
                    this.refresh = true;
                    this.elapsedTime %= 1.0d;
                }
                this.updateSceneComponents(new FrameTicking(this.elapsedTime, frameDeltaTime));
            } else {
                this.elapsedTime = 0.0f;
            }
            this.getSceneRenderer().onRender(new FrameTicking(this.elapsedTime, frameDeltaTime), this.getWindowDimensions());
            JGemsOpenGLRenderer.getGameUboShader().unBind();
        }
    }

    @SuppressWarnings("all")
    public void updateSceneComponents(final FrameTicking frameTicking) throws InterruptedException {
        this.getFrustumCulling().refreshFrustumCullingState(this.getTransformationUtils().getPerspectiveMatrix(), this.getTransformationUtils().getMainCameraViewMatrix());
        this.getSceneWorld().updateWorldObjects(this.refresh, frameTicking);
        this.refresh = false;
        this.getSceneWorld().onWorldUpdate();
        this.getCurrentCamera().updateCamera(frameTicking.getFrameDeltaTime());
        this.getTransformationUtils().updateCamera(this.getCurrentCamera());
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
        return JGemsHelper.getScreen().getWindow();
    }

    public boolean isCameraAttachedToItem(AbstractSceneEntity abstractSceneEntity) {
        return this.getCurrentCamera() instanceof AttachedCamera && ((AttachedCamera) this.getCurrentCamera()).getPhysXObject() == abstractSceneEntity;
    }

    public JGemsOpenGLRenderer getSceneRenderer() {
        return this.sceneRenderer;
    }

    public void setSceneRenderer(JGemsOpenGLRenderer sceneRenderer) {
        this.sceneRenderer = sceneRenderer;
    }

    public FrustumCulling getFrustumCulling() {
        return this.frustumCulling;
    }
}