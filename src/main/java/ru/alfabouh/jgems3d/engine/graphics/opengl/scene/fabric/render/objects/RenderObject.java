package ru.alfabouh.jgems3d.engine.graphics.opengl.scene.fabric.render.objects;

import org.lwjgl.opengl.GL30;
import ru.alfabouh.jgems3d.engine.physics.jb_objects.JBulletEntity;
import ru.alfabouh.jgems3d.engine.physics.objects.states.EntityState;
import ru.alfabouh.jgems3d.engine.physics.world.World;
import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.components.base.SceneRenderBase;
import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.fabric.render.base.RenderWorldItem;
import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.objects.IRenderObject;
import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.objects.items.AbstractSceneItemObject;
import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.utils.JGemsSceneUtils;
import ru.alfabouh.jgems3d.engine.system.resources.manager.JGemsResourceManager;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.Model;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.formats.Format3D;
import ru.alfabouh.jgems3d.engine.system.resources.assets.shaders.manager.JGemsShaderManager;

public class RenderObject extends RenderWorldItem {
    public RenderObject() {
    }

    @Override
    public void onRender(float partialTicks, SceneRenderBase sceneRenderBase, IRenderObject renderItem) {
        AbstractSceneItemObject entityObject = (AbstractSceneItemObject) renderItem;
        GL30.glClearStencil(0);
        GL30.glClear(GL30.GL_STENCIL_BUFFER_BIT);
        if (entityObject.hasRender() && entityObject.hasModel()) {
            GL30.glStencilFunc(GL30.GL_ALWAYS, 1, 0xFF);
            GL30.glStencilMask(0xFF);
            if (World.isItemJBulletObject(entityObject.getWorldItem())) {
                JBulletEntity jBulletEntity = (JBulletEntity) entityObject.getWorldItem();
                if (jBulletEntity.entityState().checkState(EntityState.StateType.IS_SELECTED_BY_PLAYER)) {
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
