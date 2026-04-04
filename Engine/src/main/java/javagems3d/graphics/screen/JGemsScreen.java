package javagems3d.graphics.screen;

import api.system.scripting.JavaToJsAPI;
import javagems3d.graphics.world.IRenderWorld;
import javagems3d.help.JGemsHelper;
import javagems3d.system.global.JGemsConfig;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.screen.window.IWindow;
import javagems3d.system.resources.managing.ResourceManager;
import javagems3d.system.service.files.source.ISource;
import javagems3d.system.service.files.source.JGemsPathSource;
import javagems3d.system.service.profiler.SpeedProfiler;
import javagems3d.system.resources.managing.resources.SystemResources;
import logger.Log;
import org.joml.Vector2i;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.MemoryUtil;
import javagems3d.JGems3D;
import javagems3d.audio.sound.SoundListener;
import javagems3d.graphics.camera.base.ICamera;
import javagems3d.graphics.rendering.ui.jgems_imgui.elements.UIText;
import javagems3d.graphics.rendering.ui.jgems_imgui.elements.base.font.FontCode;
import javagems3d.graphics.rendering.ui.jgems_imgui.elements.base.font.JGemsGuiFont;
import javagems3d.graphics.rendering.scene.JGemsScene;
import javagems3d.graphics.screen.timer.JGemsTimedAction;
import javagems3d.graphics.screen.timer.TimerPool;
import javagems3d.graphics.screen.window.Window;
import javagems3d.graphics.world.SceneWorld;
import javagems3d.graphics.transformation.JGemsTransformManager;
import javagems3d.physics.world.thread.timer.PhysicsProcessor;
import javagems3d.system.controller.dispatcher.JGemsControllerDispatcher;
import javagems3d.system.core.JGemsCore;
import javagems3d.system.resources.managing.JGemsResourceManager;
import javagems3d.system.service.collections.Pair;
import javagems3d.system.service.exceptions.JGemsRuntimeException;
import javagems3d.system.service.files.JGemsPath;

import java.awt.*;
import java.util.ArrayList;

public class JGemsScreen implements IScreen {
    public static int RENDER_FPS;
    public static int PHYS_TPS;
    private final TimerPool timerPool;
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

    public void createObjects(IWindow window) {
        this.controllerDispatcher = new JGemsControllerDispatcher(window);
        this.scene = new JGemsScene(window, new SceneWorld());
    }

