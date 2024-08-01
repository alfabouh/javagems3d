package ru.jgems3d.engine.graphics.opengl.rendering.scene;

import ru.jgems3d.engine.graphics.opengl.camera.ICamera;
import ru.jgems3d.engine.JGemsHelper;

public abstract class SceneRenderBase {
    private final int renderOrder;
    private final RenderGroup renderGroup;
    private final SceneData sceneData;

    protected SceneRenderBase(int renderOrder, SceneData sceneData, RenderGroup renderGroup) {
        this.renderOrder = renderOrder;
        this.renderGroup = renderGroup;
        this.sceneData = sceneData;
    }

    public ICamera getCamera() {
        return this.getSceneData().getCamera();
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

    public SceneData getSceneData() {
        return this.sceneData;
    }
}
