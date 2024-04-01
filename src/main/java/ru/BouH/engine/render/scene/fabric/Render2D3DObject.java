package ru.BouH.engine.render.scene.fabric;

import ru.BouH.engine.render.scene.Scene;
import ru.BouH.engine.render.scene.SceneRenderBase;
import ru.BouH.engine.render.scene.fabric.render.base.RenderWorldItem;
import ru.BouH.engine.render.scene.objects.IRenderObject;
import ru.BouH.engine.render.scene.objects.items.PhysicsObject;

public class Render2D3DObject extends RenderWorldItem {
    public Render2D3DObject() {
    }

    @Override
    public void onRender(double partialTicks, SceneRenderBase sceneRenderBase, IRenderObject renderItem) {
        PhysicsObject entityObject = (PhysicsObject) renderItem;
        if (entityObject.isHasModel()) {
            entityObject.getModel3D().getFormat().setOrientedToView(true);
            Scene.renderSceneObject(entityObject, entityObject.getRenderData().getOverObjectMaterial());
        }
    }
}
