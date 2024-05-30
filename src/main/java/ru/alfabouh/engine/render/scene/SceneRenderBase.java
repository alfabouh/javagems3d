package ru.alfabouh.engine.render.scene;

import ru.alfabouh.engine.JGems;
import ru.alfabouh.engine.render.scene.components.RenderGroup;
import ru.alfabouh.engine.render.scene.world.SceneWorld;
import ru.alfabouh.engine.render.scene.world.camera.ICamera;

public abstract class SceneRenderBase {
    private final int renderPriority;
    private final RenderGroup renderGroup;
    private final SceneRender sceneRenderConveyor;

    protected SceneRenderBase(int renderPriority, SceneRender sceneRenderConveyor, RenderGroup renderGroup) {
        this.renderPriority = renderPriority;
        this.renderGroup = renderGroup;
        this.sceneRenderConveyor = sceneRenderConveyor;
        JGems.get().getLogManager().log("Scene \"" + renderGroup.getId() + "\" init");
    }

    public ICamera getCamera() {
        return JGems.get().getScreen().getCamera();
    }

    public abstract void onRender(double partialTicks);

    public void onStartRender() {
        JGems.get().getLogManager().log("Scene " + this.getRenderGroup().getId() + ": render start!");
    }

    public void onStopRender() {
        JGems.get().getLogManager().log("Scene " + this.getRenderGroup().getId() + ": render stop!");
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
