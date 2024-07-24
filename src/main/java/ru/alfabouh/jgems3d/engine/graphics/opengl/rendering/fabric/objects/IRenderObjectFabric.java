package ru.alfabouh.jgems3d.engine.graphics.opengl.rendering.fabric.objects;

import ru.alfabouh.jgems3d.engine.graphics.opengl.rendering.scene.SceneRenderBase;
import ru.alfabouh.jgems3d.engine.graphics.opengl.rendering.items.IRenderObject;

public interface IRenderObjectFabric {
    void onRender(float partialTicks, SceneRenderBase sceneRenderBase, IRenderObject renderItem);

    void onStartRender(IRenderObject renderItem);

    void onStopRender(IRenderObject renderItem);
}
