package ru.jgems3d.engine.system.map.loaders;

import org.jetbrains.annotations.NotNull;
import ru.jgems3d.engine.graphics.opengl.world.SceneWorld;
import ru.jgems3d.engine.physics.world.PhysicsWorld;
import ru.jgems3d.engine.system.map.MapInfo;
import ru.jgems3d.engine.system.resources.manager.GameResources;

public interface IMapLoader {
    void createMap(GameResources localResources, PhysicsWorld world, SceneWorld sceneWorld);
    void postLoad(PhysicsWorld world, SceneWorld sceneWorld);

    @NotNull MapInfo getLevelInfo();
}
