package ru.alfabouh.engine.render.scene.scene_render.groups;

import ru.alfabouh.engine.render.scene.SceneRender;
import ru.alfabouh.engine.render.scene.SceneRenderBase;
import ru.alfabouh.engine.render.scene.objects.IModeledSceneObject;
import ru.alfabouh.engine.render.scene.scene_render.RenderGroup;

import java.util.List;

public class WorldDeferredRender extends SceneRenderBase {
    public WorldDeferredRender(SceneRender sceneRenderConveyor) {
        super(1, sceneRenderConveyor, new RenderGroup("WORLD_DEFERRED"));
    }

    public void onRender(double partialTicks) {
        this.render(partialTicks, this.getSceneWorld().getFilteredEntityList(SceneRender.RenderPass.DEFERRED));
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
                    entityItem.getModelRenderParams().getShaderManager().bind();
                    entityItem.getModelRenderParams().getShaderManager().getUtils().performProjectionMatrix();
                    entityItem.renderFabric().onRender(partialTicks, this, entityItem);
                    entityItem.getModelRenderParams().getShaderManager().unBind();
                }
            }
        }
    }
}