package javagems3d.system.core;

import org.jetbrains.annotations.Nullable;

public interface ICore {
    void startSystem(@Nullable String externalGamePath);

    JGemsCore.EngineState engineState();
}
