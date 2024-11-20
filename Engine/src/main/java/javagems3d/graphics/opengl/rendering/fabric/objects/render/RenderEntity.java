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

import org.lwjgl.opengl.GL46;
import javagems3d.graphics.opengl.rendering.JGemsSceneUtils;
import javagems3d.graphics.opengl.rendering.items.IRendered;
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
    public void onRender(FrameTicking frameTicking, SceneRenderBase sceneRenderBase, IRendered renderItem) {
        AbstractSceneEntity entityObject = (AbstractSceneEntity) renderItem;
        GL46.glClearStencil(0);
        GL46.glClear(GL46.GL_STENCIL_BUFFER_BIT);
        if (entityObject.hasRender() && entityObject.hasModel()) {
            sceneRenderBase.getSceneRenderer().renderModeledSceneObject(entityObject);
        }
    }
}
