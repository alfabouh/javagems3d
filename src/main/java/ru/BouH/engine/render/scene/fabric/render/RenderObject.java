package ru.BouH.engine.render.scene.fabric.render;

import ru.BouH.engine.render.scene.Scene;
import ru.BouH.engine.render.scene.SceneRenderBase;
import ru.BouH.engine.render.scene.fabric.render.base.RenderWorldItem;
import ru.BouH.engine.render.scene.objects.IRenderObject;
import ru.BouH.engine.render.scene.objects.items.PhysicsObjectModeled;

public class RenderObject extends RenderWorldItem {
    public RenderObject() {
    }

    @Override
    public void onRender(double partialTicks, SceneRenderBase sceneRenderBase, IRenderObject renderItem) {
        PhysicsObjectModeled entityObject = (PhysicsObjectModeled) renderItem;
        if (entityObject.isHasModel()) {
            Scene.renderSceneObject(entityObject, entityObject.getRenderData().getOverObjectMaterial());
        }
    }
}
