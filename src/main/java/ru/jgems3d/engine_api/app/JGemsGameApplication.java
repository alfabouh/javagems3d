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

package ru.jgems3d.engine_api.app;

import org.jetbrains.annotations.NotNull;
import ru.jgems3d.engine.system.core.EngineSystem;
import ru.jgems3d.engine_api.events.IAppEventSubscriber;
import ru.jgems3d.engine_api.manager.AppManager;
import ru.jgems3d.engine_api.resources.IAppResourceLoader;

/**
 * The entry point for a JavaGems3D-based game
 */
public interface JGemsGameApplication {
    /**
     * Resource loading manager.
     */
    void loadResources(IAppResourceLoader appResourceLoader);

    /**
     * Events subscriber.
     */
    void subscribeEvents(IAppEventSubscriber appEventSubscriber);

    /**
     * Pre init event(launches before base system initialization).
     *
     * @param engineSystem engine core
     */
    void preInitEvent(EngineSystem engineSystem);

    /**
     * Post init event(launches after base system initialization).
     *
     * @param engineSystem engine core
     */
    void postInitEvent(EngineSystem engineSystem);

    /**
     * Application state manager.
     *
     * @return the app manager
     */
    @NotNull
    AppManager createAppManager();
}
