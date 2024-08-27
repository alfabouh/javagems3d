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
import org.joml.Vector3f;
import ru.jgems3d.engine.graphics.opengl.rendering.imgui.panels.base.PanelUI;
import ru.jgems3d.engine.graphics.opengl.world.SceneWorld;
import ru.jgems3d.engine.physics.world.PhysicsWorld;
import ru.jgems3d.engine.system.controller.binding.BindingManager;
import ru.jgems3d.engine.system.core.player.IPlayerConstructor;
import ru.jgems3d.engine.system.map.loaders.IMapLoader;
import ru.jgems3d.engine.system.map.loaders.tbox.placers.TBoxMapDefaultObjectsPlacer;
import ru.jgems3d.engine.system.resources.manager.GameResources;
import ru.jgems3d.engine_api.app.tbox.containers.TUserData;
import ru.jgems3d.engine_api.configuration.AppConfiguration;
import ru.jgems3d.toolbox.map_sys.save.objects.object_attributes.AttributesContainer;

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
     * @return The BindingManager, that contains key bindings for different actions.
     */
    public abstract @NotNull BindingManager createBindingManager();

    /**
     * @return instance of the game character constructor that will be created each time the map is launched.
     */
    public abstract @NotNull IPlayerConstructor createPlayer(IMapLoader mapLoader);
    /**
     * @return Panel UI instance of the game's main menu.
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
