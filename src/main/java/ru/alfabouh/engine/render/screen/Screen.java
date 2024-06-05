package ru.alfabouh.engine.render.screen;

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
import ru.alfabouh.engine.audio.sound.SoundListener;
import ru.alfabouh.engine.JGems;
import ru.alfabouh.engine.render.scene.immediate_gui.elements.UIText;
import ru.alfabouh.engine.system.EngineSystem;
import ru.alfabouh.engine.system.controller.ControllerDispatcher;
import ru.alfabouh.engine.system.exception.GameException;
import ru.alfabouh.engine.system.resources.ResourceManager;
import ru.alfabouh.engine.physics.world.timer.PhysicsTimer;
import ru.alfabouh.engine.render.scene.Scene;
import ru.alfabouh.engine.render.scene.immediate_gui.elements.base.font.FontCode;
import ru.alfabouh.engine.render.scene.immediate_gui.elements.base.font.GuiFont;
import ru.alfabouh.engine.render.scene.world.SceneWorld;
import ru.alfabouh.engine.render.scene.world.camera.ICamera;
import ru.alfabouh.engine.render.screen.timer.GameRenderTimer;
import ru.alfabouh.engine.render.screen.timer.TimerPool;
import ru.alfabouh.engine.render.screen.window.Window;
import ru.alfabouh.engine.render.transformation.TransformationManager;

import java.awt.*;
import java.util.ArrayList;

public class Screen {
    public static int RENDER_FPS;
    public static int PHYS_TPS;
    public static final double RENDER_TICKS_UPD_RATE = 60.0d;

    private final TimerPool timerPool;
    private ControllerDispatcher controllerDispatcher;
    private Scene scene;
    private Window window;
    private Screen.GameLoadingScreen gameLoadingScreen;
    private float renderTicks;

    public Screen() {
        this.timerPool = new TimerPool();
        this.gameLoadingScreen = null;
        this.renderTicks = 0.0f;
    }

    public void addLineInLoadingScreen(String s) {
        if (this.gameLoadingScreen == null) {
            JGems.get().getLogManager().warn("Loading screen is NULL");
            return;
        }
        this.gameLoadingScreen.addText(s);
    }

