package ru.alfabouh.jgems3d.engine.render.opengl.scene.components.groups;

import ru.alfabouh.jgems3d.engine.render.opengl.scene.JGemsSceneRender;
import ru.alfabouh.jgems3d.engine.render.opengl.scene.objects.IModeledSceneObject;
import ru.alfabouh.jgems3d.engine.render.opengl.scene.components.base.SceneRenderBase;
import ru.alfabouh.jgems3d.engine.render.opengl.scene.components.RenderGroup;

import java.util.List;

public class WorldForwardRender extends SceneRenderBase {
    public WorldForwardRender(JGemsSceneRender sceneRenderConveyor) {
        super(1, sceneRenderConveyor, new RenderGroup("WORLD_FORWARD"));
    }

    public void onRender(double partialTicks) {
        this.render(partialTicks, this.getSceneWorld().getFilteredEntityList(JGemsSceneRender.RenderPass.FORWARD));
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
                    entityItem.getModelRenderParams().getShaderManager().getUtils().performPerspectiveMatrix();
                    entityItem.renderFabric().onRender(partialTicks, this, entityItem);
                    entityItem.getModelRenderParams().getShaderManager().unBind();
                }
            }
        }
    }
}