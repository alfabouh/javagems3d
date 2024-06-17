package ru.alfabouh.jgems3d.toolbox.render.scene.items.renderers;

import org.joml.Vector3d;
import org.lwjgl.opengl.GL30;
import ru.alfabouh.jgems3d.toolbox.render.scene.dear_imgui.content.EditorContent;
import ru.alfabouh.jgems3d.toolbox.render.scene.items.objects.TBoxScene3DObject;
import ru.alfabouh.jgems3d.toolbox.render.scene.utils.TBoxSceneUtils;

public class Object3DRenderer implements ITBoxObjectRenderer {
    private final EditorContent editorContent;

    public Object3DRenderer(EditorContent editorContent) {
        this.editorContent = editorContent;
    }

    @Override
    public void onRender(TBoxScene3DObject tBoxScene3DObject, double deltaTime) {
        tBoxScene3DObject.getRenderData().getShaderManager().bind();
        tBoxScene3DObject.getRenderData().getShaderManager().getUtils().performPerspectiveMatrix();
        tBoxScene3DObject.getRenderData().getShaderManager().getUtils().performViewAndModelMatricesSeparately(TBoxSceneUtils.getMainCameraViewMatrix(), tBoxScene3DObject.getModel());
        tBoxScene3DObject.getRenderData().getShaderManager().performUniform("selected", tBoxScene3DObject.isSelected());

        tBoxScene3DObject.getRenderData().getShaderManager().performUniform("showLight", true);
        tBoxScene3DObject.getRenderData().getShaderManager().performUniform("sunPos", editorContent.getSunPos());
        tBoxScene3DObject.getRenderData().getShaderManager().performUniform("sunColor", new Vector3d(editorContent.sunClr));
        tBoxScene3DObject.getRenderData().getShaderManager().performUniform("sunBright", editorContent.sunBrightness[0]);

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
