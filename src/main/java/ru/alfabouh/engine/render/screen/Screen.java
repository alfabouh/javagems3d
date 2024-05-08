package ru.alfabouh.engine.render.screen;

import org.joml.Vector2d;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.*;
import org.lwjgl.system.MemoryUtil;
import ru.alfabouh.engine.audio.sound.SoundListener;
import ru.alfabouh.engine.game.Game;
import ru.alfabouh.engine.game.GameSystem;
import ru.alfabouh.engine.game.controller.ControllerDispatcher;
import ru.alfabouh.engine.game.exception.GameException;
import ru.alfabouh.engine.game.resources.ResourceManager;
import ru.alfabouh.engine.math.MathHelper;
import ru.alfabouh.engine.physics.world.timer.PhysicsTimer;
import ru.alfabouh.engine.render.scene.Scene;
import ru.alfabouh.engine.render.scene.gui.font.FontCode;
import ru.alfabouh.engine.render.scene.gui.font.GuiFont;
import ru.alfabouh.engine.render.scene.gui.ui.TextUI;
import ru.alfabouh.engine.render.scene.scene_render.groups.*;
import ru.alfabouh.engine.render.scene.world.SceneWorld;
import ru.alfabouh.engine.render.scene.world.camera.ICamera;
import ru.alfabouh.engine.render.screen.timer.Timer;
import ru.alfabouh.engine.render.screen.window.Window;
import ru.alfabouh.engine.render.transformation.TransformationManager;

import java.awt.*;
import java.util.ArrayList;

public class Screen {
    public static final double RENDER_TICKS_UPD_RATE = 60.0d;
    public static final int defaultW = 1280;
    public static final int defaultH = 720;
    public static int FPS;
    public static boolean lastFrame;
    public static int PHYS2_TPS;
    private final Timer timer;
    private ControllerDispatcher controllerDispatcher;
    private Scene scene;
    private Window window;
    private GameLoadingScreen gameLoadingScreen;
    private double lastRenderTicksUpdate;
    private float renderTicks;

    public Screen() {
        this.timer = new Timer();
        this.gameLoadingScreen = null;
        this.lastRenderTicksUpdate = Game.glfwTime();
        this.renderTicks = 0.0f;
    }

    public static boolean isScreenActive() {
        Window window1 = Game.getGame().getScreen().getWindow();
        if (window1.getWidth() == 0 || window1.getHeight() == 0) {
            return false;
        }
        return GLFW.glfwGetWindowAttrib(window1.getDescriptor(), GLFW.GLFW_ICONIFIED) == 0;
    }

    public static void setViewport(Vector2d dim) {
        GL30.glViewport(0, 0, (int) dim.x, (int) dim.y);
    }

    public static void setViewport(Vector2i dim) {
        GL30.glViewport(0, 0, dim.x, dim.y);
    }

    public static boolean isInFocus() {
        return Game.getGame().getScreen().getWindow().isInFocus();
    }

    public void initScreen() {
        this.scene = new Scene(this, new SceneWorld(Game.getGame().getPhysicsWorld()));
        this.setWindowCallbacks();
        this.createControllerDispatcher(this.getWindow());
    }

    public void addLineInLoadingScreen(String s) {
        if (this.gameLoadingScreen == null) {
            Game.getGame().getLogManager().warn("Loading screen is NULL");
            return;
        }
        this.gameLoadingScreen.addText(s);
    }

    private void initShaders() {
        ResourceManager.shaderAssets.loadAllShaders();
        ResourceManager.shaderAssets.startShaders();
    }

    public void buildScreen() {
        if (this.tryToBuildScreen()) {
            GL.createCapabilities();
            this.initShaders();
            this.showGameLoadingScreen();
            Game.getGame().getLogManager().log("Screen built successful");
        } else {
            throw new GameException("Screen build error!");
        }
    }

    private void createControllerDispatcher(Window window) {
        this.controllerDispatcher = new ControllerDispatcher(window);
    }

    public void startScreen() {
        this.updateScreen();
        Game.getGame().getLogManager().log("Stopping screen...");
        GLFW.glfwDestroyWindow(this.getWindow().getDescriptor());
        GLFW.glfwTerminate();
    }

