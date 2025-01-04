package javagems3d.system.resources.managing.resources;

import javagems3d.system.resources.cache.ResourceCache;

public interface IGameResources {
    void destroy();
    ResourceCache getResourceCache();
    ResourceArrays getResourceArrays();
}
