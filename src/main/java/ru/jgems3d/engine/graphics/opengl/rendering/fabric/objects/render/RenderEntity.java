package ru.jgems3d.engine.graphics.opengl.rendering.fabric.objects.render;

import org.lwjgl.opengl.GL30;
import ru.jgems3d.engine.graphics.opengl.rendering.items.objects.AbstractSceneEntity;
import ru.jgems3d.engine.graphics.opengl.rendering.scene.SceneRenderBase;
import ru.jgems3d.engine.graphics.opengl.rendering.items.IRenderObject;
import ru.jgems3d.engine.graphics.opengl.rendering.utils.JGemsSceneUtils;
import ru.jgems3d.engine.physics.entities.properties.state.EntityState;
import ru.jgems3d.engine.physics.entities.properties.state.IEntityState;
import ru.jgems3d.engine.physics.world.basic.WorldItem;
import ru.jgems3d.engine.system.resources.assets.models.Model;
import ru.jgems3d.engine.system.resources.assets.models.formats.Format3D;
import ru.jgems3d.engine.system.resources.assets.shaders.manager.JGemsShaderManager;
import ru.jgems3d.engine.system.resources.manager.JGemsResourceManager;

public class RenderEntity extends RenderWorldItem {
    public RenderEntity() {
    }

    @Override
    public void onRender(float partialTicks, SceneRenderBase sceneRenderBase, IRenderObject renderItem) {
        AbstractSceneEntity entityObject = (AbstractSceneEntity) renderItem;
        GL30.glClearStencil(0);
        GL30.glClear(GL30.GL_STENCIL_BUFFER_BIT);
        if (entityObject.hasRender() && entityObject.hasModel()) {
            GL30.glStencilFunc(GL30.GL_ALWAYS, 1, 0xFF);
            GL30.glStencilMask(0xFF);
            WorldItem worldItem = entityObject.getWorldItem();
            if (worldItem instanceof IEntityState) {
                IEntityState entityState = (IEntityState) entityObject.getWorldItem();
                if (entityState.getEntityState().checkState(EntityState.Type.IS_SELECTED_BY_PLAYER)) {
                    GL30.glStencilOp(GL30.GL_KEEP, GL30.GL_REPLACE, GL30.GL_REPLACE);
                    GL30.glEnable(GL30.GL_STENCIL_TEST);
                    GL30.glStencilMask(0xFF);
                    JGemsSceneUtils.renderSceneObject(entityObject, entityObject.getRenderData().getOverObjectMaterial());
                    GL30.glStencilOp(GL30.GL_KEEP, GL30.GL_KEEP, GL30.GL_KEEP);

                    GL30.glStencilFunc(GL30.GL_NOTEQUAL, 1, 0xFF);
                    GL30.glStencilMask(0x00);
                    JGemsShaderManager shaderManager = JGemsResourceManager.globalShaderAssets.world_selected_gbuffer;
                    Model<Format3D> model = entityObject.getModel();
                    GL30.glDisable(GL30.GL_CULL_FACE);
                    GL30.glEnable(GL30.GL_DEPTH_TEST);
                    shaderManager.bind();
                    shaderManager.getUtils().performPerspectiveMatrix();
                    shaderManager.getUtils().performViewAndModelMatricesSeparately(model);
                    JGemsSceneUtils.renderModel(model, GL30.GL_TRIANGLES);
                    shaderManager.unBind();
                    GL30.glEnable(GL30.GL_DEPTH_TEST);
                    GL30.glEnable(GL30.GL_CULL_FACE);
                    GL30.glStencilFunc(GL30.GL_ALWAYS, 1, 0xFF);
                    GL30.glStencilMask(0x00);
                    return;
                }
            }
            JGemsSceneUtils.renderSceneObject(entityObject, entityObject.getRenderData().getOverObjectMaterial());
        }
    }
}