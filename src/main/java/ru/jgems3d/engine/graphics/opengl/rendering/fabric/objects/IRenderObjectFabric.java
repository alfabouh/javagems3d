package ru.jgems3d.engine.graphics.opengl.rendering.fabric.objects;

import ru.jgems3d.engine.graphics.opengl.rendering.scene.SceneRenderBase;
import ru.jgems3d.engine.graphics.opengl.rendering.items.IRenderObject;

public interface IRenderObjectFabric {
    void onRender(float partialTicks, SceneRenderBase sceneRenderBase, IRenderObject renderItem);

    void onPreRender(IRenderObject renderItem);

    void onPostRender(IRenderObject renderItem);
}
