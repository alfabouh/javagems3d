package ru.jgems3d.engine.graphics.opengl.rendering.scene.render_base;

import ru.jgems3d.engine.graphics.opengl.camera.ICamera;
import ru.jgems3d.engine.JGemsHelper;
import ru.jgems3d.engine.graphics.opengl.rendering.scene.JGemsOpenGLRenderer;
import ru.jgems3d.engine.graphics.opengl.world.SceneWorld;

public abstract class SceneRenderBase {
    private final int renderOrder;
    private final RenderGroup renderGroup;
    private final JGemsOpenGLRenderer sceneRender;

    protected SceneRenderBase(int renderOrder, JGemsOpenGLRenderer sceneRender, RenderGroup renderGroup) {
        this.renderOrder = renderOrder;
        this.renderGroup = renderGroup;
        this.sceneRender = sceneRender;
    }

    public SceneWorld getSceneWorld() {
        return this.getSceneRenderer().getSceneData().getSceneWorld();
    }

    public ICamera getCamera() {
        return this.getSceneRenderer().getSceneData().getCamera();
    }

    public abstract void onRender(float partialTicks);

    public void onStartRender() {
        JGemsHelper.getLogger().log("Scene " + this.getRenderGroup().getId() + ": render start!");
    }

    public void onStopRender() {
        JGemsHelper.getLogger().log("Scene " + this.getRenderGroup().getId() + ": render stop!");
    }

    public int getRenderOrder() {
        return this.renderOrder;
    }

    public RenderGroup getRenderGroup() {
        return this.renderGroup;
    }

    public JGemsOpenGLRenderer getSceneRenderer() {
        return this.sceneRender;
    }
}