    public void createScreenAndContext() {
        Log.get().info("Init Graphics");

        if (this.tryToBuildScreen(!JGemsConfig.SYSTEM.DISABLE_FULLSCREEN_START_ADJUSTMENT && JGems3D.get().getGameSettings().windowMode.getValue() == 0)) {
            JGemsTransformManager.INSTANCE.setProjectionData(this.getWindow(), JGemsConfig.SYSTEM.FOV, JGemsConfig.SYSTEM.Z_NEAR, JGemsConfig.SYSTEM.Z_FAR);
            JGemsTransformManager.INSTANCE.updateSetOfMatrices(this.getWindow());

            if (!JGemsConfig.SYSTEM.DISABLE_FULLSCREEN_START_ADJUSTMENT) {
                this.adjustScreenMode();
            }
            this.adjustVSync();
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
            ResourceManager.initDefaults();
            JGemsResourceManager.createShaders();

            this.showGameLoadingScreen("System01");
            this.setScreenCallbacks();

            OpenGLRenderer.setViewPort(this.getWindow().getWindowSize());
            this.getWindow().showWindow();
        } else {
            throw new JGemsRuntimeException("Caught service, while building screen");
        }
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

    public boolean tryToBuildScreen(boolean fullScreen) {
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
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GL46.GL_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_DOUBLEBUFFER, GLFW.GLFW_TRUE);

        GLFWVidMode vidMode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
        boolean flag = vidMode != null && fullScreen;

        int width = flag ? vidMode.width() : JGemsConfig.SYSTEM.DEFAULT_SCREEN_WIDTH;
        int height = flag ? vidMode.height() : JGemsConfig.SYSTEM.DEFAULT_SCREEN_HEIGHT;

        this.window = new Window(width, height, JGems3D.getAPIAppData().getWindowProperties());
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

    public void adjustVSync() {
        if (JGems3D.get().getGameSettings().vSync.getValue() == 1) {
            this.getWindow().enableVSync();
        } else {
            this.getWindow().disableVSync();
        }
    }

    public void adjustScreenMode() {
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

    public void refreshSceneResources() {
        JGems3D.get().getScreen().adjustScreenMode();
        JGems3D.get().getScreen().adjustVSync();
        this.getScene().getSceneRenderer().recreateResources();
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
        this.loadingScreen.clear();
        this.loadingScreen = null;
    }

    private void updateController() {
        if (this.getControllerDispatcher() != null) {
            this.getControllerDispatcher().updateController(this.getWindow());
        }
    }

    public void runRenderThread() {
        Log.get().info("Starting screen");
        SoundListener.updateListenerGain(JGemsHelper.get().getGameSettings());
        GL46.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        this.getScene().preRender();
        this.removeLoadingScreen();
        if (!JGemsCore.loadTestMap()) {
            JGemsHelper.ui().openMainMenu();
        }
        try {
            this.renderLoop();
        } catch (Exception e) {
            throw new JGemsRuntimeException(e);
        } finally {
            this.getScene().postRender();
            this.getTimerPool().clear();
            {
                JavaToJsAPI.disposeRender();
            }
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
        while (!JGems3D.get().isShouldBeClosed()) {
            if (GLFW.glfwWindowShouldClose(this.getWindow().getDescriptor())) {
                JGems3D.close(null);
                break;
            }
            JGems3D.get().getCore().update();

            this.updateController();
            this.getWindow().setCursorFocused(this.getWindow().isWindowInFocus());
            this.getTimerPool().update();
            this.renderGameScene(deltaTimer.getDeltaTime());
            if (renderTimer.resetTimerAfterReachedSeconds(1.0d / JGemsConfig.SYSTEM.RENDER_TICKS_UPD_RATE)) {
                this.renderTicks += 0.01f;
            }
            fps += 1;
            if (perSecondTimer.resetTimerAfterReachedSeconds(1.0d)) {
                JGemsScreen.PHYS_TPS = PhysicsProcessor.TPS;
                JGemsScreen.RENDER_FPS = fps;
                PhysicsProcessor.TPS = 0;
                fps = 0;
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
        this.updateSound();
        OpenGLRenderer.catchGLContextExceptions();
    }

    private void updateSound() {
        JGems3D.get().getSoundManager().update();
        if (JGems3D.get().isCurrentGameMapPlayerValid()) {
            SoundListener.updateOrientationAndPosition(JGemsTransformManager.INSTANCE.getCameraViewMatrix(), this.getCamera().getCamPosition());
        }
        SoundListener.updateListenerGain(JGemsHelper.get().getGameSettings());
    }

    public IRenderWorld getSceneWorld() {
        return this.getScene() == null ? null : this.getScene().getWorld();
    }

    public ICamera getCamera() {
        return this.getScene().getCamera();
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

    public TimerPool getTimerPool() {
        return this.timerPool;
    }

    public class LoadingScreen {
        private final JGemsGuiFont guiFont;
        private final ArrayList<Pair<Integer, String>> lines;
        private int counter;

        public LoadingScreen(String title) {
            Font gameFont = SystemResources.createFontFromFile(new JGemsPathSource(new JGemsPath("/assets/jgems/gamefont.ttf"), ISource.Source.INSIDE_JAR));
            this.guiFont = new JGemsGuiFont(gameFont.deriveFont(Font.PLAIN, 20), FontCode.Window);
            this.lines = new ArrayList<>();
            this.lines.add(new Pair<>(0x00ff00, JGemsCore.ENG_NAME + " : " + JGemsCore.ENG_VER));
            this.lines.add(new Pair<>(0x00ff00, title));
            this.lines.add(new Pair<>(0x00ff00, "..."));
            this.counter = 0;
        }

        public void updateScreen() {
            GL46.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
            GL46.glClear(GL46.GL_COLOR_BUFFER_BIT | GL46.GL_DEPTH_BUFFER_BIT);
            int strokes = 0;
            for (Pair<Integer, String> s : this.lines) {
                String textPre = strokes < 3 ? "[*] " : "[" + ++this.counter + "] ";
                UIText textUI = new UIText(textPre + s.second(), this.guiFont, s.first(), new Vector2i(5, (strokes++) * 40 + 5), 0.5f);
                textUI.build();
                textUI.render(0.0f);
                textUI.clear();
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

        public void clear() {
            GL46.glClear(GL46.GL_COLOR_BUFFER_BIT | GL46.GL_DEPTH_BUFFER_BIT);
            this.guiFont.clear();
            this.lines.clear();
        }
    }
}
