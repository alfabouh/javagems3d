package ru.jgems3d.engine.graphics.opengl.rendering.fabric.objects.render;

import ru.jgems3d.engine.graphics.opengl.rendering.scene.render_base.SceneRenderBase;
import ru.jgems3d.engine.graphics.opengl.rendering.items.IRenderObject;
import ru.jgems3d.engine.graphics.opengl.rendering.items.objects.AbstractSceneEntity;
import ru.jgems3d.engine.graphics.opengl.rendering.JGemsSceneUtils;

public class RenderEntity2D3D extends RenderWorldItem {
    public RenderEntity2D3D() {
    }

    @Override
    public void onRender(float partialTicks, SceneRenderBase sceneRenderBase, IRenderObject renderItem) {
        AbstractSceneEntity entityObject = (AbstractSceneEntity) renderItem;
        if (entityObject.hasRender() && entityObject.hasModel()) {
            entityObject.getModel().getFormat().setOrientedToView(true);
            JGemsSceneUtils.renderSceneObject(entityObject);
        }
    }
}
