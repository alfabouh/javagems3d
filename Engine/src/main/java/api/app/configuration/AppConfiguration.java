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

package api.app.configuration;

import api.app.main.tbox.containers.TUserData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import javagems3d.graphics.opengl.world.SceneWorld;
import javagems3d.physics.world.PhysicsWorld;
import javagems3d.system.map.loaders.tbox.placers.TBoxMapDefaultObjectsPlacer;
import javagems3d.system.resources.manager.GameResources;
import javagems3d.system.service.path.JGemsPath;
import api.app.manager.ITBoxMapLoaderManager;
import javagems3d.temp.map_sys.save.objects.object_attributes.AttributesContainer;

/**
 * Game Configuration. You can configure this yourself, or create a default:
 * <pre>
 *     {@code
 *     AppConfiguration.createDefaultAppConfiguration()
 *     }
 * </pre>
 */
public class AppConfiguration {
    private final JGemsPath windowIcon;
    private final ITBoxMapLoaderManager mapLoaderManager;

    public AppConfiguration(@NotNull ITBoxMapLoaderManager mapLoaderManager, JGemsPath windowIcon) {
        this.mapLoaderManager = mapLoaderManager;
        this.windowIcon = windowIcon;
    }

    public static AppConfiguration createDefaultAppConfiguration(ITBoxMapLoaderManager mapLoaderManager) {
        return new AppConfiguration(mapLoaderManager, new JGemsPath("/assets/jgems/icons/icon.png"));
    }

    public static AppConfiguration createDefaultAppConfiguration() {
        return new AppConfiguration(new DefaultMapLoaderManager(), new JGemsPath("/assets/jgems/icons/icon.png"));
    }

    /**
     * @return object, that handles TBox incoming entities on map.
     */
    public @NotNull ITBoxMapLoaderManager getMapLoaderManager() {
        return this.mapLoaderManager;
    }

    /**
     * Window icon.
     *
     * @return the window icon
     */
    public @Nullable JGemsPath getWindowIcon() {
        return this.windowIcon;
    }

    public static class DefaultMapLoaderManager implements ITBoxMapLoaderManager {
        @Override
        public void placeTBoxEntityOnMap(SceneWorld sceneWorld, PhysicsWorld physicsWorld, GameResources globalGameResources, GameResources localGameResources, String id, AttributesContainer attributesContainer, TUserData userData) {
            TBoxMapDefaultObjectsPlacer.placeTBoxEntityOnMap(sceneWorld, physicsWorld, globalGameResources, localGameResources, id, attributesContainer, userData);
        }

        @Override
        public void placeTBoxTriggerZoneOnMap(PhysicsWorld physicsWorld, Vector3f position, Vector3f size, String id, AttributesContainer attributesContainer, TUserData userData) {
            TBoxMapDefaultObjectsPlacer.placeTBoxTriggerZoneOnMap(physicsWorld, position, size, id, attributesContainer, userData);
        }

        @Override
        public void handleTBoxMarker(SceneWorld sceneWorld, PhysicsWorld physicsWorld, GameResources globalGameResources, GameResources localGameResources, String id, AttributesContainer attributesContainer, TUserData userData) {
            TBoxMapDefaultObjectsPlacer.handleTBoxMarker(sceneWorld, physicsWorld, globalGameResources, localGameResources, id, attributesContainer, userData);
        }

        @Override
        public void mapPostLoad(PhysicsWorld physicsWorld, SceneWorld sceneWorld) {
            TBoxMapDefaultObjectsPlacer.mapPostLoad(physicsWorld, sceneWorld);
        }

        @Override
        public void mapPreLoad(PhysicsWorld physicsWorld, SceneWorld sceneWorld) {
            TBoxMapDefaultObjectsPlacer.mapPreLoad(physicsWorld, sceneWorld);
        }
    }
}