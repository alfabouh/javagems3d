package ru.BouH.engine.render.scene.fabric.physics.base;

import ru.BouH.engine.render.scene.objects.IRenderObject;
import ru.BouH.engine.render.scene.objects.items.PhysicsObject;

public abstract class RenderWorldItem implements IRenderFabric {
    @Override
    public void onStartRender(IRenderObject renderItem) {
        PhysicsObject entityItem = (PhysicsObject) renderItem;
        if (entityItem.isHasModel()) {
            entityItem.getModel3D().getFormat().getPosition().set(entityItem.getWorldItem().getPosition());
        }
    }

    @Override
    public void onStopRender(IRenderObject renderItem) {
    }
}
