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

package javagems3d.graphics.OLD.render_base.groups.forward;

import javagems3d.graphics.objects.AbstractSceneObject;
import javagems3d.graphics.OLD.JGemsOpenGLRendererOLD;
import javagems3d.graphics.OLD.render_base.RenderGroup;
import javagems3d.graphics.OLD.render_base.SceneRenderBase;
import javagems3d.graphics.screen.ticking.FrameTicking;

import java.util.Set;

public class WorldForwardRender extends SceneRenderBase {
    public WorldForwardRender(JGemsOpenGLRendererOLD sceneRender) {
        super(4, sceneRender, new RenderGroup("WORLD_FORWARD"));
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

    private void render(FrameTicking frameTicking, Set<AbstractSceneObject> renderObjects) {
        for (AbstractSceneObject entityItem : renderObjects) {
            if (entityItem.hasRender()) {
                if (entityItem.isVisible()) {
                    entityItem.getObjectRenderConfiguration().getModelRenderShader().beginShading();
                    entityItem.getObjectRenderConfiguration().getModelRenderShader().getUtils().performPerspectiveMatrix();
                    entityItem.getRenderFabric().onPreRender(entityItem);
                    entityItem.getRenderFabric().onRender(frameTicking, this, entityItem);
                    entityItem.getRenderFabric().onPostRender(entityItem);
                    entityItem.getObjectRenderConfiguration().getModelRenderShader().endShading();
                }
            }
        }
    }
}