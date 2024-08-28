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

package ru.jgems3d.engine.graphics.opengl.rendering.fabric.objects.render;

import org.lwjgl.opengl.GL30;
import ru.jgems3d.engine.graphics.opengl.rendering.scene.render_base.SceneRenderBase;
import ru.jgems3d.engine.graphics.opengl.rendering.items.IRenderObject;
import ru.jgems3d.engine.graphics.opengl.rendering.items.objects.AbstractSceneEntity;
import ru.jgems3d.engine.graphics.opengl.rendering.JGemsSceneUtils;
import ru.jgems3d.engine.graphics.opengl.rendering.scene.tick.FrameTicking;

public class RenderEntity2D3D extends RenderWorldItem {
    public RenderEntity2D3D() {
    }

    @Override
    public void onRender(FrameTicking frameTicking, SceneRenderBase sceneRenderBase, IRenderObject renderItem) {
        //GL30.glDisable(GL30.GL_DEPTH_TEST);
        AbstractSceneEntity entityObject = (AbstractSceneEntity) renderItem;
        if (entityObject.hasRender() && entityObject.hasModel()) {
            entityObject.getModel().getFormat().setOrientedToView(true);
            JGemsSceneUtils.renderSceneObject(entityObject);
        }
        //GL30.glEnable(GL30.GL_DEPTH_TEST);
    }
}
