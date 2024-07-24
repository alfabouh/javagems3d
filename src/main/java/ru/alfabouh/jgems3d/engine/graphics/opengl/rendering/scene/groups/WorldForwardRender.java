package ru.alfabouh.jgems3d.engine.graphics.opengl.rendering.scene.groups;

import ru.alfabouh.jgems3d.engine.graphics.opengl.rendering.JGemsOpenGLRenderer;
import ru.alfabouh.jgems3d.engine.graphics.opengl.rendering.scene.RenderGroup;
import ru.alfabouh.jgems3d.engine.graphics.opengl.rendering.scene.SceneRenderBase;
import ru.alfabouh.jgems3d.engine.graphics.opengl.rendering.items.IModeledSceneObject;

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