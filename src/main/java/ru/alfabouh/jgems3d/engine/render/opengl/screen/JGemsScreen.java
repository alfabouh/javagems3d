package ru.alfabouh.jgems3d.engine.render.opengl.screen;

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
import ru.alfabouh.jgems3d.engine.JGems;
import ru.alfabouh.jgems3d.engine.audio.sound.SoundListener;
import ru.alfabouh.jgems3d.engine.physics.world.timer.PhysicsTimer;
import ru.alfabouh.jgems3d.engine.render.opengl.scene.JGemsScene;
import ru.alfabouh.jgems3d.engine.render.opengl.scene.immediate_gui.elements.UIText;
import ru.alfabouh.jgems3d.engine.render.opengl.scene.immediate_gui.elements.base.font.FontCode;
import ru.alfabouh.jgems3d.engine.render.opengl.scene.immediate_gui.elements.base.font.GuiFont;
import ru.alfabouh.jgems3d.engine.render.opengl.scene.utils.JGemsSceneUtils;
import ru.alfabouh.jgems3d.engine.render.opengl.scene.world.SceneWorld;
import ru.alfabouh.jgems3d.engine.render.opengl.scene.world.camera.ICamera;
import ru.alfabouh.jgems3d.engine.render.opengl.screen.timer.TimerPool;
import ru.alfabouh.jgems3d.engine.render.opengl.screen.window.Window;
import ru.alfabouh.jgems3d.engine.render.opengl.screen.timer.GameRenderTimer;
import ru.alfabouh.jgems3d.engine.render.transformation.TransformationUtils;
import ru.alfabouh.jgems3d.engine.system.EngineSystem;
import ru.alfabouh.jgems3d.engine.system.controller.dispatcher.JGemsControllerDispatcher;
import ru.alfabouh.jgems3d.engine.system.resources.ResourceManager;
import ru.alfabouh.jgems3d.engine.system.exception.JGemsException;
import ru.alfabouh.jgems3d.logger.SystemLogging;

import java.awt.*;
import java.util.ArrayList;

public class JGemsScreen implements IScreen {
    public static int RENDER_FPS;
    public static int PHYS_TPS;
    public static final double RENDER_TICKS_UPD_RATE = 60.0d;
    private TransformationUtils transformationUtils;

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

    public void addLineInLoadingScreen(String s) {
        if (this.loadingScreen == null) {
            SystemLogging.get().getLogManager().warn("Loading screen is NULL");
            return;
        }
        this.loadingScreen.addText(s);
    }

