package ru.alfabouh.jgems3d.engine.render.opengl.scene.fabric.render;

import ru.alfabouh.jgems3d.engine.render.opengl.scene.fabric.render.base.RenderWorldItem;
import ru.alfabouh.jgems3d.engine.render.opengl.scene.objects.IRenderObject;
import ru.alfabouh.jgems3d.engine.render.opengl.scene.utils.JGemsSceneUtils;
import ru.alfabouh.jgems3d.engine.render.opengl.scene.components.base.SceneRenderBase;
import ru.alfabouh.jgems3d.engine.render.opengl.scene.objects.items.PhysicsObject;

public class RenderParticle extends RenderWorldItem {
    public RenderParticle() {
    }

    @Override
    public void onRender(float partialTicks, SceneRenderBase sceneRenderBase, IRenderObject renderItem) {
        PhysicsObject entityObject = (PhysicsObject) renderItem;
        if (entityObject.isHasModel()) {
            JGemsSceneUtils.renderSceneObject(entityObject, entityObject.getRenderData().getOverObjectMaterial());
        }
    }
}
