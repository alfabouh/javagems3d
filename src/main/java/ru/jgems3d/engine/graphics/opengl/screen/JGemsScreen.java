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

package ru.jgems3d.engine.graphics.opengl.screen;

import org.joml.Vector2i;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;
import ru.jgems3d.engine.JGems3D;
import ru.jgems3d.engine.api_bridge.APIContainer;
import ru.jgems3d.engine.audio.sound.SoundListener;
import ru.jgems3d.engine.graphics.opengl.rendering.JGemsSceneGlobalConstants;
import ru.jgems3d.engine.physics.world.thread.timer.PhysicsTimer;
import ru.jgems3d.engine.graphics.opengl.rendering.scene.JGemsScene;
import ru.jgems3d.engine.graphics.opengl.rendering.imgui.elements.UIText;
import ru.jgems3d.engine.graphics.opengl.rendering.imgui.elements.base.font.FontCode;
import ru.jgems3d.engine.graphics.opengl.rendering.imgui.elements.base.font.GuiFont;
import ru.jgems3d.engine.graphics.opengl.rendering.JGemsSceneUtils;
import ru.jgems3d.engine.graphics.opengl.world.SceneWorld;
import ru.jgems3d.engine.graphics.opengl.camera.ICamera;
import ru.jgems3d.engine.graphics.opengl.screen.timer.JGemsTimer;
import ru.jgems3d.engine.graphics.opengl.screen.timer.TimerPool;
import ru.jgems3d.engine.graphics.opengl.screen.window.Window;
import ru.jgems3d.engine.graphics.transformation.TransformationUtils;
import ru.jgems3d.engine.JGemsHelper;
import ru.jgems3d.engine.system.core.EngineSystem;
import ru.jgems3d.engine.system.controller.dispatcher.JGemsControllerDispatcher;
import ru.jgems3d.engine.system.service.exceptions.JGemsRuntimeException;
import ru.jgems3d.engine.system.service.path.JGemsPath;
import ru.jgems3d.engine.system.resources.manager.JGemsResourceManager;
import ru.jgems3d.engine.system.service.collections.Pair;
import ru.jgems3d.engine.system.service.stat.PerformanceStat;

import java.awt.*;
import java.util.ArrayList;

public class JGemsScreen implements IScreen {
    public static int RENDER_FPS;
    public static int PHYS_TPS;
    private final TimerPool timerPool;
    private TransformationUtils transformationUtils;
    private JGemsControllerDispatcher controllerDispatcher;
    private JGemsScene scene;
    private Window window;
    private LoadingScreen loadingScreen;
    private float renderTicks;

    public JGemsScreen() {
        this.timerPool = new TimerPool();
        this.loadingScreen = null;
        this.renderTicks = 0.0f;
    }

    @SuppressWarnings("all")
    public boolean tryAddLineInLoadingScreen(int color, String s) {
        if (GLFW.glfwGetCurrentContext() == 0L) {
            return false;
        }
        if (this.loadingScreen == null) {
            return false;
        }
        this.loadingScreen.addText(color, s);
        return true;
    }

    public void buildScreen() {
        JGemsHelper.getLogger().log("Init Graphics!");
        if (this.tryToBuildScreen()) {
            this.createTransformationUtils();

            this.checkScreenMode();
            this.checkVSync();

            GL.createCapabilities();
            JGemsResourceManager.createShaders();

            this.showGameLoadingScreen("System01");
            this.setScreenCallbacks();
            this.createObjects(this.getWindow());

            this.normalizeViewPort();
            this.getWindow().showWindow();

            JGemsHelper.getLogger().log("JGemsScreen built successful");
        } else {
            throw new JGemsRuntimeException("Caught service, while building screen!!");
        }
    }

    private void setScreenCallbacks() {
        Callbacks.glfwFreeCallbacks(this.getWindow().getDescriptor());
        GLFW.glfwSetWindowSizeCallback(this.getWindow().getDescriptor(), (a, b, c) -> {
            this.resizeWindow(new Vector2i(b, c));
            this.getWindow().onWindowChanged();
        });
        GLFW.glfwSetWindowPosCallback(this.getWindow().getDescriptor(), (a, b, c) -> {
            this.getWindow().onWindowChanged();
        });
        GLFWErrorCallback glfwErrorCallback = GLFW.glfwSetErrorCallback(null);
        if (glfwErrorCallback != null) {
            glfwErrorCallback.free();
        }
    }

    private void createTransformationUtils() {
        this.transformationUtils = new TransformationUtils(this.window, JGemsSceneGlobalConstants.FOV, JGemsSceneGlobalConstants.Z_NEAR, JGemsSceneGlobalConstants.Z_FAR);
    }

