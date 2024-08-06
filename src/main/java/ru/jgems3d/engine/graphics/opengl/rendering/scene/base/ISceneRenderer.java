package ru.jgems3d.engine.graphics.opengl.rendering.scene.base;

import org.joml.Vector2i;
import ru.jgems3d.engine.graphics.opengl.rendering.scene.render_base.SceneData;

public interface ISceneRenderer {
    void onStartRender();
    void onRender(Vector2i windowSize);
    void onStopRender();

    void createResources(Vector2i windowSize);
    void destroyResources();
    void onWindowResize(Vector2i windowSize);
    SceneData getSceneData();
}
