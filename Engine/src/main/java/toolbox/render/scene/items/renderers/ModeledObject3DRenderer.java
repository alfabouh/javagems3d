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

import javagems3d.graphics.rendering.programs.shaders.unifrom.UniformFunctions;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL46;
import javagems3d.system.resources.assets.shaders.uniform.UniformString;
import javagems3d.temp.map_sys.save.objects.MapProperties;
import toolbox.render.scene.dear_imgui.content.EditorContent;
import toolbox.render.scene.items.objects.base.TBoxAbstractObject;
import toolbox.render.scene.utils.TBoxSceneUtils;

public class ModeledObject3DRenderer implements ITBoxObjectRenderer {
    @Override
    public void onRender(MapProperties properties, TBoxAbstractObject tBoxAbstractObject, float deltaTime) {
        tBoxAbstractObject.getRenderData().getShaderManager().beginShading();
        tBoxAbstractObject.getRenderData().getShaderManager().getUtils().performPerspectiveMatrix();
        tBoxAbstractObject.getRenderData().getShaderManager().getUtils().performViewAndModelMatricesSeparately(TBoxSceneUtils.getMainCameraViewMatrix(), tBoxAbstractObject.getModel());
        tBoxAbstractObject.getRenderData().getShaderManager().performUniform(new UniformString("diffuse_color"),  UniformFunctions.VEC4F(new Vector4f(1.0f)));
        tBoxAbstractObject.getRenderData().getShaderManager().performUniform(new UniformString("use_texturing"),  UniformFunctions.BOOLEAN(true));

        tBoxAbstractObject.getRenderData().getShaderManager().performUniform(new UniformString("selected"),  UniformFunctions.BOOLEAN(tBoxAbstractObject.isSelected()));

        tBoxAbstractObject.getRenderData().getShaderManager().performUniform(new UniformString("showLight"),  UniformFunctions.BOOLEAN(EditorContent.sceneShowLight));
        tBoxAbstractObject.getRenderData().getShaderManager().performUniform(new UniformString("showFog"),  UniformFunctions.BOOLEAN(EditorContent.sceneShowFog && properties.getFogProp().isFogEnabled()));

        tBoxAbstractObject.getRenderData().getShaderManager().performUniform(new UniformString("sunPos"),  UniformFunctions.VEC3F(properties.getSkyProp().getSunPos()));
        tBoxAbstractObject.getRenderData().getShaderManager().performUniform(new UniformString("sunColor"),  UniformFunctions.VEC3F(properties.getSkyProp().getSunColor()));
        tBoxAbstractObject.getRenderData().getShaderManager().performUniform(new UniformString("sunBright"),  UniformFunctions.FLOAT(properties.getSkyProp().getSunBrightness()));

        tBoxAbstractObject.getRenderData().getShaderManager().performUniform(new UniformString("fogDensity"),  UniformFunctions.FLOAT(properties.getFogProp().getFogDensity()));
        tBoxAbstractObject.getRenderData().getShaderManager().performUniform(new UniformString("fogColor"), UniformFunctions.VEC3F(properties.getFogProp().getFogColor()));

        TBoxSceneUtils.renderModelTextured(tBoxAbstractObject.getRenderData().getShaderManager(), tBoxAbstractObject.getModel(), GL46.GL_TRIANGLES);
        tBoxAbstractObject.getRenderData().getShaderManager().endShading();
    }

    @Override
    public void preRender(TBoxAbstractObject tBoxAbstractObject) {

    }

    @Override
    public void postRender(TBoxAbstractObject tBoxAbstractObject) {

    }
}
