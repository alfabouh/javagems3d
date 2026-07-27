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

package workbench.graphics.scene;

import javagems3d.graphics.camera.base.ICamera;
import javagems3d.graphics.rendering.scene.IScene;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.screen.ticking.FrameTicking;
import javagems3d.graphics.screen.window.IWindow;
import javagems3d.graphics.screen.window.Window;
import javagems3d.graphics.transformation.JGemsTransformManager;
import logger.Log;
import workbench.graphics.scene.renderer.WBenchOpenGLRenderer;
import workbench.graphics.scene.world.WBenchWorld;

public class WBenchScene implements IScene {
    private final IWindow window;
    private final WBenchWorld wBenchWorld;
    protected OpenGLRenderer sceneRenderer;

    public WBenchScene(IWindow window, WBenchWorld wBenchWorld) {
        this.wBenchWorld = wBenchWorld;
        this.window = window;
        this.setDefaultRenderer();
    }

    protected void setDefaultRenderer() {
        this.setSceneRenderer(new WBenchOpenGLRenderer(this.getWindow(), this.getWorld()));
    }

    public void preRender() {
        Log.get().info("Starting scenes rendering");
        this.getSceneRenderer().onStartRender();
    }

    @SuppressWarnings("all")
    public void renderScene(float frameDeltaTime) throws InterruptedException {
        if (this.getWindow().isWindowActive()) {
           // JGemsOpenGLRenderer.UBOShader().beginShading();
            this.updateSceneComponents(new FrameTicking(0.0f, frameDeltaTime));
            this.getSceneRenderer().onRender(new FrameTicking(0.0f, frameDeltaTime));
           // JGemsOpenGLRenderer.UBOShader().endShading();
        }
    }

    @SuppressWarnings("all")
    public void updateSceneComponents(final FrameTicking frameTicking) throws InterruptedException {
        this.getWorld().updateWorldObjects(frameTicking);
        this.getWorld().onWorldUpdate();

        ((Window) this.getWindow()).setFocus(true);

        if (this.getCamera() != null) {
            this.getCamera().updateCamera(frameTicking.frameDeltaTime());
            JGemsTransformManager.INSTANCE.updateCamera(this.getCamera());
        }
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
    public WBenchWorld getWorld() {
        return this.wBenchWorld;
    }

    @Override
    public OpenGLRenderer getSceneRenderer() {
        return this.sceneRenderer;
    }
}