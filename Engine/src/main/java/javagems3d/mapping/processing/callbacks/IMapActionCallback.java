package javagems3d.mapping.processing.callbacks;

import javagems3d.mapping.IGameMap;
import javagems3d.mapping.processing.base.IMapProcessor;
import javagems3d.system.resources.managing.JGemsResourceManager;
import org.jetbrains.annotations.NotNull;

public interface IMapActionCallback {
    void onLoaded(@NotNull IMapProcessor mapProcessor, @NotNull IGameMap gameMap, @NotNull JGemsResourceManager resourceManager);
    void onDestroying(@NotNull IGameMap gameMap, @NotNull JGemsResourceManager resourceManager);
}
