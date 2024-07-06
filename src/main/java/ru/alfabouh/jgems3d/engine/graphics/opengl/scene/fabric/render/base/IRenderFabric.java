package ru.alfabouh.jgems3d.engine.graphics.opengl.scene.fabric.render.base;

import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.components.base.SceneRenderBase;
import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.objects.IRenderObject;

public interface IRenderFabric {
    void onRender(float partialTicks, SceneRenderBase sceneRenderBase, IRenderObject renderItem);

    void onStartRender(IRenderObject renderItem);

    void onStopRender(IRenderObject renderItem);
}
