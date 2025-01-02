package javagems3d.system.resources.assets.loading;

import javagems3d.system.resources.cache.ResourceCache;
import org.jetbrains.annotations.Nullable;

public interface ILoadingHelper {
    String DEFAULT_NAME = "unknown";

    ResourceCache getResourceCache();

    default boolean isCacheValid() {
        return this.getResourceCache() != null;
    }
}
