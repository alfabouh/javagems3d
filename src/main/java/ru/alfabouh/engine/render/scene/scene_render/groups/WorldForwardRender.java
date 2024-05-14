package ru.alfabouh.engine.render.scene.scene_render.groups;

import ru.alfabouh.engine.render.scene.SceneRender;
import ru.alfabouh.engine.render.scene.SceneRenderBase;
import ru.alfabouh.engine.render.scene.objects.IModeledSceneObject;
import ru.alfabouh.engine.render.scene.scene_render.RenderGroup;

import java.util.List;

public class WorldForwardRender extends SceneRenderBase {
    public WorldForwardRender(SceneRender sceneRenderConveyor) {
        super(1, sceneRenderConveyor, new RenderGroup("WORLD_FORWARD"));
    }

    public void onRender(double partialTicks) {
        this.render(partialTicks, this.getSceneWorld().getFilteredEntityList(SceneRender.RenderPass.FORWARD));
    }

    public void onStartRender() {
        super.onStartRender();
    }

    public void onStopRender() {
        super.onStopRender();
    }

    private void render(double partialTicks, List<IModeledSceneObject> renderObjects) {
        for (IModeledSceneObject entityItem : renderObjects) {
            if (entityItem.hasRender()) {
                if (entityItem.isVisible()) {
                    if (entityItem.getModelRenderParams().isHasTransparency()) {
                        WorldTransparentRender.transparentRenderObjects.add(entityItem);
                        continue;
                    }
                    entityItem.getModelRenderParams().getShaderManager().bind();
                    entityItem.getModelRenderParams().getShaderManager().getUtils().performProjectionMatrix();
                    entityItem.renderFabric().onRender(partialTicks, this, entityItem);
                    entityItem.getModelRenderParams().getShaderManager().unBind();
                }
            }
        }
    }
}