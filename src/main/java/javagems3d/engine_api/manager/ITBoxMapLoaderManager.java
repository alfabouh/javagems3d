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

package javagems3d.engine_api.manager;

import org.joml.Vector3f;
import javagems3d.engine.graphics.opengl.world.SceneWorld;
import javagems3d.engine.physics.world.PhysicsWorld;
import javagems3d.engine.system.resources.manager.GameResources;
import javagems3d.engine_api.app.tbox.containers.TUserData;
import javagems3d.toolbox.map_sys.save.objects.object_attributes.AttributesContainer;

/**
 * This class has methods, that used to place entities in worlds
 *
 * @see javagems3d.engine.system.map.loaders.tbox.placers.TBoxMapDefaultObjectsPlacer
 */
public interface ITBoxMapLoaderManager {
    /**
     * This method is responsible for arranging TBox entities on the TBox game map.
     *
     * @param sceneWorld          the scene world
     * @param physicsWorld        the physics world
     * @param localGameResources  the local game resources
     * @param globalGameResources the global game resources
     * @param id                  the object id
     * @param attributesContainer an attribute container that stores the parameters of an object (for example, position, rotation, etc.)
     * @param userData            data to configure the object properties
     */

    void placeTBoxEntityOnMap(SceneWorld sceneWorld, PhysicsWorld physicsWorld, GameResources globalGameResources, GameResources localGameResources, String id, AttributesContainer attributesContainer, TUserData userData);

    /**
     * This method is responsible for arranging TBox triggers on the TBox game map.
     *
     * @param physicsWorld        the physics world
     * @param position            vec3 pos
     * @param size                vec3 size
     * @param id                  the object id
     * @param attributesContainer an attribute container that stores the parameters of an object (for example, position, rotation, etc.)
     * @param userData            data to configure the object properties
     */

    void placeTBoxTriggerZoneOnMap(PhysicsWorld physicsWorld, Vector3f position, Vector3f size, String id, AttributesContainer attributesContainer, TUserData userData);

    /**
     * This method is responsible for arranging TBox markers on the TBox game map.
     *
     * @param sceneWorld          the scene world
     * @param physicsWorld        the physics world
     * @param localGameResources  the local game resources
     * @param globalGameResources the global game resources
     * @param id                  the object id
     * @param attributesContainer an attribute container that stores the parameters of an object (for example, position, rotation, etc.)
     * @param userData            data to configure the object properties
     */
    void handleTBoxMarker(SceneWorld sceneWorld, PhysicsWorld physicsWorld, GameResources globalGameResources, GameResources localGameResources, String id, AttributesContainer attributesContainer, TUserData userData);

    /**
     * Post Load.
     *
     * @param sceneWorld   the scene world
     * @param physicsWorld the physics world
     */
    void mapPostLoad(PhysicsWorld physicsWorld, SceneWorld sceneWorld);

    /**
     * Pre Load.
     *
     * @param sceneWorld   the scene physicsWorld
     * @param physicsWorld the physics physicsWorld
     */
    void mapPreLoad(PhysicsWorld physicsWorld, SceneWorld sceneWorld);
}