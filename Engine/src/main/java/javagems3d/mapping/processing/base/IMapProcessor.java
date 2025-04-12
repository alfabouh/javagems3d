package javagems3d.mapping.processing.base;

import javagems3d.graphics.environment.fog.FogScene;
import javagems3d.graphics.environment.fog.IFogScene;
import javagems3d.graphics.environment.skybox.ISkyBox;
import javagems3d.graphics.environment.skybox.SkyBox;
import javagems3d.graphics.world.SceneWorld;
import javagems3d.mapping.IGameMap;
import javagems3d.physics.world.PhysicsWorld;
import javagems3d.system.resources.managing.resources.SystemResources;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public interface IMapProcessor {
    void init();

    void preProcessing(PhysicsWorld world, SceneWorld sceneWorld);
    void onProcessing(PhysicsWorld world, SceneWorld sceneWorld);
    void postProcessing(PhysicsWorld world, SceneWorld sceneWorld);

    void onSetupSkyBox(ISkyBox skyBox, SkyBox.Background background);
    void onSetupFog(IFogScene fogScene);

    @Nullable IGameMap.IPlayerConstructor getPlayerConstructor();

    @NotNull String getMapName();
    @NotNull String getMapInformation();

    default @NotNull Vector3f getDefaultStartPosition() {
        return new Vector3f(0.0f);
    }

    default @NotNull Vector3f getDefaultStartRotation() {
        return new Vector3f(0.0f);
    }

    void setGlobalResources(@NotNull SystemResources globalResources);
    void setLocalResources(@NotNull SystemResources localResources);

    @NotNull SystemResources getGlobalResources();
    @NotNull SystemResources getLocalResources();
}