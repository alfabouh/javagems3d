package javagems3d.mapping.loading;

import javagems3d.system.map.loaders.IMapLoader;
import javagems3d.system.resources.managing.JGemsResourceManager;

public interface IMapProcessingCallback {
    void onMapLoaded(IMapLoader loader, JGemsResourceManager resourceManager);
    void onMapDestroyed(IMapLoader loader, JGemsResourceManager resourceManager);
}
