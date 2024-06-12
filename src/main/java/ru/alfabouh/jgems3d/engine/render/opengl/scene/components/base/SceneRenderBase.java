package ru.alfabouh.jgems3d.engine.render.opengl.scene.components.base;

import ru.alfabouh.jgems3d.engine.JGems;
import ru.alfabouh.jgems3d.engine.render.opengl.scene.world.SceneWorld;
import ru.alfabouh.jgems3d.engine.render.opengl.scene.world.camera.ICamera;
import ru.alfabouh.jgems3d.engine.render.opengl.scene.JGemsSceneRender;
import ru.alfabouh.jgems3d.engine.render.opengl.scene.components.RenderGroup;
import ru.alfabouh.jgems3d.proxy.logger.SystemLogging;

public abstract class SceneRenderBase {
    private final int renderPriority;
    private final RenderGroup renderGroup;
    private final JGemsSceneRender sceneRenderConveyor;

    protected SceneRenderBase(int renderPriority, JGemsSceneRender sceneRenderConveyor, RenderGroup renderGroup) {
        this.renderPriority = renderPriority;
        this.renderGroup = renderGroup;
        this.sceneRenderConveyor = sceneRenderConveyor;
        SystemLogging.get().getLogManager().log("Scene \"" + renderGroup.getId() + "\" init");
    }

    public ICamera getCamera() {
        return JGems.get().getScreen().getCamera();
    }

    public abstract void onRender(double partialTicks);

    public void onStartRender() {
        SystemLogging.get().getLogManager().log("Scene " + this.getRenderGroup().getId() + ": render start!");
    }

    public void onStopRender() {
        SystemLogging.get().getLogManager().log("Scene " + this.getRenderGroup().getId() + ": render stop!");
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
