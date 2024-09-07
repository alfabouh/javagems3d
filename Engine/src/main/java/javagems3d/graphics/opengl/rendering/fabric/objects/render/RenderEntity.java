/*
 * *
 *  * @author alfabouh
 *  * @since 2024
 *  * @link https://github.com/alfabouh/JavaGems3D
 *  *
 *  * This software is provided 'as-is', without any express or implied warranty.
 *  * In no event will the authors be held liable for any damages arising from the use of this software.
 *
 */

package javagems3d.graphics.opengl.rendering.fabric.objects.render;

import org.lwjgl.opengl.GL30;
import javagems3d.graphics.opengl.rendering.JGemsSceneUtils;
import javagems3d.graphics.opengl.rendering.items.IRenderObject;
import javagems3d.graphics.opengl.rendering.items.objects.AbstractSceneEntity;
import javagems3d.graphics.opengl.rendering.scene.render_base.SceneRenderBase;
import javagems3d.graphics.opengl.rendering.scene.tick.FrameTicking;
import javagems3d.physics.entities.properties.state.EntityState;
import javagems3d.physics.entities.properties.state.IHasEntityState;
import javagems3d.physics.world.basic.WorldItem;
import javagems3d.system.resources.assets.models.Model;
import javagems3d.system.resources.assets.models.formats.Format3D;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import javagems3d.system.resources.manager.JGemsResourceManager;

public class RenderEntity extends RenderWorldItem {
    public RenderEntity() {
    }

    @Override
    public void onRender(FrameTicking frameTicking, SceneRenderBase sceneRenderBase, IRenderObject renderItem) {
        AbstractSceneEntity entityObject = (AbstractSceneEntity) renderItem;
        GL30.glClearStencil(0);
        GL30.glClear(GL30.GL_STENCIL_BUFFER_BIT);
        if (entityObject.hasRender() && entityObject.hasModel()) {
            GL30.glStencilFunc(GL30.GL_ALWAYS, 1, 0xFF);
            GL30.glStencilMask(0xFF);
            WorldItem worldItem = entityObject.getWorldItem();
            if (worldItem instanceof IHasEntityState) {
                IHasEntityState entityState = (IHasEntityState) entityObject.getWorldItem();
                if (entityState.getEntityState().checkState(EntityState.Type.IS_SELECTED_BY_PLAYER)) {
                    GL30.glStencilOp(GL30.GL_KEEP, GL30.GL_REPLACE, GL30.GL_REPLACE);
                    GL30.glEnable(GL30.GL_STENCIL_TEST);
                    GL30.glStencilMask(0xFF);
                    sceneRenderBase.getSceneRenderer().renderModeledSceneObject(entityObject);
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
            sceneRenderBase.getSceneRenderer().renderModeledSceneObject(entityObject);
        }
    }
}
