package ru.jgems3d.engine.graphics.opengl.rendering.fabric.objects.render;

import ru.jgems3d.engine.graphics.opengl.rendering.scene.render_base.SceneRenderBase;
import ru.jgems3d.engine.graphics.opengl.rendering.items.props.SceneProp;
import ru.jgems3d.engine.graphics.opengl.rendering.items.IRenderObject;
import ru.jgems3d.engine.graphics.opengl.rendering.JGemsSceneUtils;
import ru.jgems3d.engine.graphics.opengl.rendering.scene.tick.FrameTicking;

public class RenderProp extends RenderWorldItem {
    public RenderProp() {
    }

    @Override
    public void onRender(FrameTicking frameTicking, SceneRenderBase sceneRenderBase, IRenderObject renderItem) {
        SceneProp sceneObject = (SceneProp) renderItem;
        sceneObject.getMeshRenderData().getShaderManager().bind();
        sceneObject.getMeshRenderData().getShaderManager().getUtils().performPerspectiveMatrix();
        JGemsSceneUtils.renderSceneObject(sceneObject);
        sceneObject.getMeshRenderData().getShaderManager().unBind();
    }
}
