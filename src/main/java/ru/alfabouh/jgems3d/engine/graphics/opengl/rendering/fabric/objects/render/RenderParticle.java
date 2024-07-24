package ru.alfabouh.jgems3d.engine.graphics.opengl.rendering.fabric.objects.render;

import ru.alfabouh.jgems3d.engine.graphics.opengl.rendering.scene.SceneRenderBase;
import ru.alfabouh.jgems3d.engine.graphics.opengl.rendering.items.IRenderObject;
import ru.alfabouh.jgems3d.engine.graphics.opengl.rendering.items.objects.AbstractSceneEntity;
import ru.alfabouh.jgems3d.engine.graphics.opengl.rendering.utils.JGemsSceneUtils;

public class RenderParticle extends RenderWorldItem {
    public RenderParticle() {
    }

    @Override
    public void onRender(float partialTicks, SceneRenderBase sceneRenderBase, IRenderObject renderItem) {
        AbstractSceneEntity entityObject = (AbstractSceneEntity) renderItem;
        if (entityObject.hasRender() && entityObject.hasModel()) {
            JGemsSceneUtils.renderSceneObject(entityObject, entityObject.getRenderData().getOverObjectMaterial());
        }
    }
}
