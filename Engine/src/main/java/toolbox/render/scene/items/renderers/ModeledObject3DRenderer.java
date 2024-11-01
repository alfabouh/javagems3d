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

package toolbox.render.scene.items.renderers;

import javagems3d.graphics.opengl.rendering.programs.shaders.unifrom.DefaultUniformActions;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL30;
import javagems3d.system.resources.assets.shaders.uniform.UniformString;
import javagems3d.temp.map_sys.save.objects.MapProperties;
import toolbox.render.scene.dear_imgui.content.EditorContent;
import toolbox.render.scene.items.objects.base.TBoxAbstractObject;
import toolbox.render.scene.utils.TBoxSceneUtils;

public class ModeledObject3DRenderer implements ITBoxObjectRenderer {
    @Override
    public void onRender(MapProperties properties, TBoxAbstractObject tBoxAbstractObject, float deltaTime) {
        tBoxAbstractObject.getRenderData().getShaderManager().bind();
        tBoxAbstractObject.getRenderData().getShaderManager().getUtils().performPerspectiveMatrix();
        tBoxAbstractObject.getRenderData().getShaderManager().getUtils().performViewAndModelMatricesSeparately(TBoxSceneUtils.getMainCameraViewMatrix(), tBoxAbstractObject.getModel());
        tBoxAbstractObject.getRenderData().getShaderManager().performUniform(new UniformString("diffuse_color"),  DefaultUniformActions.VEC4F(new Vector4f(1.0f)));
        tBoxAbstractObject.getRenderData().getShaderManager().performUniform(new UniformString("use_texturing"),  DefaultUniformActions.BOOLEAN(true));

        tBoxAbstractObject.getRenderData().getShaderManager().performUniform(new UniformString("selected"),  DefaultUniformActions.BOOLEAN(tBoxAbstractObject.isSelected()));

        tBoxAbstractObject.getRenderData().getShaderManager().performUniform(new UniformString("showLight"),  DefaultUniformActions.BOOLEAN(EditorContent.sceneShowLight));
        tBoxAbstractObject.getRenderData().getShaderManager().performUniform(new UniformString("showFog"),  DefaultUniformActions.BOOLEAN(EditorContent.sceneShowFog && properties.getFogProp().isFogEnabled()));

        tBoxAbstractObject.getRenderData().getShaderManager().performUniform(new UniformString("sunPos"),  DefaultUniformActions.VEC3F(properties.getSkyProp().getSunPos()));
        tBoxAbstractObject.getRenderData().getShaderManager().performUniform(new UniformString("sunColor"),  DefaultUniformActions.VEC3F(properties.getSkyProp().getSunColor()));
        tBoxAbstractObject.getRenderData().getShaderManager().performUniform(new UniformString("sunBright"),  DefaultUniformActions.FLOAT(properties.getSkyProp().getSunBrightness()));

        tBoxAbstractObject.getRenderData().getShaderManager().performUniform(new UniformString("fogDensity"),  DefaultUniformActions.FLOAT(properties.getFogProp().getFogDensity()));
        tBoxAbstractObject.getRenderData().getShaderManager().performUniform(new UniformString("fogColor"), DefaultUniformActions.VEC3F(properties.getFogProp().getFogColor()));

        TBoxSceneUtils.renderModelTextured(tBoxAbstractObject.getRenderData().getShaderManager(), tBoxAbstractObject.getModel(), GL30.GL_TRIANGLES);
        tBoxAbstractObject.getRenderData().getShaderManager().unBind();
    }

    @Override
    public void preRender(TBoxAbstractObject tBoxAbstractObject) {

    }

    @Override
    public void postRender(TBoxAbstractObject tBoxAbstractObject) {

    }
}
