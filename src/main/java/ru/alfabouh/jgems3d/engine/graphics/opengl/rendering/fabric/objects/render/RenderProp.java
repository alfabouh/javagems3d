package ru.alfabouh.jgems3d.engine.graphics.opengl.rendering.fabric.objects.render;

import ru.alfabouh.jgems3d.engine.graphics.opengl.rendering.scene.SceneRenderBase;
import ru.alfabouh.jgems3d.engine.graphics.opengl.rendering.items.props.SceneProp;
import ru.alfabouh.jgems3d.engine.graphics.opengl.rendering.items.IRenderObject;
import ru.alfabouh.jgems3d.engine.graphics.opengl.rendering.utils.JGemsSceneUtils;

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
