package javagems3d.graphics.environment.skybox.background;

import javagems3d.graphics.camera.FixedCamera;
import javagems3d.graphics.camera.base.ICamera;
import javagems3d.graphics.objects.entities.SceneProp;
import javagems3d.graphics.rendering.scene.culling.SceneCulling;
import javagems3d.graphics.transformation.JGemsTransformManager;
import javagems3d.physics.world.IWorld;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public abstract class SkyBoxBackground implements ISkyBackground {
    private final Set<SceneProp> toRenderSet;
    private final FixedCamera scaledCameraBackground;
    private final IWorld world;
    private float viewScaling;
    private final SceneCulling<SceneProp> sceneCulling;

    public SkyBoxBackground(IWorld world, float viewScaling) {
        this.toRenderSet = new HashSet<>();
        this.scaledCameraBackground = new FixedCamera(new Vector3f(), new Vector3f());
        this.sceneCulling = new SceneCulling<>(SceneCulling.FRUSTUM_CPU, null);
        this.viewScaling = viewScaling;
        this.world = world;
    }

    public void update(ICamera mainViewCamera) {
        this.getScaledCameraBackground().setCameraPosition(mainViewCamera.getCamPosition().mul(1.0f / this.getViewScaling()));
        this.getScaledCameraBackground().setCameraRotation(mainViewCamera.getCamRotation());
        this.updateIterator();
    }

    protected void updateIterator() {
        Iterator<SceneProp> scenePropIterator = this.getSkySceneObjects().iterator();
        while (scenePropIterator.hasNext()) {
            SceneProp sceneProp = scenePropIterator.next();
            sceneProp.onUpdate(this.getWorld());
            if (sceneProp.isDead()) {
                sceneProp.onDestroyWithEvent(this.getWorld());
                scenePropIterator.remove();
            }
        }
    }

    public void setViewScaling(float viewScaling) {
        this.viewScaling = viewScaling;
    }

    public void clearBackGround() {
        this.getSkySceneObjects().forEach(e -> e.onDestroyWithEvent(this.getWorld()));
        this.getSkySceneObjects().clear();
    }

    @Override
    public void addObject(SceneProp object) {
        object.onSpawnWithEvent(this.getWorld());
        this.getSkySceneObjects().add(object);
    }

    @Override
    public void removeObject(SceneProp object) {
        object.onDestroyWithEvent(this.getWorld());
        this.getSkySceneObjects().remove(object);
    }

    @Override
    public void destroy(IWorld world) {
        this.clearBackGround();
        this.getSceneCulling().destroyResources();
    }

    @Override
    public void create(IWorld world) {
        this.getSceneCulling().createResources();
    }

    public IWorld getWorld() {
        return this.world;
    }

    public @NotNull FixedCamera getScaledCameraBackground() {
        return this.scaledCameraBackground;
    }

    public float getViewScaling() {
        return this.viewScaling;
    }

    public SceneCulling<SceneProp> getSceneCulling() {
        return this.sceneCulling;
    }

    public @NotNull Set<SceneProp> getSkySceneObjectsFiltered() {
        Set<SceneProp> set = new HashSet<>(this.getSkySceneObjects());
        this.getSceneCulling().cull(set, JGemsTransformManager.INSTANCE.getPerspectiveMatrix(), this.getScaledCameraBackground());
        return set;
    }

    public @NotNull Set<SceneProp> getSkySceneObjects() {
        return this.toRenderSet;
    }
}
