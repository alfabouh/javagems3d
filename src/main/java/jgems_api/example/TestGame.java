package jgems_api.example;

import jgems_api.example.events.TestEvents;
import jgems_api.example.manager.TestManager;
import jgems_api.example.resources.ModelLoader;
import org.jetbrains.annotations.NotNull;
import ru.jgems3d.engine.JGemsHelper;
import ru.jgems3d.engine.system.service.misc.JGPath;
import ru.jgems3d.engine_api.app.JGemsGameApplication;
import ru.jgems3d.engine_api.app.JGemsGameEntry;
import ru.jgems3d.engine_api.events.IAppEventSubscriber;
import ru.jgems3d.engine_api.manager.AppManager;
import ru.jgems3d.engine_api.resources.IAppResourceLoader;

@JGemsGameEntry(gameTitle = "Test", gameVersion = "0.1a", devStage = JGemsGameEntry.DevStage.PRE_ALPHA)
public class TestGame implements JGemsGameApplication {
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
    public void preInitEvent() {

    }

    @Override
    public void postInitEvent() {
        JGemsHelper.LOCALISATION.createLocalisation("English", new JGPath("/assets/testgame/lang/"));
    }

    @Override
    public @NotNull AppManager createAppManager() {
        return new TestManager(null);
    }
}
