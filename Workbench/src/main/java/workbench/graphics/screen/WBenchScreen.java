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

package workbench.graphics.screen;

import javagems3d.JGems3D;
import javagems3d.system.global.JGemsConfig;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.screen.IScreen;
import javagems3d.graphics.screen.OpenGLSysUtils;
import javagems3d.graphics.screen.timer.JGemsTimedAction;
import javagems3d.graphics.screen.timer.TimerPool;
import javagems3d.graphics.screen.window.IWindow;
import javagems3d.graphics.screen.window.Window;
import javagems3d.graphics.transformation.JGemsTransformManager;
import javagems3d.system.resources.managing.ResourceManager;
import javagems3d.system.service.profiler.SpeedProfiler;
import javagems3d.system.service.exceptions.JGemsRuntimeException;
import logger.Log;
import org.joml.Vector2i;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL46;
import org.lwjgl.system.MemoryUtil;
import workbench.WBench;
import workbench.controller.WBenchControllerDispatcher;
import workbench.global.WBenchConstants;
import workbench.graphics.scene.WBenchScene;
import workbench.graphics.scene.world.WBenchWorld;
import workbench.project.game.settings.GameProjectSettings;
import workbench.resources.WBenchResourceManager;

public class WBenchScreen implements IScreen {
    public static int RENDER_FPS;
    private final TimerPool timerPool;
    private WBenchControllerDispatcher controllerDispatcher;
    private WBenchScene scene;
    private Window window;
    private float renderTicks;
    public boolean triggerAutoSave;

    public WBenchScreen() {
        this.timerPool = new TimerPool();
        this.renderTicks = 0.0f;
    }

    public void createObjects(IWindow window) {
        this.controllerDispatcher = new WBenchControllerDispatcher(window);
        this.scene = new WBenchScene(window, new WBenchWorld());
        WBench.get().getMapProjectManager().setWorld(this.getScene().getWorld());
    }

    public void createScreenAndContext() {
        Log.get().info("Init Graphics");
        if (this.tryToBuildScreen()) {
            JGemsTransformManager.INSTANCE.setProjectionData(this.getWindow(), WBenchConstants.FOV, WBenchConstants.Z_NEAR, WBenchConstants.Z_FAR);
            JGemsTransformManager.INSTANCE.updateSetOfMatrices(this.getWindow());
            GL.createCapabilities();
            GL46.glEnable(GL46.GL_LINE_SMOOTH);
            GL46.glHint(GL46.GL_LINE_SMOOTH_HINT, GL46.GL_NICEST);
            String validate = OpenGLSysUtils.validateOGLFunctions();
            if (validate != null) {
                throw new JGemsRuntimeException(validate);
            }
            if (JGems3D.DEBUG_MODE) {
                OpenGLSysUtils.registerOGLDebugOutput();
            }
            ResourceManager.initDefaultErrorTexture();
            WBenchResourceManager.createGlobalShaders();
            this.setScreenCallbacks();
            OpenGLRenderer.setViewPort(this.getWindow().getWindowSize());
        } else {
            throw new JGemsRuntimeException("Caught service, while building screen");
        }
    }

    public static void clearColor() {
        GL46.glClearColor(0.0f, 0.0f, 0.2f, 0.0f);
    }

    private void showScreen() {
        WBenchScreen.clearColor();
        GL46.glClear(GL46.GL_COLOR_BUFFER_BIT);
        this.getWindow().showWindow();
        GLFW.glfwSwapBuffers(this.getWindow().getDescriptor());
        GLFW.glfwPollEvents();
    }

    private void setScreenCallbacks() {
        Callbacks.glfwFreeCallbacks(this.getWindow().getDescriptor());
        GLFW.glfwSetWindowSizeCallback(this.getWindow().getDescriptor(), (a, b, c) -> {
            this.resizeWindow(this.getWindow());
            this.getWindow().onWindowChangedCallback();
        });
        GLFW.glfwSetWindowPosCallback(this.getWindow().getDescriptor(), (a, b, c) -> {
            this.getWindow().onWindowChangedCallback();
        });
        GLFWErrorCallback glfwErrorCallback = GLFW.glfwSetErrorCallback(null);
        if (glfwErrorCallback != null) {
            glfwErrorCallback.free();
        }
    }

    private void resizeWindow(IWindow window) {
        if (this.getScene() != null) {
            this.getScene().onWindowResize(window);
        }
        JGemsTransformManager.INSTANCE.updateSetOfMatrices(this.getWindow());
    }

