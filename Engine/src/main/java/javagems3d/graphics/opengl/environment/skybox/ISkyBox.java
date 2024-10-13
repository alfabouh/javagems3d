package javagems3d.graphics.opengl.environment.skybox;

import javagems3d.graphics.opengl.camera.ICamera;
import javagems3d.graphics.opengl.rendering.scene.render_base.SceneData;
import javagems3d.graphics.opengl.world.SceneWorld;

public interface ISkyBox {
    void updateSkyBox(SceneWorld sceneWorld, ICamera camera);
    void destroySkyBox(SceneWorld sceneWorld);
}
