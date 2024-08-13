package ru.jgems3d.engine.graphics.opengl.rendering.scene.render_base.groups.forward;

import ru.jgems3d.engine.graphics.opengl.rendering.scene.JGemsOpenGLRenderer;
import ru.jgems3d.engine.graphics.opengl.rendering.scene.render_base.RenderGroup;
import ru.jgems3d.engine.graphics.opengl.rendering.scene.render_base.SceneRenderBase;
import ru.jgems3d.engine.graphics.opengl.rendering.items.IModeledSceneObject;
import ru.jgems3d.engine.graphics.opengl.rendering.scene.tick.FrameTicking;
import ru.jgems3d.engine.system.resources.assets.shaders.RenderPass;

import java.util.Set;

public class WorldForwardRender extends SceneRenderBase {
    public WorldForwardRender(JGemsOpenGLRenderer sceneRender) {
        super(1, sceneRender, new RenderGroup("WORLD_FORWARD"));
    }

    public void onRender(FrameTicking frameTicking) {
        this.render(frameTicking, this.getSceneWorld().getFilteredEntitySet(RenderPass.FORWARD));
    }

    public void onStartRender() {
        super.onStartRender();
    }

    public void onStopRender() {
        super.onStopRender();
    }

    private void render(FrameTicking frameTicking, Set<IModeledSceneObject> renderObjects) {
        for (IModeledSceneObject entityItem : renderObjects) {
            if (entityItem.hasRender()) {
                if (entityItem.isVisible()) {
                    entityItem.getMeshRenderData().getShaderManager().bind();
                    entityItem.getMeshRenderData().getShaderManager().getUtils().performPerspectiveMatrix();
                    entityItem.renderFabric().onPreRender(entityItem);
                    entityItem.renderFabric().onRender(frameTicking, this, entityItem);
                    entityItem.renderFabric().onPostRender(entityItem);
                    entityItem.getMeshRenderData().getShaderManager().unBind();
                }
            }
        }
    }
}