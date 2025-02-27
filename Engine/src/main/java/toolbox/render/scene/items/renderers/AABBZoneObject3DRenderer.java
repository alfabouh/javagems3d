package toolbox.render.scene.items.renderers;

import javagems3d.graphics.rendering.programs.shaders.unifrom.UniformFunctions;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL46;
import javagems3d.system.resources.assets.shaders.uniform.UniformString;
import javagems3d.temp.map_sys.save.objects.MapProperties;
import javagems3d.temp.map_sys.save.objects.object_attributes.AttributeID;
import toolbox.render.scene.dear_imgui.content.EditorContent;
import toolbox.render.scene.items.objects.base.TBoxAbstractObject;
import toolbox.render.scene.utils.TBoxSceneUtils;

public class AABBZoneObject3DRenderer implements ITBoxObjectRenderer {
    @Override
    public void onRender(MapProperties properties, TBoxAbstractObject tBoxAbstractObject, float deltaTime) {
        tBoxAbstractObject.getRenderData().getShaderManager().beginShading();
       //tBoxAbstractObject.getRenderData().getShaderManager().performPerspectiveMatrix(new UniformString("projection_matrix"), JGemsTransformManager.INSTANCE.getPerspectiveMatrix());
       //tBoxAbstractObject.getRenderData().getShaderManager().performViewAndModelMatricesSeparately(TBoxSceneUtils.getMainCameraViewMatrix(), tBoxAbstractObject.getModel());
        tBoxAbstractObject.getRenderData().getShaderManager().performUniform(new UniformString("selected"), UniformFunctions.BOOLEAN(tBoxAbstractObject.isSelected()));
        Vector3f color = tBoxAbstractObject.getAttributeContainer().getValueFromAttributeByID(AttributeID.COLOR, Vector3f.class);
        if (color == null) {
            color = new Vector3f(1.0f);
        }

        tBoxAbstractObject.getRenderData().getShaderManager().performUniform(new UniformString("showFog"), UniformFunctions.BOOLEAN(EditorContent.sceneShowFog && properties.getFogProp().isFogEnabled()));
        tBoxAbstractObject.getRenderData().getShaderManager().performUniform(new UniformString("sunBright"), UniformFunctions.FLOAT(properties.getSkyProp().getSunBrightness()));
        tBoxAbstractObject.getRenderData().getShaderManager().performUniform(new UniformString("fogDensity"), UniformFunctions.FLOAT(properties.getFogProp().getFogDensity()));
        tBoxAbstractObject.getRenderData().getShaderManager().performUniform(new UniformString("fogColor"), UniformFunctions.VEC3F(properties.getFogProp().getFogColor()));

        tBoxAbstractObject.getRenderData().getShaderManager().performUniform(new UniformString("color"), UniformFunctions.VEC4F(new Vector4f(color, 1.0f)));
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
