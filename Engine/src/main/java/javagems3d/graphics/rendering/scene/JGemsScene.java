/*
 *
 *  * javagems3d
 *  * Copyright (C) 2026 gltexture
 *  *
 *  * This program is free software: you can redistribute it and/or modify
 *  * it under the terms of the GNU General Public License as published by
 *  * the Free Software Foundation, either version 3 of the License, or
 *  * (at your option) any later version.
 *  *
 *  * This program is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  * GNU General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU General Public License
 *  * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 */

package javagems3d.graphics.rendering.scene;

import javagems3d.graphics.rendering.scene.renderer.JGemsOpenGLRenderer;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.screen.window.IWindow;
import javagems3d.graphics.camera.base.ICamera;
import javagems3d.graphics.screen.ticking.FrameTicking;
import javagems3d.graphics.world.IRenderWorld;
import javagems3d.graphics.world.SceneWorld;
import javagems3d.graphics.transformation.JGemsTransformManager;
import javagems3d.physics.world.thread.JGemsPhysics;
import javagems3d.system.service.synchronizing.SyncManager;
import logger.Log;

public class JGemsScene implements IScene {
    private final IWindow window;
    private final IRenderWorld sceneWorld;
    protected OpenGLRenderer sceneRenderer;

    private float elapsedTime;
    private boolean refresh;

    public JGemsScene(IWindow window, IRenderWorld sceneWorld) {
        this.sceneWorld = sceneWorld;
        this.window = window;
        this.setDefaultRenderer();
    }

    protected void setDefaultRenderer() {
        this.setSceneRenderer(new JGemsOpenGLRenderer(this.getWindow(), this.getWorld()));
    }

    public void preRender() {
        Log.get().info("Starting scenes rendering");
        this.getSceneRenderer().onStartRender();
    }

    @SuppressWarnings("all")
    public void renderScene(float frameDeltaTime) throws InterruptedException {
        if (this.getWindow().isWindowActive()) {
            if (JGemsOpenGLRenderer.UBOShader() != null) {
                JGemsOpenGLRenderer.UBOShader().beginShading();
            }
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
            if (JGemsOpenGLRenderer.UBOShader() != null) {
                JGemsOpenGLRenderer.UBOShader().endShading();
            }
        }
    }

    @SuppressWarnings("all")
    public void updateSceneComponents(final FrameTicking frameTicking) throws InterruptedException {
        SceneWorld sceneWorld1 = (SceneWorld) this.getWorld();
        sceneWorld1.updateWorldObjects(this.refresh, frameTicking);
        this.refresh = false;
        sceneWorld1.onWorldUpdate();
        this.getCamera().updateCamera(frameTicking.frameDeltaTime());
        JGemsTransformManager.INSTANCE.updateCamera(this.getCamera());
    }

    public void postRender() {
        Log.get().info("Stopping scenes rendering");
        this.getSceneRenderer().onStopRender();
    }

    @Override
    public void onWindowResize(IWindow window) {
        this.getSceneRenderer().onWindowResize(window);
    }

    public void setCamera(ICamera camera) {
        this.getWorld().setCamera(camera);
    }

    public void setSceneRenderer(OpenGLRenderer sceneRenderer) {
        this.sceneRenderer = sceneRenderer;
    }

    public ICamera getCamera() {
        return this.getWorld().getCamera();
    }

    public IWindow getWindow() {
        return this.window;
    }

    @Override
    public IRenderWorld getWorld() {
        return this.sceneWorld;
    }

    @Override
    public OpenGLRenderer getSceneRenderer() {
        return this.sceneRenderer;
    }
}