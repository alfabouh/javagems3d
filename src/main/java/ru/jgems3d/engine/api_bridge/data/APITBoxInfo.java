package ru.jgems3d.engine.api_bridge.data;

import org.jetbrains.annotations.NotNull;
import ru.jgems3d.engine_api.app.JGemsTBoxApplication;
import ru.jgems3d.engine_api.app.JGemsTBoxEntry;

public class APITBoxInfo {
    private final JGemsTBoxEntry tBoxEntry;
    private final JGemsTBoxApplication appInstance;

    public APITBoxInfo(@NotNull JGemsTBoxApplication appInstance, @NotNull JGemsTBoxEntry tBoxEntry) {
        this.tBoxEntry = tBoxEntry;
        this.appInstance = appInstance;
    }

    public JGemsTBoxEntry getTBoxEntry() {
        return this.tBoxEntry;
    }

    public JGemsTBoxApplication getAppInstance() {
        return this.appInstance;
    }
}
