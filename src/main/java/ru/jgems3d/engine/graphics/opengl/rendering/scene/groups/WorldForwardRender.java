package ru.jgems3d.engine.graphics.opengl.rendering.scene.groups;

import ru.jgems3d.engine.graphics.opengl.rendering.JGemsOpenGLRenderer;
import ru.jgems3d.engine.graphics.opengl.rendering.scene.RenderGroup;
import ru.jgems3d.engine.graphics.opengl.rendering.scene.SceneData;
import ru.jgems3d.engine.graphics.opengl.rendering.scene.SceneRenderBase;
import ru.jgems3d.engine.graphics.opengl.rendering.items.IModeledSceneObject;
import ru.jgems3d.engine.system.resources.assets.shaders.RenderPass;

import java.util.List;

public class WorldForwardRender extends SceneRenderBase {
    public WorldForwardRender(JGemsOpenGLRenderer sceneRender) {
        super(1, sceneRender, new RenderGroup("WORLD_FORWARD"));
    }

    public void onRender(float partialTicks) {
        this.render(partialTicks, this.getSceneWorld().getFilteredEntityList(RenderPass.FORWARD));
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
                    entityItem.getMeshRenderData().getShaderManager().bind();
                    entityItem.getMeshRenderData().getShaderManager().getUtils().performPerspectiveMatrix();
                    entityItem.renderFabric().onRender(partialTicks, this, entityItem);
                    entityItem.getMeshRenderData().getShaderManager().unBind();
                }
            }
        }
    }
}