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

package api.app.main;

import org.jetbrains.annotations.NotNull;
import javagems3d.system.core.EngineSystem;
import api.app.events.IAppEventSubscriber;
import api.app.manager.AppManager;
import api.app.resources.IAppResourceLoader;

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
     * @return the main manager
     */
    @NotNull
    AppManager createAppManager();
}
