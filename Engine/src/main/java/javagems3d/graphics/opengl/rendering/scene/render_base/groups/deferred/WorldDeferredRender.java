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

package javagems3d.graphics.opengl.rendering.scene.render_base.groups.deferred;

import javagems3d.graphics.opengl.rendering.items.IModeledSceneObject;
import javagems3d.graphics.opengl.rendering.scene.JGemsOpenGLRenderer;
import javagems3d.graphics.opengl.rendering.scene.render_base.RenderGroup;
import javagems3d.graphics.opengl.rendering.scene.render_base.SceneRenderBase;
import javagems3d.graphics.opengl.rendering.scene.tick.FrameTicking;
import javagems3d.system.resources.assets.shaders.base.RenderPass;

import java.util.Set;

public class WorldDeferredRender extends SceneRenderBase {
    public WorldDeferredRender(JGemsOpenGLRenderer sceneRender) {
        super(1, sceneRender, new RenderGroup("WORLD_DEFERRED"));
    }

    public void onRender(FrameTicking frameTicking) {
        this.render(frameTicking, this.getSceneWorld().getFilteredEntitySet(RenderPass.DEFERRED));
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