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

package jgems_api.test;

import javagems3d.JGems3D;
import javagems3d.system.core.EngineSystem;
import api.app.main.JGemsGameApplication;
import api.app.main.JGemsGameEntry;
import api.app.main.JGemsGameInstance;
import api.app.events.IAppEventSubscriber;
import api.app.manager.AppManager;
import api.app.resources.IAppResourceLoader;
import jgems_api.test.events.TestEvents;
import jgems_api.test.manager.TestManager;
import jgems_api.test.resources.ModelLoader;
import org.jetbrains.annotations.NotNull;

@JGemsGameEntry(gameTitle = "Test", gameVersion = "0.1a", devStage = JGemsGameEntry.DevStage.PRE_ALPHA)
public class TestGame implements JGemsGameApplication {
    @JGemsGameInstance
    private static TestGame game;

    public TestGame() {
    }

    @Override
    public void loadResources(IAppResourceLoader appResourceLoader) {
        appResourceLoader.addAssetsLoader(new ModelLoader());
    }

    @Override
    public void subscribeEvents(IAppEventSubscriber appEventSubscriber) {
        appEventSubscriber.addClassWithEvents(TestEvents.class);
    }

    @Override
    public void preInitEvent(EngineSystem engineSystem) {
    }

    @Override
    public void postInitEvent(EngineSystem engineSystem) {
    }

    @Override
    public @NotNull AppManager createAppManager() {
        return new TestManager(null);
    }
}
