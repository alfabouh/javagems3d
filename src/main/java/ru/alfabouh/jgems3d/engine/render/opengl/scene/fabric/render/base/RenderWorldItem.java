package ru.alfabouh.jgems3d.engine.render.opengl.scene.fabric.render.base;

import ru.alfabouh.jgems3d.engine.render.opengl.scene.objects.IRenderObject;
import ru.alfabouh.jgems3d.engine.render.opengl.scene.objects.items.PhysicsObject;

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
