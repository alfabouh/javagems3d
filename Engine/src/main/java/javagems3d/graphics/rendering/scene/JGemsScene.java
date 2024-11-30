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

package javagems3d.graphics.rendering.scene;

import javagems3d.graphics.rendering.scene.buffers.IndirectRenderBuffer;
import javagems3d.graphics.OLD.JGemsOpenGLRendererOLD;
import javagems3d.graphics.rendering.scene.renderer.JGemsOpenGLRenderer;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.screen.window.IWindow;
import javagems3d.system.resources.assets.models.mesh.vertex.pointers.DefaultAttributePointers;
import javagems3d.system.resources.manager.mesh.MeshBuffersDrawCache;
import javagems3d.JGems3D;
import javagems3d.JGemsHelper;
import javagems3d.graphics.camera.base.ICamera;
import javagems3d.graphics.rendering.ui.jgems_imgui.ImmediateUI;
import javagems3d.graphics.screen.ticking.FrameTicking;
import javagems3d.graphics.screen.window.Window;
import javagems3d.graphics.world.SceneWorld;
import javagems3d.graphics.transformation.TransformationUtils;
import javagems3d.physics.world.thread.JGemsPhysics;
import javagems3d.system.service.synchronizing.SyncManager;

public class JGemsScene implements IScene {
    private final JGemsSceneData sceneData;
    private OpenGLRenderer sceneRenderer;
    private float elapsedTime;
    private boolean refresh;

    public JGemsScene(Window window, TransformationUtils transformationUtils, SceneWorld sceneWorld) {
        ImmediateUI immediateUI = new ImmediateUI();
        IndirectRenderBuffer renderBuffer = new IndirectRenderBuffer(DefaultAttributePointers.ATTR_POSITIONS, DefaultAttributePointers.ATTR_NORMALS, DefaultAttributePointers.ATTR_TEXTURE_COORDINATES, DefaultAttributePointers.ATTR_TANGENTS, DefaultAttributePointers.ATTR_BI_TANGENTS);
        this.sceneData = new JGemsSceneData(transformationUtils, sceneWorld, immediateUI, renderBuffer);
        this.setSceneRenderer(new JGemsOpenGLRenderer(window, this.getData()));
    }

    public void initSceneIndirectRenderBuffer(MeshBuffersDrawCache meshBuffersDrawCache) {
        this.getSceneIndirectRenderBuffer().clear();
        this.getSceneIndirectRenderBuffer().init(meshBuffersDrawCache);
    }

    public void preRender() {
        JGemsHelper.getLogger().log("Starting scene rendering!");
        this.getSceneRenderer().onStartRender();
        JGemsHelper.getLogger().log("Scene rendering started!");
    }

    @SuppressWarnings("all")
    public void renderScene(float frameDeltaTime) throws InterruptedException {
        if (JGemsHelper.WINDOW.isWindowActive()) {
            JGems3D.get().getScreen().normalizeViewPort();
            JGemsOpenGLRendererOLD.getGameUboShader().beginShading();
            if (this.getCamera() != null) {
                this.elapsedTime += frameDeltaTime / JGemsPhysics.getFrameTime();
                if (this.elapsedTime > 1.0d) {
                    SyncManager.SyncPhysics.free();
                    this.refresh = true;
                    this.elapsedTime %= 1.0d;
                }
                this.updateSceneComponents(new FrameTicking(this.elapsedTime, frameDeltaTime));
            } else {
                this.elapsedTime = 0.0f;
            }
            this.getSceneRenderer().onRender(new FrameTicking(this.elapsedTime, frameDeltaTime));
            JGemsOpenGLRendererOLD.getGameUboShader().endShading();
        }
    }

    @SuppressWarnings("all")
    public void updateSceneComponents(final FrameTicking frameTicking) throws InterruptedException {
        this.getSceneWorld().updateWorldObjects(this.refresh, frameTicking);
        this.refresh = false;
        this.getSceneWorld().onWorldUpdate();
        this.getCamera().updateCamera(frameTicking.getFrameDeltaTime());
        this.getTransformationUtils().updateCamera(this.getCamera());
    }

    public void postRender() {
        JGemsHelper.getLogger().log("Stopping scene rendering!");
        this.getSceneRenderer().onStopRender();
        JGemsHelper.getLogger().log("Destroying resources!");
        this.getImmediateUI().destroyUI();
        this.getSceneIndirectRenderBuffer().clear();
        JGemsHelper.getLogger().log("Scene rendering stopped");
    }

    @Override
    public void onWindowResize(IWindow window) {
        this.getSceneRenderer().onWindowResize(window);
        this.getImmediateUI().onWindowResize(window);
    }

    public void setCamera(ICamera camera) {
        this.getSceneWorld().setCamera(camera);
    }

    public void setSceneRenderer(OpenGLRenderer sceneRenderer) {
        this.sceneRenderer = sceneRenderer;
    }

    public ICamera getCamera() {
        return this.getSceneWorld().getCamera();
    }

    public JGemsSceneData getData() {
        return this.sceneData;
    }

    public Window getWindow() {
        return JGemsHelper.getScreen().getWindow();
    }

    public IndirectRenderBuffer getSceneIndirectRenderBuffer() {
        return this.getData().getSceneIndirectRenderBuffer();
    }

    public ImmediateUI getImmediateUI() {
        return this.getData().getImmediateUI();
    }

    @Override
    public SceneWorld getSceneWorld() {
        return this.getData().getSceneWorld();
    }

    @Override
    public TransformationUtils getTransformationUtils() {
        return this.getData().getTransformationUtils();
    }

    @Override
    public OpenGLRenderer getSceneRenderer() {
        return this.sceneRenderer;
    }
}