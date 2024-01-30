package ru.BouH.engine.render.environment.light;

import org.joml.Matrix4d;
import ru.BouH.engine.render.scene.world.SceneWorld;

public interface ILightManager {
    void updateBuffers(SceneWorld sceneWorld, Matrix4d viewMatrix);
}
