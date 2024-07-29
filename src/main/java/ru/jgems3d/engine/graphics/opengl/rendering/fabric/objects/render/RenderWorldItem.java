package ru.jgems3d.engine.graphics.opengl.rendering.fabric.objects.render;

import ru.jgems3d.engine.graphics.opengl.rendering.fabric.objects.IRenderObjectFabric;
import ru.jgems3d.engine.graphics.opengl.rendering.items.IRenderObject;

public abstract class RenderWorldItem implements IRenderObjectFabric {
    @Override
    public void onStartRender(IRenderObject renderItem) {
    }

    @Override
    public void onStopRender(IRenderObject renderItem) {
    }
}
