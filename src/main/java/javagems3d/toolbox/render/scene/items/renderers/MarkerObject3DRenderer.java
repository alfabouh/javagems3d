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

package javagems3d.toolbox.render.scene.items.renderers;

import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL30;
import javagems3d.engine.system.resources.assets.shaders.UniformString;
import javagems3d.toolbox.map_sys.save.objects.MapProperties;
import javagems3d.toolbox.map_sys.save.objects.object_attributes.AttributeID;
import javagems3d.toolbox.render.scene.items.objects.base.TBoxAbstractObject;
import javagems3d.toolbox.render.scene.utils.TBoxSceneUtils;

public class MarkerObject3DRenderer implements ITBoxObjectRenderer {
    @Override
    public void onRender(MapProperties properties, TBoxAbstractObject tBoxAbstractObject, float deltaTime) {
        tBoxAbstractObject.getRenderData().getShaderManager().bind();
        tBoxAbstractObject.getRenderData().getShaderManager().getUtils().performPerspectiveMatrix();
        tBoxAbstractObject.getRenderData().getShaderManager().getUtils().performViewAndModelMatricesSeparately(TBoxSceneUtils.getMainCameraViewMatrix(), tBoxAbstractObject.getModel());
        tBoxAbstractObject.getRenderData().getShaderManager().performUniform(new UniformString("use_texturing"), false);
        tBoxAbstractObject.getRenderData().getShaderManager().performUniform(new UniformString("selected"), tBoxAbstractObject.isSelected());
        Vector3f color = tBoxAbstractObject.getAttributeContainer().getValueFromAttributeByID(AttributeID.COLOR, Vector3f.class);
        if (color == null) {
            color = new Vector3f(1.0f);
        }
        tBoxAbstractObject.getRenderData().getShaderManager().performUniform(new UniformString("diffuse_color"), new Vector4f(color, 1.0f));
        TBoxSceneUtils.renderModel(tBoxAbstractObject.getModel(), GL30.GL_TRIANGLES);
        tBoxAbstractObject.getRenderData().getShaderManager().unBind();
    }

    @Override
    public void preRender(TBoxAbstractObject tBoxAbstractObject) {

    }

    @Override
    public void postRender(TBoxAbstractObject tBoxAbstractObject) {

    }
}
