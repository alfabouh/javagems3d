package workbench.graphics.scene;

import javagems3d.graphics.camera.base.ICamera;
import javagems3d.graphics.rendering.scene.IScene;
import javagems3d.graphics.rendering.scene.renderer.JGemsOpenGLRenderer;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.screen.ticking.FrameTicking;
import javagems3d.graphics.screen.window.IWindow;
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
        Log.get().info("Starting scene rendering");
        this.getSceneRenderer().onStartRender();
    }

    @SuppressWarnings("all")
    public void renderScene(float frameDeltaTime) throws InterruptedException {
        if (this.getWindow().isWindowActive()) {
            JGemsOpenGLRenderer.UBOShader().beginShading();
            this.updateSceneComponents(new FrameTicking(0.0f, frameDeltaTime));
            this.getSceneRenderer().onRender(new FrameTicking(0.0f, frameDeltaTime));
            JGemsOpenGLRenderer.UBOShader().endShading();
        }
    }

    @SuppressWarnings("all")
    public void updateSceneComponents(final FrameTicking frameTicking) throws InterruptedException {
        this.getWorld().updateWorldObjects(frameTicking);
        this.getWorld().onWorldUpdate();
        this.getCamera().updateCamera(frameTicking.getFrameDeltaTime());
        JGemsTransformManager.INSTANCE.updateCamera(this.getCamera());
    }

    public void postRender() {
        Log.get().info("Stopping scene rendering");
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