package ru.jgems3d.toolbox.render.scene.items.renderers;

import org.joml.Vector3f;
import org.lwjgl.opengl.GL30;
import ru.jgems3d.engine.system.resources.assets.shaders.UniformString;
import ru.jgems3d.toolbox.map_sys.save.objects.MapProperties;
import ru.jgems3d.toolbox.render.scene.dear_imgui.content.EditorContent;
import ru.jgems3d.toolbox.render.scene.items.objects.base.TBoxScene3DObject;
import ru.jgems3d.toolbox.render.scene.utils.TBoxSceneUtils;

public class ModeledObject3DRenderer implements ITBoxObjectRenderer {
    @Override
    public void onRender(MapProperties properties, TBoxScene3DObject tBoxScene3DObject, float deltaTime) {
        tBoxScene3DObject.getRenderData().getShaderManager().bind();
        tBoxScene3DObject.getRenderData().getShaderManager().getUtils().performPerspectiveMatrix();
        tBoxScene3DObject.getRenderData().getShaderManager().getUtils().performViewAndModelMatricesSeparately(TBoxSceneUtils.getMainCameraViewMatrix(), tBoxScene3DObject.getModel());
        tBoxScene3DObject.getRenderData().getShaderManager().performUniform(new UniformString("diffuse_color"), new Vector3f(1.0f));
        tBoxScene3DObject.getRenderData().getShaderManager().performUniform(new UniformString("use_texturing"), true);

        tBoxScene3DObject.getRenderData().getShaderManager().performUniform(new UniformString("selected"), tBoxScene3DObject.isSelected());

        tBoxScene3DObject.getRenderData().getShaderManager().performUniform(new UniformString("showLight"), EditorContent.sceneShowLight);
        tBoxScene3DObject.getRenderData().getShaderManager().performUniform(new UniformString("showFog"), EditorContent.sceneShowFog && properties.getFogProp().isFogEnabled());

        tBoxScene3DObject.getRenderData().getShaderManager().performUniform(new UniformString("sunPos"), properties.getSkyProp().getSunPos());
        tBoxScene3DObject.getRenderData().getShaderManager().performUniform(new UniformString("sunColor"), properties.getSkyProp().getSunColor());
        tBoxScene3DObject.getRenderData().getShaderManager().performUniform(new UniformString("sunBright"), properties.getSkyProp().getSunBrightness());

        tBoxScene3DObject.getRenderData().getShaderManager().performUniform(new UniformString("fogDensity"), properties.getFogProp().getFogDensity());
        tBoxScene3DObject.getRenderData().getShaderManager().performUniform(new UniformString("fogColor"), properties.getFogProp().getFogColor());

        TBoxSceneUtils.renderModelTextured(tBoxScene3DObject.getRenderData().getShaderManager(), tBoxScene3DObject.getModel(), GL30.GL_TRIANGLES);
        tBoxScene3DObject.getRenderData().getShaderManager().unBind();
    }

    @Override
    public void preRender(TBoxScene3DObject tBoxScene3DObject) {

    }

    @Override
    public void postRender(TBoxScene3DObject tBoxScene3DObject) {

    }
}
