package ru.alfabouh.jgems3d.engine.render.opengl.environment.light;

import org.joml.Matrix4d;
import ru.alfabouh.jgems3d.engine.render.opengl.scene.world.SceneWorld;

public interface ILightManager {
    void updateBuffers(SceneWorld sceneWorld, Matrix4d viewMatrix);
}
