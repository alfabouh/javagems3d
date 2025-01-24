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

import javagems3d.global.JGemsRenderingGlobalConstants;
import javagems3d.graphics.rendering.scene.renderer.JGemsOpenGLRenderer;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.screen.window.IWindow;
import javagems3d.JGems3D;
import javagems3d.JGemsHelper;
import javagems3d.graphics.camera.base.ICamera;
import javagems3d.graphics.screen.ticking.FrameTicking;
import javagems3d.graphics.world.SceneWorld;
import javagems3d.graphics.transformation.JGemsTransformation;
import javagems3d.physics.world.thread.JGemsPhysics;
import javagems3d.system.service.synchronizing.SyncManager;

public class JGemsScene implements IScene {
    private final IWindow window;
    private final SceneWorld sceneWorld;
    protected OpenGLRenderer sceneRenderer;

    private float elapsedTime;
    private boolean refresh;

    public JGemsScene(IWindow window, SceneWorld sceneWorld) {
        this.sceneWorld = sceneWorld;
        this.window = window;
        this.setDefaultRenderer();
    }

    protected void setDefaultRenderer() {
        this.setSceneRenderer(new JGemsOpenGLRenderer(this.getWindow(), this.getSceneWorld()));
    }

    public void preRender() {
        JGemsHelper.getLogger().log("Starting scene rendering!");
        this.getSceneRenderer().onStartRender();
    }

    @SuppressWarnings("all")
    public void renderScene(float frameDeltaTime) throws InterruptedException {
        if (JGemsHelper.WINDOW.isWindowActive()) {
            JGemsOpenGLRenderer.UBOShader().beginShading();
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
            JGemsOpenGLRenderer.UBOShader().endShading();
        }
    }

    @SuppressWarnings("all")
    public void updateSceneComponents(final FrameTicking frameTicking) throws InterruptedException {
        this.getSceneWorld().updateWorldObjects(this.refresh, frameTicking);
        this.refresh = false;
        this.getSceneWorld().onWorldUpdate();
        this.getCamera().updateCamera(frameTicking.getFrameDeltaTime());
        JGemsTransformation.INSTANCE.updateCamera(this.getCamera());
    }

    public void postRender() {
        JGemsHelper.getLogger().log("Stopping scene rendering!");
        this.getSceneRenderer().onStopRender();
    }

    @Override
    public void onWindowResize(IWindow window) {
        this.getSceneRenderer().onWindowResize(window);
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

    public IWindow getWindow() {
        return this.window;
    }

    @Override
    public SceneWorld getSceneWorld() {
        return this.sceneWorld;
    }

    @Override
    public OpenGLRenderer getSceneRenderer() {
        return this.sceneRenderer;
    }
}