package javagems3d.system.resources.managing.resources;

import javagems3d.JGems3D;
import javagems3d.system.resources.cache.ResourceCache;
import org.jetbrains.annotations.NotNull;

public class JGemsSystemResources extends SystemResources {
    public JGemsSystemResources(@NotNull ResourceCache resourceCache) {
        super(resourceCache);
    }

    @Override
    protected void handlePreProcessingMessage(String message) {
        JGems3D.get().getScreen().tryAddLineInLoadingScreen(0xffffff, message);
    }

    @Override
    protected void handleFailedProcessingMessage(String message) {
        JGems3D.get().getScreen().tryAddLineInLoadingScreen(0xff0000, message);
    }

    @Override
    protected void handleSuccessfulProcessingMessage(String message) {
        JGems3D.get().getScreen().tryAddLineInLoadingScreen(0x00ff00, message);
    }
}
