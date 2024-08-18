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
    public abstract BindingManager createBindingManager();

    /**
     * This is a player constructor that will be introduced to the game world.
     *
     * @param mapLoader the map loader
     * @return the player constructor
     */
    public abstract @NotNull IPlayerConstructor createPlayer(IMapLoader mapLoader);

    /**
     * Place object in t box map.
     *
     * @param sceneWorld          the scene world
     * @param physicsWorld        the physics world
     * @param localGameResources  the local game resources
     * @param id                  the id
     * @param objectCategory      the object category
     * @param attributesContainer the attributes container
     * @param renderContainer     the render container
     */
    public abstract void placeObjectInTBoxMap(SceneWorld sceneWorld, PhysicsWorld physicsWorld, GameResources localGameResources, String id, ObjectCategory objectCategory, AttributesContainer attributesContainer, TRenderContainer renderContainer);

    /**
     * Open main menu panel ui.
     *
     * @return the panel ui
     */
    public abstract @NotNull PanelUI openMainMenu();

    /**
     * Gets app configuration.
     *
     * @return the app configuration
     */
    public @NotNull AppConfiguration getAppConfiguration() {
        return this.appConfiguration;
    }
}
