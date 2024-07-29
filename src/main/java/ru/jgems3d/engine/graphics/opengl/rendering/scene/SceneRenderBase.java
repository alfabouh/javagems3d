package ru.jgems3d.engine.graphics.opengl.rendering.scene;

import ru.jgems3d.engine.JGems;
import ru.jgems3d.engine.graphics.opengl.rendering.JGemsOpenGLRenderer;
import ru.jgems3d.engine.graphics.opengl.world.SceneWorld;
import ru.jgems3d.engine.graphics.opengl.camera.ICamera;
import ru.jgems3d.engine.JGemsHelper;

public abstract class SceneRenderBase {
    private final int renderPriority;
    private final RenderGroup renderGroup;
    private final JGemsOpenGLRenderer sceneRenderConveyor;

    protected SceneRenderBase(int renderPriority, JGemsOpenGLRenderer sceneRenderConveyor, RenderGroup renderGroup) {
        this.renderPriority = renderPriority;
        this.renderGroup = renderGroup;
        this.sceneRenderConveyor = sceneRenderConveyor;
    }

    public ICamera getCamera() {
        return JGems.get().getScreen().getCamera();
    }

    public abstract void onRender(float partialTicks);

    public void onStartRender() {
        JGemsHelper.getLogger().log("Scene " + this.getRenderGroup().getId() + ": render start!");
    }

    public void onStopRender() {
        JGemsHelper.getLogger().log("Scene " + this.getRenderGroup().getId() + ": render stop!");
    }

    public int getRenderPriority() {
        return this.renderPriority;
    }

    public RenderGroup getRenderGroup() {
        return this.renderGroup;
    }

    public SceneWorld getSceneWorld() {
        return this.sceneRenderConveyor.getShadowScene().getSceneWorld();
    }
}
