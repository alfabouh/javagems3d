package ru.jgems3d.engine.graphics.opengl.rendering;

import org.joml.Vector2i;
import ru.jgems3d.engine.graphics.opengl.rendering.scene.SceneData;

public interface ISceneRenderer {
    SceneData getSceneData();
    Vector2i getWindowDimensions();
    void onWindowResize(Vector2i dim);
}
