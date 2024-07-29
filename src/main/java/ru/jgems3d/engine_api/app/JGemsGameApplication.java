package ru.jgems3d.engine_api.app;

import org.jetbrains.annotations.NotNull;
import ru.jgems3d.engine_api.events.IAppEventSubscriber;
import ru.jgems3d.engine_api.manager.AppManager;
import ru.jgems3d.engine_api.resources.IAppResourceLoader;

public interface JGemsGameApplication {
    void loadResources(IAppResourceLoader appResourceLoader);
    void subscribeEvents(IAppEventSubscriber appEventSubscriber);

    void preInitEvent();
    void postInitEvent();

    @NotNull AppManager createAppManager();
}