    public void buildScreen() {
        JGems.get().getLogManager().log("Init Graphics!");
        if (this.tryToBuildScreen()) {
            this.checkScreenMode();
            this.checkVSync();

            GL.createCapabilities();
            ResourceManager.loadShaders();

            this.showGameLoadingScreen();
            this.addLineInLoadingScreen("Drawing scene...");
            this.setScreenCallbacks();
            this.createObjects(this.getWindow());

            this.normalizeViewPort();
            this.getWindow().showWindow();

            JGems.get().getLogManager().log("Screen built successful");
        } else {
            throw new GameException("Caught exception, while building screen!!");
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

    private void createObjects(Window window) {
        this.controllerDispatcher = new ControllerDispatcher(window);
        this.scene = new Scene(this, new SceneWorld(JGems.get().getPhysicsWorld()));
    }

    private void resizeWindow(Vector2i dim) {
        this.getScene().onWindowResize(dim);
    }

    public void normalizeViewPort() {
        GL30.glViewport(0, 0, this.getDimensions().x, this.getDimensions().y);
    }

    private boolean tryToBuildScreen() {
        GLFWErrorCallback.createPrint(System.err).set();
        if (!GLFW.glfwInit()) {
            throw new GameException("Error, while initializing GLFW");
        }
        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 3);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 4);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GL20.GL_TRUE);
        GLFW.glfwWindowHint(GLFW.GLFW_DOUBLEBUFFER, GLFW.GLFW_TRUE);
        if (JGems.DEBUG_MODE) {
            GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_DEBUG_CONTEXT, GLFW.GLFW_TRUE);
        }
        GLFWVidMode vidMode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
        boolean flag = vidMode != null && JGems.get().getGameSettings().windowMode.getValue() == 0;
        this.window = new Window(new Window.WindowProperties(flag ? vidMode.width() : Window.defaultW, flag ? vidMode.height() : Window.defaultH, JGems.get().toString()));
        long window = this.getWindow().getDescriptor();
        if (window == MemoryUtil.NULL) {
            throw new GameException("Failed to create the GLFW window");
        }
        if (vidMode != null) {
            int x = (vidMode.width() - Window.defaultW) / 2;
            int y = (vidMode.height() - Window.defaultH) / 2;
            GLFW.glfwSetWindowPos(window, x, y);
        } else {
            return false;
        }
        GLFW.glfwMakeContextCurrent(window);
        return true;
    }

    public void checkVSync() {
        if (JGems.get().getGameSettings().vSync.getValue() == 1) {
            this.getWindow().enableVSync();
        } else {
            this.getWindow().disableVSync();
        }
    }

    public void checkScreenMode() {
        if (JGems.get().getGameSettings().windowMode.getValue() == 0) {
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
        this.getScene().getSceneRender().createFBOs(this.getDimensions());
        this.getScene().getSceneRender().getShadowScene().createFBO();
    }

    public void showGameLoadingScreen() {
        this.gameLoadingScreen = new Screen.GameLoadingScreen();
        this.gameLoadingScreen.updateScreen();
    }

    public void removeLoadingScreen() {
        this.gameLoadingScreen.clean();
        this.gameLoadingScreen = null;
    }

    public void startScreenRenderProcess() {
        JGems.get().getLogManager().log("Starting screen...");
        SoundListener.updateListenerGain();

        GL11.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        this.getScene().preRender();
        this.removeLoadingScreen();
        JGems.get().showMainMenu();

        try {
            this.renderLoop();
        } catch (InterruptedException e) {
            throw new GameException(e);
        }

        this.getScene().postRender();
        JGems.get().getLogManager().log("Destroying screen...");
        this.getTimerPool().clear();
        GLFW.glfwDestroyWindow(this.getWindow().getDescriptor());
        GLFW.glfwTerminate();
    }

    private void renderLoop() throws InterruptedException {
        int fps = 0;
        GameRenderTimer perSecondTimer = this.getTimerPool().createTimer();
        GameRenderTimer renderTimer = this.getTimerPool().createTimer();
        GameRenderTimer deltaTimer = this.getTimerPool().createTimer();
        while (!JGems.get().isShouldBeClosed()) {
            this.getTimerPool().update();
            if (GLFW.glfwWindowShouldClose(this.getWindow().getDescriptor())) {
                JGems.get().destroyGame();
                break;
            }
            JGems.get().getProxy().update();
            if (this.getControllerDispatcher() != null) {
                this.getControllerDispatcher().updateController(this.getWindow());
                this.getWindow().refreshFocusState();
            }
            this.renderGameScene(deltaTimer.getDeltaTime());
            if (renderTimer.resetTimerAfterReachedSeconds(1.0d / Screen.RENDER_TICKS_UPD_RATE)) {
                this.renderTicks += 0.01f;
            }
            fps += 1;
            if (perSecondTimer.resetTimerAfterReachedSeconds(1.0d)) {
                Screen.PHYS_TPS = PhysicsTimer.TPS;
                Screen.RENDER_FPS = fps;
                PhysicsTimer.TPS = 0;
                fps = 0;
            }
            GLFW.glfwSwapBuffers(this.getWindow().getDescriptor());
            GLFW.glfwPollEvents();
        }
    }

    private void renderGameScene(double delta) throws InterruptedException {
        GL30.glEnable(GL30.GL_CULL_FACE);
        GL30.glCullFace(GL30.GL_BACK);
        GL11.glDepthFunc(GL11.GL_LESS);
        this.getScene().renderScene(delta);
        this.updateSound();
        Scene.checkGLErrors();
    }

    private void updateSound() {
        JGems.get().getSoundManager().update();
        if (JGems.get().isValidPlayer()) {
            SoundListener.updateOrientationAndPosition(TransformationManager.instance.getMainCameraViewMatrix(), this.getCamera().getCamPosition());
        }
        SoundListener.updateListenerGain();
    }

    public SceneWorld getRenderWorld() {
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

    public ControllerDispatcher getControllerDispatcher() {
        return this.controllerDispatcher;
    }

    public int getWidth() {
        return this.getWindow().getWidth();
    }

    public int getHeight() {
        return this.getWindow().getHeight();
    }

    public Vector2i getDimensions() {
        return this.getWindow().getWindowDimensions();
    }

    public Scene getScene() {
        return this.scene;
    }

    public Window getWindow() {
        return this.window;
    }

    public TimerPool getTimerPool() {
        return this.timerPool;
    }

    public class GameLoadingScreen {
        private final GuiFont guiFont;
        private final ArrayList<String> lines;

        public GameLoadingScreen() {
            JGems.get().getLogManager().log("Loading screen");
            Font gameFont = ResourceManager.createFontFromJAR("gamefont.ttf");
            this.guiFont = new GuiFont(gameFont.deriveFont(Font.PLAIN, 20), FontCode.Window);
            this.lines = new ArrayList<>();
            this.lines.add(EngineSystem.ENG_NAME + " : " + EngineSystem.ENG_VER);
            this.lines.add("...");
            this.lines.add("System01...");
            this.lines.add("...");
        }

        public void updateScreen() {
            GL30.glClear(GL30.GL_COLOR_BUFFER_BIT);
            GL11.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
            int strokes = 0;
            for (String s : this.lines) {
                UIText textUI = new UIText(s, this.guiFont,0x00ff00, new Vector2i(5, (strokes++) * 40 + 5), 0.5f);
                textUI.render(0.0f);
                textUI.cleanData();
            }
            GLFW.glfwSwapBuffers(Screen.this.getWindow().getDescriptor());
            GLFW.glfwPollEvents();
        }

        private void addText(String s) {
            this.lines.add(s);
            this.updateScreen();
        }

        public void clean() {
            this.guiFont.cleanUp();
            this.lines.clear();
        }
    }
}