    private void setWindowCallbacks() {
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

    private void resizeWindow(Vector2i dim) {
        this.getScene().onWindowResize(dim);
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
        if (Game.DEBUG_MODE) {
            GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_DEBUG_CONTEXT, GLFW.GLFW_TRUE);
        }
        this.window = new Window(new Window.WindowProperties(Screen.defaultW, Screen.defaultH, Game.getGame().toString()));
        long window = this.getWindow().getDescriptor();
        if (window == MemoryUtil.NULL) {
            throw new GameException("Failed to create the GLFW window");
        }
        GLFWVidMode vidMode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
        if (vidMode != null) {
            int x = (vidMode.width() - Screen.defaultW) / 2;
            int y = (vidMode.height() - Screen.defaultH) / 2;
            GLFW.glfwSetWindowPos(window, x, y);
        } else {
            return false;
        }
        GLFW.glfwMakeContextCurrent(window);
        this.checkScreenMode();
        this.checkVSync();
        return true;
    }

    public void reloadFBOs() {
        this.getScene().getSceneRender().createFBOs(this.getDimensions());
        this.getScene().getSceneRender().getShadowScene().createFBO();
    }

    public void checkVSync() {
        if (Game.getGame().getGameSettings().vSync.getValue() == 1) {
            this.enableVSync();
        } else {
            this.disableVSync();
        }
    }

    public void enableVSync() {
        GLFW.glfwSwapInterval(1);
    }

    public void disableVSync() {
        GLFW.glfwSwapInterval(0);
    }

    public void checkScreenMode() {
        if (Game.getGame().getGameSettings().windowMode.getValue() == 0) {
            if (!this.isFullScreen()) {
                this.makeFullScreen();
            }
        } else {
            if (this.isFullScreen()) {
                this.removeFullScreen();
            }
        }
    }

    public boolean isFullScreen() {
        return GLFW.glfwGetWindowMonitor(this.getWindow().getDescriptor()) != 0;
    }

    public void switchScreenMode() {
        if (this.isFullScreen()) {
            this.removeFullScreen();
        } else {
            this.makeFullScreen();
        }
    }

    public void makeFullScreen() {
        GLFWVidMode vidMode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
        if (vidMode != null) {
            GLFW.glfwSetWindowMonitor(this.getWindow().getDescriptor(), GLFW.glfwGetPrimaryMonitor(), 0, 0, vidMode.width(), vidMode.height(), GLFW.GLFW_DONT_CARE);
            Game.getGame().getLogManager().log("FullScreen mode");
        } else {
            throw new GameException("Monitor None");
        }
        this.checkVSync();
    }

    public void removeFullScreen() {
        GLFWVidMode vidMode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
        if (vidMode != null) {
            int x = (vidMode.width() - Screen.defaultW) / 2;
            int y = (vidMode.height() - Screen.defaultH) / 2;
            GLFW.glfwSetWindowMonitor(this.getWindow().getDescriptor(), 0, x, y, Screen.defaultW, Screen.defaultH, GLFW.GLFW_DONT_CARE);
            Game.getGame().getLogManager().log("DefaultScreen mode");
        } else {
            throw new GameException("Monitor None");
        }
        this.checkVSync();
    }

    public void hideWindow() {
        GLFW.glfwHideWindow(this.getWindow().getDescriptor());
    }

    public void showWindow() {
        Screen.setViewport(this.getDimensions());
        GLFW.glfwShowWindow(this.getWindow().getDescriptor());
        GLFW.glfwFocusWindow(this.getWindow().getDescriptor());
    }

    public void showGameLoadingScreen() {
        this.gameLoadingScreen = new GameLoadingScreen();
        this.gameLoadingScreen.updateScreen();
    }

    public void removeLoadingScreen() {
        this.gameLoadingScreen.clean();
        this.gameLoadingScreen = null;
    }

    public Timer getTimer() {
        return this.timer;
    }

    public SceneWorld getRenderWorld() {
        return this.getScene().getSceneWorld();
    }

    public ICamera getCamera() {
        return this.getScene().getCurrentCamera();
    }

