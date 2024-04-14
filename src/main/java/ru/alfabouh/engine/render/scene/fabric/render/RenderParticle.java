package ru.alfabouh.engine.render.scene.fabric.render;

import ru.alfabouh.engine.render.scene.Scene;
import ru.alfabouh.engine.render.scene.SceneRenderBase;
import ru.alfabouh.engine.render.scene.fabric.render.base.RenderWorldItem;
import ru.alfabouh.engine.render.scene.objects.IRenderObject;
import ru.alfabouh.engine.render.scene.objects.items.PhysicsObject;

public class RenderParticle extends RenderWorldItem {
    public RenderParticle() {
    }

    @Override
    public void onRender(double partialTicks, SceneRenderBase sceneRenderBase, IRenderObject renderItem) {
        PhysicsObject entityObject = (PhysicsObject) renderItem;
        if (entityObject.isHasModel()) {
            Scene.renderSceneObject(entityObject, entityObject.getRenderData().getOverObjectMaterial());
        }
    }
}
