package javagems3d.graphics.environment.skybox.background;

import javagems3d.graphics.camera.FixedCamera;
import javagems3d.graphics.camera.base.ICamera;
import javagems3d.graphics.objects.entities.SceneProp;
import javagems3d.physics.world.IWorld;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public interface ISkyBackground{
    @NotNull FixedCamera getScaledCameraBackground();

    @NotNull Set<? extends SceneProp> getSkySceneObjectsFiltered();
    @NotNull Set<? extends SceneProp> getSkySceneObjects();

    IWorld getWorld();

    void update(ICamera mainViewCamera);

    void addObject(SceneProp object);

    void removeObject(SceneProp object);

    void clearBackGround();

    void setViewScaling(float scaling);

    float getViewScaling();

    void destroy(IWorld world);
    void create(IWorld world);

    default boolean contains(SceneProp sceneProp) {
        return this.getSkySceneObjects().contains(sceneProp);
    }
}
