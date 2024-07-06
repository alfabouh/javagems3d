package ru.alfabouh.jgems3d.engine.graphics.opengl.scene.components.groups;

import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.JGemsSceneRender;
import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.components.RenderGroup;
import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.components.base.SceneRenderBase;
import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.objects.IModeledSceneObject;

import java.util.List;

public class WorldDeferredRender extends SceneRenderBase {
    public WorldDeferredRender(JGemsSceneRender sceneRenderConveyor) {
        super(1, sceneRenderConveyor, new RenderGroup("WORLD_DEFERRED"));
    }

    public void onRender(float partialTicks) {
        this.render(partialTicks, this.getSceneWorld().getFilteredEntityList(true));
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
                    entityItem.getModelRenderParams().getShaderManager().bind();
                    entityItem.getModelRenderParams().getShaderManager().getUtils().performPerspectiveMatrix();
                    entityItem.renderFabric().onRender(partialTicks, this, entityItem);
                    entityItem.getModelRenderParams().getShaderManager().unBind();
                }
            }
        }
    }
}