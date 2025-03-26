package javagems3d.graphics.world;

import javagems3d.graphics.camera.base.ICamera;
import javagems3d.graphics.environment.IEnvironment;
import javagems3d.graphics.objects.SceneObject;
import javagems3d.physics.world.IWorld;

public interface IRenderWorld extends IWorld {
    void setCamera(ICamera camera);

    IEnvironment getEnvironment();
    ICamera getCamera();

    void removeObjectFromWorld(SceneObject sceneObject);
    void addObjectInWorld(SceneObject sceneObject);
}
