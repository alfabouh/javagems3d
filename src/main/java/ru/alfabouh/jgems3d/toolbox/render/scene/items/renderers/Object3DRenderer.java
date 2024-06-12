package ru.alfabouh.jgems3d.toolbox.render.scene.items.renderers;

import org.lwjgl.opengl.GL30;
import ru.alfabouh.jgems3d.toolbox.render.scene.items.objects.ITBoxScene3DObject;
import ru.alfabouh.jgems3d.toolbox.render.scene.utils.TBoxSceneUtils;

public class Object3DRenderer implements ITBoxObjectRenderer {
    @Override
    public void onRender(ITBoxScene3DObject itBoxScene3DObject, double deltaTime) {
        itBoxScene3DObject.getRenderData().getShaderManager().bind();
        itBoxScene3DObject.getRenderData().getShaderManager().getUtils().performPerspectiveMatrix();
        itBoxScene3DObject.getRenderData().getShaderManager().getUtils().performModel3DViewMatrix(itBoxScene3DObject.getModel(), TBoxSceneUtils.getMainCameraViewMatrix());
        TBoxSceneUtils.renderModelTextured(itBoxScene3DObject.getRenderData().getShaderManager(), itBoxScene3DObject.getModel(), GL30.GL_TRIANGLES);
        itBoxScene3DObject.getRenderData().getShaderManager().unBind();
    }

    @Override
    public void preRender(ITBoxScene3DObject itBoxScene3DObject) {

    }

    @Override
    public void postRender(ITBoxScene3DObject itBoxScene3DObject) {

    }
}
