package ru.alfabouh.jgems3d.engine.graphics.opengl.environment.light;

import org.joml.Matrix4f;
import ru.alfabouh.jgems3d.engine.graphics.opengl.world.SceneWorld;

public interface ILightManager {
    void updateBuffers(SceneWorld sceneWorld, Matrix4f viewMatrix);
}
