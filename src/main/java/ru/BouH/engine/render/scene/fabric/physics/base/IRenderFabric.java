package ru.BouH.engine.render.scene.fabric.physics.base;

import ru.BouH.engine.render.scene.SceneRenderBase;
import ru.BouH.engine.render.scene.objects.IRenderObject;

public interface IRenderFabric {
    void onRender(double partialTicks, SceneRenderBase sceneRenderBase, IRenderObject renderItem);

    void onStartRender(IRenderObject renderItem);

    void onStopRender(IRenderObject renderItem);
}
