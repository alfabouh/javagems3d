package ru.alfabouh.jgems3d.engine.graphics.opengl.scene.fabric.render.objects;

import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.components.base.SceneRenderBase;
import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.fabric.models.SceneProp;
import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.fabric.render.base.RenderWorldItem;
import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.objects.IRenderObject;
import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.utils.JGemsSceneUtils;

public class RenderProp extends RenderWorldItem {
    public RenderProp() {
    }

    @Override
    public void onRender(float partialTicks, SceneRenderBase sceneRenderBase, IRenderObject renderItem) {
        SceneProp sceneObject = (SceneProp) renderItem;
        sceneObject.getModelRenderParams().getShaderManager().bind();
        sceneObject.getModelRenderParams().getShaderManager().getUtils().performPerspectiveMatrix();
        JGemsSceneUtils.renderSceneObject(sceneObject, sceneObject.getOverObjectMaterial());
        sceneObject.getModelRenderParams().getShaderManager().unBind();
    }
}
