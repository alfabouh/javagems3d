package ru.alfabouh.engine.render.scene.fabric.render.base;

import ru.alfabouh.engine.render.scene.SceneRenderBase;
import ru.alfabouh.engine.render.scene.objects.IRenderObject;

public interface IRenderFabric {
    void onRender(double partialTicks, SceneRenderBase sceneRenderBase, IRenderObject renderItem);

    void onStartRender(IRenderObject renderItem);

    void onStopRender(IRenderObject renderItem);
}