    private void enableMSAA() {
        GL11.glEnable(GL13.GL_MULTISAMPLE);
        GLFW.glfwWindowHint(GLFW.GLFW_SAMPLES, (int) MathHelper.clamp(Game.getGame().getGameSettings().msaa.getValue(), 0.0f, 8.0f));
    }

    private void updateScreen() {
        GL11.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        this.getScene().preRender();
        try {
            this.renderLoop();
        } catch (InterruptedException e) {
            throw new GameException(e);
        }
        this.getScene().postRender();
    }

    public void showMainMenu() {
        this.getScene().getGui().showMainMenu();
    }

    private void renderLoop() throws InterruptedException {
        this.enableMSAA();
        int fps = 0;
        double lastFPS = Game.glfwTime();

        this.removeLoadingScreen();
        this.showMainMenu();
        while (!Game.getGame().isShouldBeClosed()) {
            if (GLFW.glfwWindowShouldClose(this.getWindow().getDescriptor())) {
                Game.getGame().destroyGame();
                break;
            }
            double currentTime = Game.glfwTime();
            double delta = this.getTimer().getDeltaTime();
            double updRate = (1000.0d / (this.getWindow().monitorRefreshRate() - 1)) * 0.001d;
            double sync = updRate - delta;
            if (sync > 0.1275d) {
                updRate -= sync;
                Game.getGame().getLogManager().warn("Slow frames. Sync: " + sync);
            }
            this.inLoop(delta);
            fps += 1;
            if (currentTime - lastFPS >= 1.0f) {
                Screen.PHYS2_TPS = PhysicsTimer.TPS;
                PhysicsTimer.TPS = 0;
                Screen.FPS = fps;
                fps = 0;
                lastFPS = currentTime;
            }

            GLFW.glfwSwapBuffers(this.getWindow().getDescriptor());
            GLFW.glfwPollEvents();
        }
    }

    private void inLoop(double delta) throws InterruptedException {
        Game.getGame().getProxy().update();

        if (this.getControllerDispatcher() != null) {
            this.getControllerDispatcher().updateController(this.getWindow());
            this.getWindow().refreshFocusState();
        }

        GL30.glClear(GL30.GL_COLOR_BUFFER_BIT | GL30.GL_DEPTH_BUFFER_BIT | GL30.GL_STENCIL_BUFFER_BIT);
        GL30.glEnable(GL30.GL_CULL_FACE);
        GL30.glCullFace(GL30.GL_BACK);
        GL11.glDepthFunc(GL11.GL_LESS);

        Screen.lastFrame = false;
        this.getScene().renderScene(delta);
        Screen.lastFrame = true;

        double curr = Game.glfwTime();
        if (curr - this.lastRenderTicksUpdate > 1.0d / Screen.RENDER_TICKS_UPD_RATE) {
            this.renderTicks += 0.01f;
            this.lastRenderTicksUpdate = curr;
        }

        Game.getGame().getSoundManager().update();
        if (Game.getGame().isValidPlayer()) {
            SoundListener.updateOrientationAndPosition(TransformationManager.instance.getMainCameraViewMatrix(), this.getCamera().getCamPosition());
        }
        SoundListener.updateListenerGain();
        Scene.checkGLErrors();
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

    public class GameLoadingScreen {
        private final GuiFont guiFont;
        private final ArrayList<String> lines;

        public GameLoadingScreen() {
            Game.getGame().getLogManager().log("Loading screen");
            Font gameFont = ResourceManager.createFontFromJAR("gamefont.ttf");
            this.guiFont = new GuiFont(gameFont.deriveFont(Font.PLAIN, 20), FontCode.Window);
            this.lines = new ArrayList<>();
            this.lines.add(GameSystem.ENG_NAME + " : " + GameSystem.ENG_VER);
            this.lines.add("...");
            this.lines.add("Loading game...");
        }

        public void updateScreen() {
            GL30.glClear(GL30.GL_COLOR_BUFFER_BIT);
            GL11.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
            int strokes = 0;
            for (String s : this.lines) {
                TextUI textUI = new TextUI(s, this.guiFont, 0x00ff00, new Vector3f(5.0f, (strokes++) * 40.0f + 5.0f, 0.5f));
                textUI.render(0.0f);
                textUI.clear();
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