    private void createObjects(Window window) {
        this.controllerDispatcher = new JGemsControllerDispatcher(window);
        this.scene = new JGemsScene(window, this.getTransformationUtils(), new SceneWorld());
    }

    private void resizeWindow(Vector2i dim) {
        this.getScene().onWindowResize(dim);
    }

    public void normalizeViewPort() {
        GL30.glViewport(0, 0, this.getWindowDimensions().x, this.getWindowDimensions().y);
    }

    public boolean tryToBuildScreen() {
        GLFWErrorCallback.createPrint(System.err).set();
        if (!GLFW.glfwInit()) {
            throw new JGemsRuntimeException("Error, while initializing GLFW");
        }
        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 3);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 4);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GL20.GL_TRUE);
        GLFW.glfwWindowHint(GLFW.GLFW_DOUBLEBUFFER, GLFW.GLFW_TRUE);
        if (JGems3D.DEBUG_MODE) {
            GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_DEBUG_CONTEXT, GLFW.GLFW_TRUE);
        }
        GLFWVidMode vidMode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
        boolean flag = vidMode != null && JGems3D.get().getGameSettings().windowMode.getValue() == 0;
        this.window = new Window(new Window.WindowProperties(flag ? vidMode.width() : JGemsSceneGlobalConstants.defaultW, flag ? vidMode.height() : JGemsSceneGlobalConstants.defaultH, JGems3D.get().toString()), APIContainer.get().getApiGameInfo().getAppManager().getAppConfiguration().getWindowIcon());
        long window = this.getWindow().getDescriptor();
        if (window == MemoryUtil.NULL) {
            throw new JGemsRuntimeException("Failed to create the GLFW window");
        }
        if (vidMode != null) {
            int x = (vidMode.width() - JGemsSceneGlobalConstants.defaultW) / 2;
            int y = (vidMode.height() - JGemsSceneGlobalConstants.defaultH) / 2;
            GLFW.glfwSetWindowPos(window, x, y);
        } else {
            return false;
        }
        GLFW.glfwMakeContextCurrent(window);
        return true;
    }

    public void checkVSync() {
        if (JGems3D.get().getGameSettings().vSync.getValue() == 1) {
            this.getWindow().enableVSync();
        } else {
            this.getWindow().disableVSync();
        }
    }

    public void checkScreenMode() {
        if (JGems3D.get().getGameSettings().windowMode.getValue() == 0) {
            if (!this.getWindow().isFullScreen()) {
                this.getWindow().makeFullScreen();
            }
        } else {
            if (this.getWindow().isFullScreen()) {
                this.getWindow().removeFullScreen();
            }
        }
    }

    public void switchScreenMode() {
        if (this.getWindow().isFullScreen()) {
            this.getWindow().removeFullScreen();
        } else {
            this.getWindow().makeFullScreen();
        }
    }

    public void reloadSceneAndShadowsFrameBufferObjects() {
        this.getScene().getSceneRenderer().recreateResources(this.getWindowDimensions());
    }

    public void showGameLoadingScreen(String title) {
        if (GLFW.glfwGetCurrentContext() == 0L) {
            return;
        }
        this.loadingScreen = new LoadingScreen(title);
        this.loadingScreen.updateScreen();
    }

    public void removeLoadingScreen() {
        if (GLFW.glfwGetCurrentContext() == 0L) {
            return;
        }
        this.loadingScreen.clean();
        this.loadingScreen = null;
    }

    private void updateController() {
        if (this.getControllerDispatcher() != null) {
            this.getControllerDispatcher().updateController(this.getWindow());
        }
    }

    public void startScreenRenderProcess() {
        JGemsHelper.getLogger().log("Starting screen...");
        SoundListener.updateListenerGain();
        GL11.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        this.getScene().preRender();
        this.removeLoadingScreen();
        JGems3D.get().showMainMenu();
        try {
            this.renderLoop();
        } catch (InterruptedException e) {
            throw new JGemsRuntimeException(e);
        } finally {
            this.getScene().postRender();
            JGemsHelper.getLogger().log("Destroying screen...");
            this.getTimerPool().clear();
            GLFW.glfwDestroyWindow(this.getWindow().getDescriptor());
            GLFW.glfwTerminate();
        }
    }

    private void renderLoop() throws InterruptedException {
        int fps = 0;
        JGemsTimer perSecondTimer = this.getTimerPool().createTimer();
        JGemsTimer renderTimer = this.getTimerPool().createTimer();
        JGemsTimer deltaTimer = this.getTimerPool().createTimer();
        while (!JGems3D.get().isShouldBeClosed()) {
            if (GLFW.glfwWindowShouldClose(this.getWindow().getDescriptor())) {
                JGems3D.get().destroyGame();
                break;
            }
            JGems3D.get().getEngineSystem().update();

            this.updateController();
            this.getWindow().refreshFocusState();
            this.getTimerPool().update();
            this.getTransformationUtils().updateMatrices();
            this.renderGameScene(deltaTimer.getDeltaTime());
            if (renderTimer.resetTimerAfterReachedSeconds(1.0d / JGemsSceneGlobalConstants.RENDER_TICKS_UPD_RATE)) {
                this.renderTicks += 0.01f;
            }
            fps += 1;
            if (perSecondTimer.resetTimerAfterReachedSeconds(1.0d)) {
                JGemsScreen.PHYS_TPS = PhysicsTimer.TPS;
                JGemsScreen.RENDER_FPS = fps;
                PhysicsTimer.TPS = 0;
                fps = 0;
            }
            GLFW.glfwSwapBuffers(this.getWindow().getDescriptor());
            GLFW.glfwPollEvents();
        }
    }

    private void renderGameScene(float delta) throws InterruptedException {
        GL30.glEnable(GL30.GL_CULL_FACE);
        GL30.glEnable(GL30.GL_DEPTH_TEST);
        GL30.glCullFace(GL30.GL_BACK);
        GL11.glDepthFunc(GL11.GL_LESS);
        this.getScene().renderScene(delta);
        this.updateSound();
        JGemsSceneUtils.checkGLErrors();
    }

    private void updateSound() {
        JGems3D.get().getSoundManager().update();
        if (JGems3D.get().isValidPlayer()) {
            SoundListener.updateOrientationAndPosition(JGemsSceneUtils.getMainCameraViewMatrix(), this.getCamera().getCamPosition());
        }
        SoundListener.updateListenerGain();
    }

    public SceneWorld getSceneWorld() {
        return this.getScene().getSceneWorld();
    }

    public ICamera getCamera() {
        return this.getScene().getCurrentCamera();
    }

    public void zeroRenderTick() {
        this.renderTicks = 0.0f;
    }

    public float getRenderTicks() {
        return this.renderTicks;
    }

    public Vector2i getWindowDimensions() {
        return this.getWindow().getWindowDimensions();
    }

    public JGemsControllerDispatcher getControllerDispatcher() {
        synchronized (this) {
            return this.controllerDispatcher;
        }
    }

    public JGemsScene getScene() {
        return this.scene;
    }

    public Window getWindow() {
        return this.window;
    }

    public TransformationUtils getTransformationUtils() {
        synchronized (this) {
            return this.transformationUtils;
        }
    }

    public TimerPool getTimerPool() {
        return this.timerPool;
    }

    public class LoadingScreen {
        private final GuiFont guiFont;
        private final ArrayList<Pair<Integer, String>> lines;
        private int counter;

        public LoadingScreen(String title) {
            JGemsHelper.getLogger().log("Loading screen");
            Font gameFont = JGemsResourceManager.createFontFromJAR(new JGemsPath("/assets/jgems/gamefont.ttf"));
            this.guiFont = new GuiFont(gameFont.deriveFont(Font.PLAIN, 20), FontCode.Window);
            this.lines = new ArrayList<>();
            this.lines.add(new Pair<>(0x00ff00, EngineSystem.ENG_NAME + " : " + EngineSystem.ENG_VER));
            this.lines.add(new Pair<>(0x00ff00, title));
            this.lines.add(new Pair<>(0x00ff00, "..."));
            this.counter = 0;
        }

        public void updateScreen() {
            GL11.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
            GL30.glClear(GL30.GL_COLOR_BUFFER_BIT | GL30.GL_DEPTH_BUFFER_BIT);
            int strokes = 0;
            for (Pair<Integer, String> s : this.lines) {
                String textPre = strokes < 3 ? "[*] " : "[" + ++this.counter + "] ";
                UIText textUI = new UIText(textPre + s.getSecond(), this.guiFont, s.getFirst(), new Vector2i(5, (strokes++) * 40 + 5), 0.5f);
                textUI.buildUI();
                textUI.render(0.0f);
                textUI.cleanData();
            }
            GLFW.glfwSwapBuffers(JGemsScreen.this.getWindow().getDescriptor());
            GLFW.glfwPollEvents();
        }

        private void addText(int color, String s) {
            this.lines.add(new Pair<>(color, s));
            int max = Math.max(((JGemsScreen.this.getWindowDimensions().y - 135) / 45), 4);
            if (this.lines.size() > max) {
                this.lines.remove(3);
            }
            this.updateScreen();
        }

        public void clean() {
            GL30.glClear(GL30.GL_COLOR_BUFFER_BIT | GL30.GL_DEPTH_BUFFER_BIT);
            this.guiFont.cleanUp();
            this.lines.clear();
        }
    }
}
