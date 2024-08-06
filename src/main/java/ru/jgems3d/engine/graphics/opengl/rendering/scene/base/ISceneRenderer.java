package ru.jgems3d.engine.graphics.opengl.rendering.scene.base;

import org.joml.Vector2i;
import ru.jgems3d.engine.graphics.opengl.rendering.scene.render_base.SceneData;

public interface ISceneRenderer {
    SceneData getSceneData();
    Vector2i getWindowDimensions();
    void onWindowResize(Vector2i dim);
}
