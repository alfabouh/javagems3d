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

package ru.jgems3d.engine_api.manager;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.jgems3d.engine.graphics.opengl.rendering.imgui.panels.base.PanelUI;
import ru.jgems3d.engine.graphics.opengl.world.SceneWorld;
import ru.jgems3d.engine.physics.world.PhysicsWorld;
import ru.jgems3d.engine.system.controller.binding.BindingManager;
import ru.jgems3d.engine.system.core.player.IPlayerConstructor;
import ru.jgems3d.engine.system.map.loaders.IMapLoader;
import ru.jgems3d.engine.system.resources.manager.GameResources;
import ru.jgems3d.engine_api.app.tbox.containers.TRenderContainer;
import ru.jgems3d.engine_api.configuration.AppConfiguration;
import ru.jgems3d.toolbox.map_sys.save.objects.object_attributes.AttributesContainer;
import ru.jgems3d.toolbox.map_table.object.ObjectCategory;

/**
 * This class contains controls/initializations for various important game systems.
 */
public abstract class AppManager {
    private final AppConfiguration appConfiguration;

    /**
     * Instantiates a new App manager.
     *
     * @param appConfiguration the app configuration
     */
    public AppManager(@Nullable AppConfiguration appConfiguration) {
        this.appConfiguration = appConfiguration == null ? AppConfiguration.createDefaultAppConfiguration() : appConfiguration;
    }

    /**
     * The BindingManager contains key bindings for different actions.
     *
     * @return the binding manager
     */
    public abstract @NotNull BindingManager createBindingManager();

    /**
     * This method returns an instance of the game character constructor that will be created each time the map is launched.
     *
     * @param mapLoader the map loader
     * @return the player constructor
     */
    public abstract @NotNull IPlayerConstructor createPlayer(IMapLoader mapLoader);

    /**
     * This method is responsible for arranging your objects on the TBox game map.
     *
     * @param sceneWorld           the scene world
     * @param physicsWorld         the physics world
     * @param localGameResources   the local game resources
     * @param globalGameResources  the global game resources
     * @param id                   the object id
     * @param objectCategory       the object category
     * @param attributesContainer  an attribute container that stores the parameters of an object (for example, position, rotation, etc.)
     * @param renderContainer      The necessary data to configure the object rendering
     * <br><br>
     * <b>You can use the default method of installing an object on the map using</b>
     * <pre>
     *      {@code
     *          TBoxMapDefaultObjectsPlacer#placeObjectOnMap
     *      }
     * </pre>
     * @see ru.jgems3d.engine.system.map.loaders.tbox.TBoxMapDefaultObjectsPlacer
     */
    public abstract void placeObjectOnMap(SceneWorld sceneWorld, PhysicsWorld physicsWorld, GameResources globalGameResources, GameResources localGameResources, String id, ObjectCategory objectCategory, AttributesContainer attributesContainer, TRenderContainer renderContainer);

    /**
     * This method returns a Panel UI instance of the game's main menu.
     *
     * @return the panel ui
     */
    public abstract @NotNull PanelUI gameMainMenuPanel();

    /**
     * Gets app configuration.
     *
     * @return the app configuration
     */
    public @NotNull AppConfiguration getAppConfiguration() {
        return this.appConfiguration;
    }
}
