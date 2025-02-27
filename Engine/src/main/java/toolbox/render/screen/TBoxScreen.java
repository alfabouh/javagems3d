package toolbox.render.screen;

import javagems3d.system.global.JGemsConfiguration;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.screen.OpenGLSysUtils;
import javagems3d.system.service.exceptions.JGemsNullException;
import logger.Log;
import org.joml.Vector2i;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL46;
import org.lwjgl.system.MemoryUtil;
import javagems3d.graphics.screen.IScreen;
import javagems3d.graphics.screen.timer.JGemsTimer;
import javagems3d.graphics.screen.timer.TimerPool;
import javagems3d.graphics.screen.window.IWindow;
import javagems3d.graphics.screen.window.Window;
import javagems3d.graphics.transformation.JGemsTransformManager;
import javagems3d.system.service.exceptions.JGemsRuntimeException;
import logger.SystemLogging;
import logger.managers.LoggingManager;
import toolbox.ToolBox;
import toolbox.controller.TBoxControllerDispatcher;
import toolbox.map_table.TBoxMapTable;
import toolbox.render.scene.TBoxScene;
import toolbox.render.scene.dear_imgui.content.LoadingContent;
import toolbox.resources.TBoxResourceManager;

import java.lang.reflect.InvocationTargetException;

public class TBoxScreen implements IScreen {
    public static int FPS;
    private final TimerPool timerPool;
    private Window tBoxWindow;
    private TBoxControllerDispatcher controllerDispatcher;
    private TBoxScene scene;
    private TBoxResourceManager resourceManager;
    private JGemsTransformManager JGemsTransformManager;

    public TBoxScreen() {
        this.timerPool = new TimerPool();
    }

