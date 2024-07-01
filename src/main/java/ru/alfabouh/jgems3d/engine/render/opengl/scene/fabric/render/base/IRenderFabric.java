package ru.alfabouh.jgems3d.engine.render.opengl.scene.fabric.render.base;

import ru.alfabouh.jgems3d.engine.render.opengl.scene.objects.IRenderObject;
import ru.alfabouh.jgems3d.engine.render.opengl.scene.components.base.SceneRenderBase;

public interface IRenderFabric {
    void onRender(float partialTicks, SceneRenderBase sceneRenderBase, IRenderObject renderItem);

    void onStartRender(IRenderObject renderItem);

    void onStopRender(IRenderObject renderItem);
}
