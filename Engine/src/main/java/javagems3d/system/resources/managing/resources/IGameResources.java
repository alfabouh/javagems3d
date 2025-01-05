package javagems3d.system.resources.managing.resources;

import javagems3d.system.resources.cache.ResourceCache;
import javagems3d.system.resources.managing.resources.data.ResourcesDataArrays;

public interface IGameResources {
    void destroy();
    ResourceCache getResourceCache();
    ResourcesDataArrays getResourceArrays();
}
