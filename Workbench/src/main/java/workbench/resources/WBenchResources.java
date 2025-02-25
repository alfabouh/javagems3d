package workbench.resources;

import javagems3d.system.resources.cache.ResourceCache;
import javagems3d.system.resources.managing.resources.SystemResources;
import org.jetbrains.annotations.NotNull;

public class WBenchResources extends SystemResources {
    public WBenchResources(@NotNull ResourceCache resourceCache) {
        super(resourceCache);
    }

    @Override
    protected void handlePreProcessingMessage(String message) {

    }

    @Override
    protected void handleFailedProcessingMessage(String message) {

    }

    @Override
    protected void handleSuccessfulProcessingMessage(String message) {

    }
}
