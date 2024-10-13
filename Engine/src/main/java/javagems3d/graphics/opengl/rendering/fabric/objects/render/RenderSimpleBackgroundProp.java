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

import javagems3d.JGemsHelper;
import javagems3d.graphics.opengl.camera.Camera;
import javagems3d.graphics.opengl.camera.FreeCamera;
import javagems3d.graphics.opengl.camera.ICamera;
import javagems3d.graphics.opengl.environment.skybox.SkyBox;
import javagems3d.graphics.opengl.rendering.items.IRenderObject;
import javagems3d.graphics.opengl.rendering.items.props.SceneProp;
import javagems3d.graphics.opengl.rendering.scene.render_base.SceneRenderBase;
import javagems3d.graphics.opengl.rendering.scene.tick.FrameTicking;

public class RenderSimpleBackgroundProp extends RenderWorldItem {
    private final SkyBox.Background background;

    public RenderSimpleBackgroundProp(SkyBox.Background background) {
        this.background = background;
    }

    @Override
    public void onRender(FrameTicking frameTicking, SceneRenderBase sceneRenderBase, IRenderObject renderItem) {
        SceneProp sceneObject = (SceneProp) renderItem;
        sceneObject.getMeshRenderData().getShaderManager().bind();
        sceneObject.getMeshRenderData().getShaderManager().getUtils().performPerspectiveMatrix();
        sceneRenderBase.getSceneRenderer().renderModeledSceneObject(sceneObject, this.background.getScaledCameraBackground());
        sceneObject.getMeshRenderData().getShaderManager().unBind();
    }
}
