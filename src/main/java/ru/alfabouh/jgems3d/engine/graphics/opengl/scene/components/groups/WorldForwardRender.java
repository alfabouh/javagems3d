package ru.alfabouh.jgems3d.engine.graphics.opengl.scene.components.groups;

import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.JGemsOpenGLRenderer;
import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.components.RenderGroup;
import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.components.base.SceneRenderBase;
import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.objects.IModeledSceneObject;

import java.util.List;

public class WorldForwardRender extends SceneRenderBase {
    public WorldForwardRender(JGemsOpenGLRenderer sceneRenderConveyor) {
        super(1, sceneRenderConveyor, new RenderGroup("WORLD_FORWARD"));
    }

    public void onRender(float partialTicks) {
        this.render(partialTicks, this.getSceneWorld().getFilteredEntityList(false));
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