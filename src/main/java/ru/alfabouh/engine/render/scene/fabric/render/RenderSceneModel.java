package ru.alfabouh.engine.render.scene.fabric.render;

import ru.alfabouh.engine.render.scene.Scene;
import ru.alfabouh.engine.render.scene.SceneRenderBase;
import ru.alfabouh.engine.render.scene.fabric.models.SceneObject;
import ru.alfabouh.engine.render.scene.fabric.render.base.RenderWorldItem;
import ru.alfabouh.engine.render.scene.objects.IRenderObject;

public class RenderSceneModel extends RenderWorldItem {
    public RenderSceneModel() {
    }

    @Override
    public void onRender(double partialTicks, SceneRenderBase sceneRenderBase, IRenderObject renderItem) {
        SceneObject sceneObject = (SceneObject) renderItem;
        sceneObject.getModelRenderParams().getShaderManager().bind();
        sceneObject.getModelRenderParams().getShaderManager().getUtils().performProjectionMatrix();
        sceneObject.getModelRenderParams().getShaderManager().performUniform("texture_scaling", sceneObject.getTextureScaling());
        Scene.renderSceneObject(sceneObject, sceneObject.getOverObjectMaterial());
        sceneObject.getModelRenderParams().getShaderManager().unBind();
    }
}
