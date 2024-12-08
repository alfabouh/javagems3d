package javagems3d.system.map;

import javagems3d.system.map.loaders.IMapLoader;
import javagems3d.system.resources.manager.JGemsResourceManager;

public interface IMapActionsCallback {
    void onMapLoaded(IMapLoader loader, JGemsResourceManager resourceManager);
    void onMapDestroyed(IMapLoader loader, JGemsResourceManager resourceManager);
}
