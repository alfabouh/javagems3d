package ru.jgems3d.engine.api_bridge.data;

import org.jetbrains.annotations.NotNull;
import ru.jgems3d.engine_api.app.JGemsGameApplication;
import ru.jgems3d.engine_api.app.JGemsGameEntry;
import ru.jgems3d.engine_api.manager.AppManager;

public class APIGameInfo {
    private final JGemsGameEntry gemsEntry;
    private final JGemsGameApplication appInstance;
    private final AppManager appManager;

    public APIGameInfo(@NotNull AppManager appManager, @NotNull JGemsGameApplication appInstance, @NotNull JGemsGameEntry gemsEntry) {
        this.appManager = appManager;
        this.gemsEntry = gemsEntry;
        this.appInstance = appInstance;
    }

    public AppManager getAppManager() {
        return this.appManager;
    }

    public JGemsGameApplication getAppInstance() {
        return this.appInstance;
    }

    public JGemsGameEntry getGemsEntry() {
        return this.gemsEntry;
    }
}