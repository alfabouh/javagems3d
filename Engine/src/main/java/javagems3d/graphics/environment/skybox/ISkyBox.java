package javagems3d.graphics.environment.skybox;

import javagems3d.graphics.camera.base.ICamera;
import javagems3d.graphics.world.SceneWorld;

public interface ISkyBox {
    void updateSkyBox(SceneWorld sceneWorld, ICamera camera);
    void destroySkyBox(SceneWorld sceneWorld);
}