    public boolean tryToBuildScreen() {
        GLFWErrorCallback.createPrint(System.err).set();
        if (!GLFW.glfwInit()) {
            throw new JGemsRuntimeException("Error, while initializing GLFW");
        }
        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 3);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 4);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GL46.GL_TRUE);
        GLFW.glfwWindowHint(GLFW.GLFW_DOUBLEBUFFER, GLFW.GLFW_TRUE);

        OpenGLSysUtils.registerOGLDebugOutput();

        GLFWVidMode vidMode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
        this.tBoxWindow = new Window(JGemsConfiguration.SYSTEM.DEFAULT_SCREEN_WIDTH, JGemsConfiguration.SYSTEM.DEFAULT_SCREEN_HEIGHT, new Window.WindowProperties(ToolBox.get().toString()));
        long window = this.getWindow().getDescriptor();
        if (window == MemoryUtil.NULL) {
            throw new JGemsNullException("Failed to create the GLFW window");
        }
        if (vidMode != null) {
            int x = (vidMode.width() - JGemsConfiguration.SYSTEM.DEFAULT_SCREEN_WIDTH) / 2;
            int y = (vidMode.height() - JGemsConfiguration.SYSTEM.DEFAULT_SCREEN_HEIGHT) / 2;
            GLFW.glfwSetWindowPos(window, x, y);
        } else {
            return false;
        }
        GLFW.glfwMakeContextCurrent(window);
        GLFW.glfwSwapInterval(1);
        return true;
    }

    private void loadResourcesAndRenderLoadingScreen() {
        GL46.glClear(GL46.GL_COLOR_BUFFER_BIT | GL46.GL_DEPTH_BUFFER_BIT);
        this.getScene().getDimGuiRenderTBox().setCurrentContentToRender(new LoadingContent());
        for (int i = 0; i < 5; i++) {
            this.getScene().getDimGuiRenderTBox().render(0);
        }
        GLFW.glfwSwapBuffers(this.getWindow().getDescriptor());
        GLFW.glfwPollEvents();
        this.getResourceManager().loadResources();
        try {
            TBoxMapTable.INSTANCE.init(this.getResourceManager());
        } catch (InvocationTargetException | NoSuchMethodException | InstantiationException |
                 IllegalAccessException e) {
            throw new JGemsRuntimeException(e);
        }
    }

    public void runRenderThread() {
        SystemLogging.get().getLogManager().info("Start screen thread");
        GL46.glClearColor(0.0f, 0.0f, 0.1f, 1.0f);
        this.getScene().createGUI();
        this.loadResourcesAndRenderLoadingScreen();
        this.getScene().preRender();
        try {
            this.renderLoop();
        } catch (InterruptedException e) {
            throw new JGemsRuntimeException(e);
        } finally {
            this.getScene().postRender();
            SystemLogging.get().getLogManager().info("Stop screen thread");
            this.getTimerPool().clear();
            GLFW.glfwDestroyWindow(this.getWindow().getDescriptor());
            GLFW.glfwTerminate();
        }
    }

    private void renderLoop() throws InterruptedException {
        JGemsTimer deltaTimer = this.getTimerPool().createTimer();
        JGemsTimer fpsTimer = this.getTimerPool().createTimer();
        GL46.glClearColor(0.4f, 0.4f, 0.8f, 1.0f);
        int fps = 0;


        while (!ToolBox.get().isShouldBeClosed()) {
            if (GLFW.glfwWindowShouldClose(this.getWindow().getDescriptor())) {
                ToolBox.get().closeTBox();
                break;
            }

            this.getTimerPool().update();
            this.getControllerDispatcher().updateController(this.getWindow());
            this.getTransformationUtils().updateCamera(this.getScene().getCamera());
            this.renderGameScene(deltaTimer.getDeltaTime());

            fps += 1;
            if (fpsTimer.resetTimerAfterReachedSeconds(1.0f)) {
                TBoxScreen.FPS = fps;
                fps = 0;
            }

            GLFW.glfwSwapBuffers(this.getWindow().getDescriptor());
            GLFW.glfwPollEvents();
        }
    }

    private void renderGameScene(float delta) throws InterruptedException {
        GL46.glEnable(GL46.GL_CULL_FACE);
        GL46.glCullFace(GL46.GL_BACK);
        GL46.glDepthFunc(GL46.GL_LESS);
        GL46.glClear(GL46.GL_COLOR_BUFFER_BIT | GL46.GL_DEPTH_BUFFER_BIT);
        this.getScene().render(delta);
        OpenGLRenderer.catchGLContextExceptions();
    }

    private void resizeWindow(IWindow window) {
        OpenGLRenderer.setViewPort(window.getWindowSize());
        this.getScene().onWindowResize(window);
    }

    private void setScreenCallbacks() {
        Callbacks.glfwFreeCallbacks(this.getWindow().getDescriptor());
        GLFW.glfwSetWindowSizeCallback(this.getWindow().getDescriptor(), (a, b, c) -> {
            this.resizeWindow(this.getWindow());
        });
        GLFWErrorCallback glfwErrorCallback = GLFW.glfwSetErrorCallback(null);
        if (glfwErrorCallback != null) {
            glfwErrorCallback.free();
        }
    }

    private void createResourceManager() {
        this.resourceManager = new TBoxResourceManager();
    }

    public void createObjects(IWindow window) {
        this.controllerDispatcher = new TBoxControllerDispatcher(window);
        this.scene = new TBoxScene(this.getTransformationUtils(), window);
    }

    @Override
    public void createScreenAndContext() {
        SystemLogging.get().getLogManager().info("Building screen");
        try {
            if (this.tryToBuildScreen()) {
                GL.createCapabilities();

                this.createResourceManager();
                TBoxResourceManager.createShaders();
                this.setScreenCallbacks();
                this.createObjects(this.getWindow());
                OpenGLRenderer.setViewPort(this.getWindow().getWindowSize());
            } else {
                throw new JGemsRuntimeException("Caught exception, while building screen!");
            }
        } catch (Exception e) {
            Log.get().exception(e);
            LoggingManager.showExceptionDialog("Couldn't create window");
        }
    }

    public TBoxResourceManager getResourceManager() {
        return this.resourceManager;
    }

    public JGemsTransformManager getTransformationUtils() {
        return JGemsTransformManager;
    }

    public TBoxControllerDispatcher getControllerDispatcher() {
        return this.controllerDispatcher;
    }

    public TimerPool getTimerPool() {
        return this.timerPool;
    }

    public TBoxScene getScene() {
        return this.scene;
    }

    public Vector2i getDimensions() {
        return this.getWindow().getWindowSize();
    }

    public Window getWindow() {
        return this.tBoxWindow;
    }
}
