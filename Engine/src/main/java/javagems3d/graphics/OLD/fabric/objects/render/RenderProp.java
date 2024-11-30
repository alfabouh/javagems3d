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

import javagems3d.graphics.objects.IRendered;
import javagems3d.graphics.objects.props.SceneProp;
import javagems3d.graphics.OLD.render_base.SceneRenderBase;
import javagems3d.graphics.screen.ticking.FrameTicking;

public class RenderProp extends RenderWorldItem {
    public RenderProp() {
    }

    @Override
    public void onRender(FrameTicking frameTicking, SceneRenderBase sceneRenderBase, IRendered renderItem) {
        SceneProp sceneObject = (SceneProp) renderItem;
        sceneObject.getObjectRenderConfiguration().getModelRenderShader().beginShading();
        sceneObject.getObjectRenderConfiguration().getModelRenderShader().getUtils().performPerspectiveMatrix();
        sceneRenderBase.getSceneRenderer().renderModeledSceneObject(sceneObject);
        sceneObject.getObjectRenderConfiguration().getModelRenderShader().endShading();
    }
}