    public void buildScreen() {
        SystemLogging.get().getLogManager().log("Init Graphics!");
        if (this.tryToBuildScreen()) {
            this.createTransformationUtils();

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

            SystemLogging.get().getLogManager().log("JGemsScreen built successful");
        } else {
            throw new JGemsException("Caught exception, while building screen!!");
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
        this.transformationUtils = new TransformationUtils(this.window, JGemsSceneUtils.FOV, JGemsSceneUtils.Z_NEAR, JGemsSceneUtils.Z_FAR);
    }

    private void createObjects(ru.alfabouh.jgems3d.engine.render.opengl.screen.window.Window window) {
        this.controllerDispatcher = new JGemsControllerDispatcher(window);
        this.scene = new JGemsScene(this.getTransformationUtils(), this, new SceneWorld(JGems.get().getPhysicsWorld()));
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
            throw new JGemsException("Error, while initializing GLFW");
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
        this.window = new ru.alfabouh.jgems3d.engine.render.opengl.screen.window.Window(new ru.alfabouh.jgems3d.engine.render.opengl.screen.window.Window.WindowProperties(flag ? vidMode.width() : ru.alfabouh.jgems3d.engine.render.opengl.screen.window.Window.defaultW, flag ? vidMode.height() : ru.alfabouh.jgems3d.engine.render.opengl.screen.window.Window.defaultH, JGems.get().toString()));
        long window = this.getWindow().getDescriptor();
        if (window == MemoryUtil.NULL) {
            throw new JGemsException("Failed to create the GLFW window");
        }
        if (vidMode != null) {
            int x = (vidMode.width() - ru.alfabouh.jgems3d.engine.render.opengl.screen.window.Window.defaultW) / 2;
            int y = (vidMode.height() - ru.alfabouh.jgems3d.engine.render.opengl.screen.window.Window.defaultH) / 2;
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
        this.getScene().getSceneRender().createFBOs(this.getWindowDimensions());
        this.getScene().getSceneRender().getShadowScene().createFBO();
    }

    public void showGameLoadingScreen() {
        this.loadingScreen = new LoadingScreen();
        this.loadingScreen.updateScreen();
    }

    public void removeLoadingScreen() {
        this.loadingScreen.clean();
        this.loadingScreen = null;
    }

    private void updateController() {
        if (this.getControllerDispatcher() != null) {
            this.getControllerDispatcher().updateController(this.getWindow());
        }
    }

    public void startScreenRenderProcess() {
        SystemLogging.get().getLogManager().log("Starting screen...");
        SoundListener.updateListenerGain();
        GL11.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        this.getScene().preRender();
        this.removeLoadingScreen();
        JGems.get().showMainMenu();
        try {
            this.renderLoop();
        } catch (InterruptedException e) {
            throw new JGemsException(e);
        } finally {
            this.getScene().postRender();
            SystemLogging.get().getLogManager().log("Destroying screen...");
            this.getTimerPool().clear();
            GLFW.glfwDestroyWindow(this.getWindow().getDescriptor());
            GLFW.glfwTerminate();
        }
    }

    private void renderLoop() throws InterruptedException {
        int fps = 0;
        GameRenderTimer perSecondTimer = this.getTimerPool().createTimer();
        GameRenderTimer renderTimer = this.getTimerPool().createTimer();
        GameRenderTimer deltaTimer = this.getTimerPool().createTimer();
        while (!JGems.get().isShouldBeClosed()) {
            if (GLFW.glfwWindowShouldClose(this.getWindow().getDescriptor())) {
                JGems.get().destroyGame();
                break;
            }
            this.updateController();
            this.getWindow().refreshFocusState();
            this.getTimerPool().update();
            this.getTransformationUtils().updateMatrices();
            JGems.get().getProxy().update();
            this.renderGameScene(deltaTimer.getDeltaTime());
            if (renderTimer.resetTimerAfterReachedSeconds(1.0d / JGemsScreen.RENDER_TICKS_UPD_RATE)) {
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
        GL30.glCullFace(GL30.GL_BACK);
        GL11.glDepthFunc(GL11.GL_LESS);
        this.getScene().renderScene(delta);
        this.updateSound();
        JGemsSceneUtils.checkGLErrors();
    }

    private void updateSound() {
        JGems.get().getSoundManager().update();
        if (JGems.get().isValidPlayer()) {
            SoundListener.updateOrientationAndPosition(JGemsSceneUtils.getMainCameraViewMatrix(), this.getCamera().getCamPosition());
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

    public JGemsControllerDispatcher getControllerDispatcher() {
        return this.controllerDispatcher;
    }

    public Vector2i getWindowDimensions() {
        return this.getWindow().getWindowDimensions();
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

    public TransformationUtils getTransformationUtils() {
        return this.transformationUtils;
    }

    public class LoadingScreen {
        private final GuiFont guiFont;
        private final ArrayList<String> lines;

        public LoadingScreen() {
            SystemLogging.get().getLogManager().log("Loading screen");
            Font gameFont = ResourceManager.createFontFromJAR("gamefont.ttf");
            this.guiFont = new GuiFont(gameFont.deriveFont(Font.PLAIN, 20), FontCode.Window);
            this.lines = new ArrayList<>();
            this.lines.add(EngineSystem.ENG_NAME + " : " + EngineSystem.ENG_VER);
            this.lines.add("...");
            this.lines.add("System01...");
            this.lines.add("...");
        }

        public void updateScreen() {
            GL11.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
            GL30.glClear(GL30.GL_COLOR_BUFFER_BIT);
            int strokes = 0;
            for (String s : this.lines) {
                UIText textUI = new UIText(s, this.guiFont, 0x00ff00, new Vector2i(5, (strokes++) * 40 + 5), 0.5f);
                textUI.buildUI();
                textUI.render(0.0f);
                textUI.cleanData();
            }
            GLFW.glfwSwapBuffers(JGemsScreen.this.getWindow().getDescriptor());
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