    public boolean tryToBuildScreen() {
        GLFWErrorCallback.createPrint(System.err).set();
        if (!GLFW.glfwInit()) {
            throw new JGemsRuntimeException("Error, while initializing GLFW");
        }
        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 6);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 4);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GL46.GL_TRUE);
        GLFW.glfwWindowHint(GLFW.GLFW_DOUBLEBUFFER, GLFW.GLFW_TRUE);
        GLFWVidMode vidMode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());

        int width = JGemsConfig.SYSTEM.DEFAULT_SCREEN_WIDTH;
        int height = JGemsConfig.SYSTEM.DEFAULT_SCREEN_HEIGHT;

        this.window = new Window(width, height, new Window.WindowProperties(WBench.get().toString()));
        long window = this.getWindow().getDescriptor();
        if (window == MemoryUtil.NULL) {
            throw new JGemsRuntimeException("Failed to create the GLFW window");
        }
        if (vidMode != null) {
            int x = (vidMode.width() - JGemsConfig.SYSTEM.DEFAULT_SCREEN_WIDTH) / 2;
            int y = (vidMode.height() - JGemsConfig.SYSTEM.DEFAULT_SCREEN_HEIGHT) / 2;
            GLFW.glfwSetWindowPos(window, x, y);
        } else {
            return false;
        }
        GLFW.glfwMakeContextCurrent(window);
        return true;
    }

    public void switchScreenMode() {
        if (this.getWindow().isFullScreen()) {
            this.getWindow().removeFullScreen();
        } else {
            this.getWindow().makeFullScreen();
        }
    }

    public void refreshSceneResources() {
        this.getScene().getSceneRenderer().recreateResources();
    }

    private void updateController() {
        if (this.getControllerDispatcher() != null) {
            this.getControllerDispatcher().updateController(this.getWindow());
        }
    }

    public void runRenderThread() {
        Log.get().info("Starting screen");
        WBench.get().getResourceManager().loadGlobalResources();
        this.getScene().preRender();
        this.showScreen();
        try {
            this.renderLoop();
        } catch (Exception e) {
            WBench.get().close();
            throw new JGemsRuntimeException(e);
        } finally {
            this.getScene().postRender();
            this.getTimerPool().clear();
            GLFW.glfwDestroyWindow(this.getWindow().getDescriptor());
            GLFW.glfwTerminate();
            Log.get().info("Screen destroyed");
        }
    }

    private void renderLoop() throws InterruptedException {
        int fps = 0;
        JGemsTimedAction perSecondTimer = this.getTimerPool().createTimer();
        JGemsTimedAction renderTimer = this.getTimerPool().createTimer();
        JGemsTimedAction deltaTimer = this.getTimerPool().createTimer();
        JGemsTimedAction autoSaveTimer = this.getTimerPool().createTimer();

        while (!WBench.get().isShouldBeClosed()) {
            if (GLFW.glfwWindowShouldClose(this.getWindow().getDescriptor())) {
                WBench.get().close();
                break;
            }
            this.updateController();
            this.getWindow().setCursorFocused(false);
            this.getTimerPool().update();
            this.renderGameScene(deltaTimer.getDeltaTime());
            WBench.get().getSoundManager().update();
            if (renderTimer.resetTimerAfterReachedSeconds(1.0d / JGemsConfig.SYSTEM.RENDER_TICKS_UPD_RATE)) {
                this.renderTicks += 0.01f;
            }
            fps += 1;
            if (perSecondTimer.resetTimerAfterReachedSeconds(1.0d)) {
                WBenchScreen.RENDER_FPS = fps;
                fps = 0;
            }

            if (WBench.get().getGameProjectManager().getGameProject() != null) {
                if (WBench.get().getGameProjectManager().gameProjectSettings.mapProjectAutoSaveMode == GameProjectSettings.MapProjectAutoSaveMode.TIMER) {
                    if (autoSaveTimer.resetTimerAfterReachedSeconds(WBench.get().getGameProjectManager().gameProjectSettings.savePerSecond)) {
                        this.triggerAutoSave = true;
                    }
                }
                if (this.triggerAutoSave) {
                    if (WBench.get().getMapProjectManager().getCurrentMapProject() != null) {
                        Log.get().trace("Autosave...");
                        WBench.get().getMapProjectManager().saveMapProject(false);
                    }
                    this.triggerAutoSave = false;
                }
            }

            SpeedProfiler.clear();
            GLFW.glfwSwapBuffers(this.getWindow().getDescriptor());
            GLFW.glfwPollEvents();
        }
    }

    private void renderGameScene(float delta) throws InterruptedException {
        GL46.glEnable(GL46.GL_CULL_FACE);
        GL46.glEnable(GL46.GL_DEPTH_TEST);
        GL46.glCullFace(GL46.GL_BACK);
        GL46.glClearDepth(1.0f);
        GL46.glDepthFunc(GL46.GL_LESS);
        this.getScene().renderScene(delta);
        OpenGLRenderer.catchGLContextExceptions();
    }

    public WBenchScene getScene() {
        return this.scene;
    }

    public void zeroRenderTick() {
        this.renderTicks = 0.0f;
    }

    public float getRenderTicks() {
        return this.renderTicks;
    }

    public Vector2i getWindowDimensions() {
        return this.getWindow().getWindowSize();
    }

    public WBenchControllerDispatcher getControllerDispatcher() {
        return this.controllerDispatcher;
    }

    public Window getWindow() {
        return this.window;
    }

    public TimerPool getTimerPool() {
        return this.timerPool;
    }
}
