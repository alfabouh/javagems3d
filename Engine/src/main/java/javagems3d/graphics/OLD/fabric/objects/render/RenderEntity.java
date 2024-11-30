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

package javagems3d.graphics.OLD.fabric.objects.render;

import org.lwjgl.opengl.GL46;
import javagems3d.graphics.objects.IRendered;
import javagems3d.graphics.objects.entities.AbstractSceneEntity;
import javagems3d.graphics.OLD.render_base.SceneRenderBase;
import javagems3d.graphics.screen.ticking.FrameTicking;

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
