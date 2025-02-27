package toolbox.render.scene.items.renderers;

import javagems3d.graphics.rendering.programs.shaders.unifrom.UniformFunctions;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL46;
import javagems3d.system.resources.assets.shaders.uniform.UniformString;
import javagems3d.temp.map_sys.save.objects.MapProperties;
import javagems3d.temp.map_sys.save.objects.object_attributes.AttributeID;
import toolbox.render.scene.items.objects.base.TBoxAbstractObject;
import toolbox.render.scene.utils.TBoxSceneUtils;

public class MarkerObject3DRenderer implements ITBoxObjectRenderer {
    @Override
    public void onRender(MapProperties properties, TBoxAbstractObject tBoxAbstractObject, float deltaTime) {
        tBoxAbstractObject.getRenderData().getShaderManager().beginShading();
     //   tBoxAbstractObject.getRenderData().getShaderManager().performPerspectiveMatrix(new UniformString("projection_matrix"), JGemsTransformManager.INSTANCE.getPerspectiveMatrix());
     //   tBoxAbstractObject.getRenderData().getShaderManager().performViewAndModelMatricesSeparately(TBoxSceneUtils.getMainCameraViewMatrix(), tBoxAbstractObject.getModel());
        tBoxAbstractObject.getRenderData().getShaderManager().performUniform(new UniformString("use_texturing"), UniformFunctions.BOOLEAN(false));
        tBoxAbstractObject.getRenderData().getShaderManager().performUniform(new UniformString("selected"), UniformFunctions.BOOLEAN(tBoxAbstractObject.isSelected()));
        Vector3f color = tBoxAbstractObject.getAttributeContainer().getValueFromAttributeByID(AttributeID.COLOR, Vector3f.class);
        if (color == null) {
            color = new Vector3f(1.0f);
        }
        tBoxAbstractObject.getRenderData().getShaderManager().performUniform(new UniformString("diffuse_color"), UniformFunctions.VEC4F(new Vector4f(color, 1.0f)));
        TBoxSceneUtils.renderModel(tBoxAbstractObject.getModel(), GL46.GL_TRIANGLES);
        tBoxAbstractObject.getRenderData().getShaderManager().endShading();
    }

    @Override
    public void preRender(TBoxAbstractObject tBoxAbstractObject) {

    }

    @Override
    public void postRender(TBoxAbstractObject tBoxAbstractObject) {

    }
}
