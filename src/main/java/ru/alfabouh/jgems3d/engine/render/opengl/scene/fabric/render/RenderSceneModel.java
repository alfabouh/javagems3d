package ru.alfabouh.jgems3d.engine.render.opengl.scene.fabric.render;

import ru.alfabouh.jgems3d.engine.render.opengl.scene.fabric.render.base.RenderWorldItem;
import ru.alfabouh.jgems3d.engine.render.opengl.scene.objects.IRenderObject;
import ru.alfabouh.jgems3d.engine.render.opengl.scene.components.base.SceneRenderBase;
import ru.alfabouh.jgems3d.engine.render.opengl.scene.fabric.models.SceneObject;
import ru.alfabouh.jgems3d.engine.render.opengl.scene.utils.JGemsSceneUtils;

public class RenderSceneModel extends RenderWorldItem {
    public RenderSceneModel() {
    }

    @Override
    public void onRender(double partialTicks, SceneRenderBase sceneRenderBase, IRenderObject renderItem) {
        SceneObject sceneObject = (SceneObject) renderItem;
        sceneObject.getModelRenderParams().getShaderManager().bind();
        sceneObject.getModelRenderParams().getShaderManager().getUtils().performPerspectiveMatrix();
        sceneObject.getModelRenderParams().getShaderManager().performUniform("texture_scaling", sceneObject.getTextureScaling());
        JGemsSceneUtils.renderSceneObject(sceneObject, sceneObject.getOverObjectMaterial());
        sceneObject.getModelRenderParams().getShaderManager().unBind();
    }
}
