package ru.alfabouh.engine.render.environment.light;

import org.joml.Matrix4d;
import ru.alfabouh.engine.render.scene.world.SceneWorld;

public interface ILightManager {
    void updateBuffers(SceneWorld sceneWorld, Matrix4d viewMatrix);
}
