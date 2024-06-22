package ru.alfabouh.jgems3d.toolbox.render.scene.items.renderers;

import org.joml.Vector3d;
import org.lwjgl.opengl.GL30;
import ru.alfabouh.jgems3d.proxy.mapsys.toolbox.table.object.attributes.AttributeIDS;
import ru.alfabouh.jgems3d.toolbox.render.scene.container.MapProperties;
import ru.alfabouh.jgems3d.toolbox.render.scene.items.objects.base.TBoxScene3DObject;
import ru.alfabouh.jgems3d.toolbox.render.scene.utils.TBoxSceneUtils;

public class MarkerObject3DRenderer implements ITBoxObjectRenderer {
    @Override
    public void onRender(MapProperties properties, TBoxScene3DObject tBoxScene3DObject, double deltaTime) {
        tBoxScene3DObject.getRenderData().getShaderManager().bind();
        tBoxScene3DObject.getRenderData().getShaderManager().getUtils().performPerspectiveMatrix();
        tBoxScene3DObject.getRenderData().getShaderManager().getUtils().performViewAndModelMatricesSeparately(TBoxSceneUtils.getMainCameraViewMatrix(), tBoxScene3DObject.getModel());
        tBoxScene3DObject.getRenderData().getShaderManager().performUniform("selected", tBoxScene3DObject.isSelected());
        tBoxScene3DObject.getRenderData().getShaderManager().performUniform("diffuse_color", tBoxScene3DObject.getAttributeContainer().tryGetValueFromAttributeByID(AttributeIDS.COLOR, Vector3d.class));
        TBoxSceneUtils.renderModel(tBoxScene3DObject.getModel(), GL30.GL_TRIANGLES);
        tBoxScene3DObject.getRenderData().getShaderManager().unBind();
    }

    @Override
    public void preRender(TBoxScene3DObject tBoxScene3DObject) {

    }

    @Override
    public void postRender(TBoxScene3DObject tBoxScene3DObject) {

    }
}
