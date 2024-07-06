package ru.alfabouh.jgems3d.engine.graphics.opengl.scene.fabric.render.base;

import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.objects.IRenderObject;
import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.objects.items.AbstractSceneItemObject;

public abstract class RenderWorldItem implements IRenderFabric {
    @Override
    public void onStartRender(IRenderObject renderItem) {
        AbstractSceneItemObject entityItem = (AbstractSceneItemObject) renderItem;
        if (entityItem.hasRender() && entityItem.hasModel()) {
            entityItem.getModel().getFormat().getPosition().set(entityItem.getWorldItem().getPosition());
        }
    }

    @Override
    public void onStopRender(IRenderObject renderItem) {
    }
}
