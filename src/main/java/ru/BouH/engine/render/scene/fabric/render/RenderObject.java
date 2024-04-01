package ru.BouH.engine.render.scene.fabric.render;

import org.lwjgl.opengl.GL30;
import ru.BouH.engine.game.resources.ResourceManager;
import ru.BouH.engine.game.resources.assets.models.Model;
import ru.BouH.engine.game.resources.assets.models.formats.Format3D;
import ru.BouH.engine.game.resources.assets.shaders.ShaderManager;
import ru.BouH.engine.physics.entities.states.EntityState;
import ru.BouH.engine.physics.jb_objects.JBulletEntity;
import ru.BouH.engine.physics.world.World;
import ru.BouH.engine.render.scene.Scene;
import ru.BouH.engine.render.scene.SceneRenderBase;
import ru.BouH.engine.render.scene.fabric.render.base.RenderWorldItem;
import ru.BouH.engine.render.scene.objects.IRenderObject;
import ru.BouH.engine.render.scene.objects.items.PhysicsObject;

public class RenderObject extends RenderWorldItem {
    public RenderObject() {
    }

    @Override
    public void onRender(double partialTicks, SceneRenderBase sceneRenderBase, IRenderObject renderItem) {
        PhysicsObject entityObject = (PhysicsObject) renderItem;
        GL30.glClearStencil(0);
        GL30.glClear(GL30.GL_STENCIL_BUFFER_BIT);
        if (entityObject.isHasModel()) {
            GL30.glStencilFunc(GL30.GL_ALWAYS, 1, 0xFF);
            GL30.glStencilMask(0xFF);
            if (World.isItemJBulletObject(entityObject.getWorldItem())) {
                JBulletEntity jBulletEntity = (JBulletEntity) entityObject.getWorldItem();
                if (jBulletEntity.entityState().checkState(EntityState.StateType.IS_SELECTED_BY_PLAYER)) {
                    GL30.glStencilOp(GL30.GL_KEEP, GL30.GL_REPLACE, GL30.GL_REPLACE);
                    GL30.glEnable(GL30.GL_STENCIL_TEST);
                    GL30.glStencilMask(0xFF);
                    Scene.renderSceneObject(entityObject, entityObject.getRenderData().getOverObjectMaterial());
                    GL30.glStencilOp(GL30.GL_KEEP, GL30.GL_KEEP, GL30.GL_KEEP);

                    GL30.glStencilFunc(GL30.GL_NOTEQUAL, 1, 0xFF);
                    GL30.glStencilMask(0x00);
                    ShaderManager shaderManager = ResourceManager.shaderAssets.world_selected;
                    Model<Format3D> model = entityObject.getModel3D();
                    GL30.glDisable(GL30.GL_CULL_FACE);
                    GL30.glEnable(GL30.GL_DEPTH_TEST);
                    shaderManager.bind();
                    shaderManager.getUtils().performProjectionMatrix();
                    shaderManager.getUtils().passViewAndModelMatrices(model);
                    Scene.renderModel(model, GL30.GL_TRIANGLES);
                    shaderManager.unBind();
                    GL30.glEnable(GL30.GL_DEPTH_TEST);
                    GL30.glEnable(GL30.GL_CULL_FACE);
                    GL30.glStencilFunc(GL30.GL_ALWAYS, 1, 0xFF);
                    GL30.glStencilMask(0x00);
                    return;
                }
            }
            Scene.renderSceneObject(entityObject, entityObject.getRenderData().getOverObjectMaterial());
        }
    }
}
