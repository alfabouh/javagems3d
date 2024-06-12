package ru.alfabouh.jgems3d.toolbox.render.screen;

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
import ru.alfabouh.jgems3d.engine.render.opengl.scene.utils.JGemsSceneUtils;
import ru.alfabouh.jgems3d.engine.render.opengl.screen.IScreen;
import ru.alfabouh.jgems3d.engine.render.opengl.screen.timer.GameRenderTimer;
import ru.alfabouh.jgems3d.engine.render.opengl.screen.timer.TimerPool;
import ru.alfabouh.jgems3d.engine.render.opengl.screen.window.IWindow;
import ru.alfabouh.jgems3d.engine.render.opengl.screen.window.Window;
import ru.alfabouh.jgems3d.engine.render.transformation.TransformationUtils;
import ru.alfabouh.jgems3d.engine.system.EngineSystem;
import ru.alfabouh.jgems3d.proxy.exception.JGemsException;
import ru.alfabouh.jgems3d.proxy.logger.SystemLogging;
import ru.alfabouh.jgems3d.proxy.mapsys.toolbox.TBoxMapSys;
import ru.alfabouh.jgems3d.toolbox.ToolBox;
import ru.alfabouh.jgems3d.toolbox.controller.TBoxControllerDispatcher;
import ru.alfabouh.jgems3d.toolbox.render.scene.TBoxScene;
import ru.alfabouh.jgems3d.toolbox.render.scene.dear_imgui.content.EditorContent;
import ru.alfabouh.jgems3d.toolbox.render.scene.dear_imgui.content.LoadingContent;
import ru.alfabouh.jgems3d.toolbox.render.scene.utils.TBoxSceneUtils;
import ru.alfabouh.jgems3d.toolbox.resources.ResourceManager;

public class TBoxScreen implements IScreen {
    private Window tBoxWindow;
    private TBoxControllerDispatcher controllerDispatcher;
    private TBoxScene scene;
    private final TimerPool timerPool;
    private ResourceManager resourceManager;
    private TransformationUtils transformationUtils;

    public TBoxScreen() {
        this.timerPool = new TimerPool();
    }

    public boolean tryToBuildScreen() {
        GLFWErrorCallback.createPrint(System.err).set();
        if (!GLFW.glfwInit()) {
            throw new JGemsException("Error, while initializing GLFW");
        }
        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 3);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 4);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GL20.GL_TRUE);
        GLFW.glfwWindowHint(GLFW.GLFW_DOUBLEBUFFER, GLFW.GLFW_TRUE);
        GLFWVidMode vidMode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());

        this.tBoxWindow = new Window(new Window.WindowProperties(Window.defaultW, Window.defaultH, ToolBox.get().toString()));
        long window = this.getWindow().getDescriptor();
        if (window == MemoryUtil.NULL) {
            throw new JGemsException("Failed to create the GLFW window");
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

    private void loadResourcesAndRenderLoadingScreen() {
        GL30.glClear(GL30.GL_COLOR_BUFFER_BIT | GL30.GL_DEPTH_BUFFER_BIT);
        this.getScene().getDimGuiRenderTBox().setCurrentContentToRender(new LoadingContent());
        for (int i = 0; i < 5; i++) {
            this.getScene().getDimGuiRenderTBox().render(0);
        }
        GLFW.glfwSwapBuffers(this.getWindow().getDescriptor());
        GLFW.glfwPollEvents();
        this.getResourceManager().loadResources();
        TBoxMapSys.INSTANCE.init();
    }

    public void startScreenRenderProcess() {
        SystemLogging.get().getLogManager().log("Starting screen...");
        GL11.glClearColor(0.0f, 0.0f, 0.1f, 1.0f);
        this.getScene().createGUI();
        this.loadResourcesAndRenderLoadingScreen();

        this.getScene().preRender();
        try {
            this.renderLoop();
        } catch (InterruptedException e) {
            throw new JGemsException(e);
        }
        this.getScene().postRender();
        SystemLogging.get().getLogManager().log("Destroying screen...");
        this.getTimerPool().clear();
        GLFW.glfwDestroyWindow(this.getWindow().getDescriptor());
        GLFW.glfwTerminate();
    }

    private void renderLoop() throws InterruptedException {
        GameRenderTimer deltaTimer = this.getTimerPool().createTimer();
        GL30.glClearColor(0.0f, 0.0f, 0.1f, 1.0f);
        while (!ToolBox.get().isShouldBeClosed()) {
            if (GLFW.glfwWindowShouldClose(this.getWindow().getDescriptor())) {
                ToolBox.get().closeTBox();
                break;
            }

            this.getTransformationUtils().updateMatrices();
            this.getTimerPool().update();
            this.getControllerDispatcher().updateController(this.getWindow());
            this.renderGameScene(deltaTimer.getDeltaTime());

            GLFW.glfwSwapBuffers(this.getWindow().getDescriptor());
            GLFW.glfwPollEvents();
        }
    }

    private void renderGameScene(double delta) throws InterruptedException {
        GL30.glEnable(GL30.GL_CULL_FACE);
        GL30.glCullFace(GL30.GL_BACK);
        GL11.glDepthFunc(GL11.GL_LESS);
        GL30.glClear(GL30.GL_COLOR_BUFFER_BIT | GL30.GL_DEPTH_BUFFER_BIT);
        this.getTransformationUtils().updateCamera(this.getScene().getCamera());
        this.getScene().render(delta);
        JGemsSceneUtils.checkGLErrors();
    }

    private void resizeWindow(Vector2i dim) {
        this.normalizeViewPort();
        this.getScene().onWindowResize(dim);
    }

    public void normalizeViewPort() {
        GL30.glViewport(0, 0, this.getDimensions().x, this.getDimensions().y);
    }

    private void setScreenCallbacks() {
        Callbacks.glfwFreeCallbacks(this.getWindow().getDescriptor());
        GLFW.glfwSetWindowSizeCallback(this.getWindow().getDescriptor(), (a, b, c) -> {
            this.resizeWindow(new Vector2i(b, c));
        });
        GLFWErrorCallback glfwErrorCallback = GLFW.glfwSetErrorCallback(null);
        if (glfwErrorCallback != null) {
            glfwErrorCallback.free();
        }
    }

    private void createResourceManager() {
        this.resourceManager = new ResourceManager();
    }

    private void createTransformation() {
        this.transformationUtils = new TransformationUtils(this.getWindow(), TBoxSceneUtils.FOV, TBoxSceneUtils.Z_NEAR, TBoxSceneUtils.Z_FAR);
    }

    private void createObjects(IWindow window) {
        this.controllerDispatcher = new TBoxControllerDispatcher(window);
        this.scene = new TBoxScene(this.getTransformationUtils(), window);
    }

    @Override
    public void buildScreen() {
        SystemLogging.get().getLogManager().log("Building screen...");
        if (this.tryToBuildScreen()) {
            GL.createCapabilities();

            this.createTransformation();
            this.createResourceManager();
            ResourceManager.loadShaders();

            this.setScreenCallbacks();
            this.createObjects(this.getWindow());
            this.normalizeViewPort();

            SystemLogging.get().getLogManager().log("TBoxScreen built successful");
        } else {
            throw new JGemsException("Caught exception, while building screen!!");
        }
    }

    public ResourceManager getResourceManager() {
        return this.resourceManager;
    }

    public TransformationUtils getTransformationUtils() {
        return transformationUtils;
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
        return this.getWindow().getWindowDimensions();
    }

    public Window getWindow() {
        return this.tBoxWindow;
    }
}
