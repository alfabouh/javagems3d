package ru.BouH.engine.render.screen;

import org.joml.Vector2d;
import org.joml.Vector2i;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.*;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import ru.BouH.engine.game.Game;
import ru.BouH.engine.game.controller.ControllerDispatcher;
import ru.BouH.engine.game.exception.GameException;
import ru.BouH.engine.physics.world.timer.PhysicsTimer;
import ru.BouH.engine.proxy.LocalPlayer;
import ru.BouH.engine.render.scene.Scene;
import ru.BouH.engine.render.scene.scene_render.groups.*;
import ru.BouH.engine.render.scene.world.SceneWorld;
import ru.BouH.engine.render.scene.world.camera.ICamera;
import ru.BouH.engine.render.screen.timer.Timer;
import ru.BouH.engine.render.screen.window.Window;
import ru.BouH.engine.render.transformation.TransformationManager;

import java.nio.IntBuffer;

public class Screen {
    public static final int defaultW = 1280;
    public static final int defaultH = 720;
    public static int FPS;
    public static int PHYS1_TPS;
    public static int PHYS2_TPS;
    public static int MSAA_SAMPLES = 4;
    private final Timer timer;
    private ControllerDispatcher controllerDispatcher;
    private Scene scene;
    private Window window;

    public Screen() {
        this.timer = new Timer();
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
        this.fillScene(this.getScene());
        this.setWindowCallbacks();
        this.createControllerDispatcher(this.getWindow());
    }

    private void fillScene(Scene scene) {
        scene.addSceneRenderBase(new WorldRender(scene.getSceneRender()));
        scene.addSceneRenderBase(new WorldTransparentRender(scene.getSceneRender()));
        scene.addSceneRenderBase(new WorldRenderLiquids(scene.getSceneRender()));
        scene.addSceneRenderBase(new SkyRender(scene.getSceneRender()));
        scene.addSceneRenderBase(new GuiRender(scene.getSceneRender()));
        scene.addSceneRenderBase(new DebugRender(scene.getSceneRender()));
    }

    public void buildScreen() {
        if (this.tryToBuildScreen()) {
            GL.createCapabilities();
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
        this.window = new Window(new Window.WindowProperties(Screen.defaultW, Screen.defaultH, "Build " + Game.build));
        long window = this.getWindow().getDescriptor();
        if (window == MemoryUtil.NULL) {
            throw new GameException("Failed to create the GLFW window");
        }
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer width = stack.mallocInt(1);
            IntBuffer height = stack.mallocInt(1);
            GLFW.glfwGetWindowSize(window, width, height);
            GLFWVidMode vidMode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
            if (vidMode != null) {
                int x = (vidMode.width() - width.get(0)) / 2;
                int y = (vidMode.height() - height.get(0)) / 2;
                GLFW.glfwSetWindowPos(window, x, y);
            } else {
                return false;
            }
        }
        GLFW.glfwMakeContextCurrent(window);
        GLFW.glfwSwapInterval(1);
        return true;
    }

    public void hideWindow() {
        GLFW.glfwHideWindow(this.getWindow().getDescriptor());
    }

    public void showWindow() {
        GLFW.glfwShowWindow(this.getWindow().getDescriptor());
        GLFW.glfwFocusWindow(this.getWindow().getDescriptor());
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
        //GL.getCapabilities().GL_ARB_framebuffer_object && GL.getCapabilities().GL_EXT_framebuffer_multisample

        GL11.glEnable(GL13.GL_MULTISAMPLE);
        GLFW.glfwWindowHint(GLFW.GLFW_SAMPLES, 8);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
        GL11.glHint(GL11.GL_POLYGON_SMOOTH_HINT, GL11.GL_NICEST);
    }

    private void updateScreen() {
        GL11.glClearColor(0.0f, 0.0f, 0.1f, 1.0f);
        this.getRenderWorld().onWorldStart();
        this.getScene().preRender();
        if (LocalPlayer.VALID_PL) {
            this.controllerDispatcher.attachControllerTo(ControllerDispatcher.mouseKeyboardController, Game.getGame().getPlayerSP());
        }
        try {
            this.renderLoop();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        this.getScene().postRender();
        this.getRenderWorld().onWorldEnd();
    }

    private void renderLoop() throws InterruptedException {
        this.enableMSAA();
        int fps = 0;
        double lastFPS = Game.glfwTime();
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
        if (this.getControllerDispatcher() != null) {
            this.getControllerDispatcher().updateController(this.getWindow());
            this.getWindow().refreshFocusState();
        }
        GL30.glClear(GL30.GL_COLOR_BUFFER_BIT | GL30.GL_DEPTH_BUFFER_BIT | GL30.GL_STENCIL_BUFFER_BIT);
        GL30.glEnable(GL30.GL_CULL_FACE);
        GL30.glCullFace(GL30.GL_BACK);
        GL11.glDepthFunc(GL11.GL_LESS);
        this.getScene().renderScene(delta);
        Game.getGame().getSoundManager().getSoundListener().updateOrientationAndPosition(TransformationManager.instance.getMainCameraViewMatrix(), this.getCamera().getCamPosition());
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
}
