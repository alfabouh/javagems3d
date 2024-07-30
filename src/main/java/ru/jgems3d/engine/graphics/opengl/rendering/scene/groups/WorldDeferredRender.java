package ru.jgems3d.engine.graphics.opengl.rendering.scene.groups;

import ru.jgems3d.engine.graphics.opengl.rendering.items.IModeledSceneObjectKeeper;
import ru.jgems3d.engine.graphics.opengl.rendering.scene.RenderGroup;
import ru.jgems3d.engine.graphics.opengl.rendering.scene.SceneData;
import ru.jgems3d.engine.graphics.opengl.rendering.scene.SceneRenderBase;

import java.util.List;

public class WorldDeferredRender extends SceneRenderBase {
    public WorldDeferredRender(SceneData sceneData) {
        super(1, sceneData, new RenderGroup("WORLD_DEFERRED"));
    }

    public void onRender(float partialTicks) {
        this.render(partialTicks, this.getSceneData().getSceneWorld().getFilteredEntityList(true));
    }

    public void onStartRender() {
        super.onStartRender();
    }

    public void onStopRender() {
        super.onStopRender();
    }

    private void render(float partialTicks, List<IModeledSceneObjectKeeper> renderObjects) {
        for (IModeledSceneObjectKeeper entityItem : renderObjects) {
            if (entityItem.hasRender()) {
                if (entityItem.isVisible()) {
                    entityItem.getMeshRenderData().getShaderManager().bind();
                    entityItem.getMeshRenderData().getShaderManager().getUtils().performPerspectiveMatrix();
                    entityItem.renderFabric().onRender(partialTicks, this, entityItem);
                    entityItem.getMeshRenderData().getShaderManager().unBind();
                }
            }
        }
    }
}