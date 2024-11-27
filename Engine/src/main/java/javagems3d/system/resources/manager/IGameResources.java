package javagems3d.system.resources.manager;

import javagems3d.system.resources.cache.ResourceCache;

public interface IGameResources {
    void destroy();
    ResourceCache getResourceCache();
}
