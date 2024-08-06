package ru.jgems3d.engine.graphics.opengl.rendering.scene.base;

import org.joml.Vector2i;
import ru.jgems3d.engine.graphics.opengl.rendering.scene.render_base.SceneData;
import ru.jgems3d.engine.graphics.opengl.rendering.scene.tick.FrameTicking;

public interface ISceneRenderer {
    void onStartRender();
    void onRender(FrameTicking frameTicking, Vector2i windowSize);
    void onStopRender();

    void createResources(Vector2i windowSize);
    void destroyResources();
    void onWindowResize(Vector2i windowSize);
    SceneData getSceneData();
}
