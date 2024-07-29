package ru.jgems3d.engine.system.map.loaders;

import ru.jgems3d.engine.graphics.opengl.world.SceneWorld;
import ru.jgems3d.engine.physics.world.PhysicsWorld;
import ru.jgems3d.engine.system.map.MapInfo;

public interface IMapLoader {
    void createMap(PhysicsWorld world, SceneWorld sceneWorld);
    void postLoad(PhysicsWorld world, SceneWorld sceneWorld);

    MapInfo getLevelInfo();
}
