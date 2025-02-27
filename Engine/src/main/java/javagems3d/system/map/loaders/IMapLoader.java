package javagems3d.system.map.loaders;

import javagems3d.graphics.environment.skybox.SkyBox;
import javagems3d.system.core.player.IPlayerConstructor;
import org.jetbrains.annotations.NotNull;
import javagems3d.graphics.world.SceneWorld;
import javagems3d.physics.world.PhysicsWorld;
import javagems3d.system.map.MapInfo;
import javagems3d.system.resources.managing.resources.SystemResources;
import org.jetbrains.annotations.Nullable;

public interface IMapLoader {
    void createMap(SystemResources globalResources, SystemResources localResources, PhysicsWorld world, SceneWorld sceneWorld);

    void postLoad(PhysicsWorld world, SceneWorld sceneWorld);

    void preLoad(PhysicsWorld world, SceneWorld sceneWorld);

    void fillSkyBox(SkyBox.Background background);

    @Nullable IPlayerConstructor playerConstructor();

    @NotNull MapInfo getLevelInfo();
}
