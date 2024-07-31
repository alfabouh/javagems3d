package ru.jgems3d.engine.graphics.opengl.rendering.scene.groups;

import ru.jgems3d.engine.graphics.opengl.rendering.scene.RenderGroup;
import ru.jgems3d.engine.graphics.opengl.rendering.scene.SceneData;
import ru.jgems3d.engine.graphics.opengl.rendering.scene.SceneRenderBase;
import ru.jgems3d.engine.graphics.opengl.rendering.items.IModeledSceneObject;

import java.util.List;

public class WorldForwardRender extends SceneRenderBase {
    public WorldForwardRender(SceneData sceneData) {
        super(1, sceneData, new RenderGroup("WORLD_FORWARD"));
    }

    public void onRender(float partialTicks) {
        this.render(partialTicks, this.getSceneData().getSceneWorld().getFilteredEntityList(false));
    }

    public void onStartRender() {
        super.onStartRender();
    }

    public void onStopRender() {
        super.onStopRender();
    }

    private void render(float partialTicks, List<IModeledSceneObject> renderObjects) {
        for (IModeledSceneObject entityItem : renderObjects) {
            if (entityItem.hasRender()) {
                if (entityItem.isVisible()) {
                    if (entityItem.getMeshRenderData().getRenderAttributes().isHasTransparency()) {
                        WorldTransparentRender.transparentRenderObjects.add(entityItem);
                        continue;
                    }
                    entityItem.getMeshRenderData().getShaderManager().bind();
                    entityItem.getMeshRenderData().getShaderManager().getUtils().performPerspectiveMatrix();
                    entityItem.renderFabric().onRender(partialTicks, this, entityItem);
                    entityItem.getMeshRenderData().getShaderManager().unBind();
                }
            }
        }
    }
}