package javagems3d.graphics.world;

import javagems3d.graphics.camera.base.ICamera;
import javagems3d.graphics.environment.IEnvironment;
import javagems3d.graphics.environment.lights.Light;
import javagems3d.graphics.objects.IObjectWithLights;
import javagems3d.graphics.objects.SceneObject;
import javagems3d.graphics.screen.timer.JGemsTimedAction;
import javagems3d.graphics.screen.timer.TimerPool;
import javagems3d.physics.world.IWorld;
import org.jetbrains.annotations.Nullable;

public interface IRenderWorld extends IWorld {
    void setCamera(ICamera camera);

    IEnvironment getEnvironment();
    ICamera getCamera();

    void removeObject(SceneObject sceneObject);
    void addObject(SceneObject sceneObject);

    TimerPool getTimerPool();

    default JGemsTimedAction createTimer() {
        return this.getTimerPool().createTimer();
    }

    void removeLight(Light light);
    void addLight(Light light, @Nullable IObjectWithLights lighted);

    void clearAll();
}
