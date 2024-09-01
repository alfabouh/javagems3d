/*
 * *
 *  * @author alfabouh
 *  * @since 2024
 *  * @link https://github.com/alfabouh/JavaGems3D
 *  *
 *  * This software is provided 'as-is', without any express or implied warranty.
 *  * In no event will the authors be held liable for any damages arising from the use of this software.
 *
 */

package javagems3d.engine.system.map.loaders;

import org.jetbrains.annotations.NotNull;
import javagems3d.engine.graphics.opengl.world.SceneWorld;
import javagems3d.engine.physics.world.PhysicsWorld;
import javagems3d.engine.system.map.MapInfo;
import javagems3d.engine.system.resources.manager.GameResources;

public interface IMapLoader {
    void createMap(GameResources globalResources, GameResources localResources, PhysicsWorld world, SceneWorld sceneWorld);

    void postLoad(PhysicsWorld world, SceneWorld sceneWorld);

    void preLoad(PhysicsWorld world, SceneWorld sceneWorld);

    @NotNull
    MapInfo getLevelInfo();
}
