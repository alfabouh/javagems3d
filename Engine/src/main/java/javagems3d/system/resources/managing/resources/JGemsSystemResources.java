package javagems3d.system.resources.managing.resources;

import javagems3d.JGems3D;
import javagems3d.system.resources.cache.ResourceCache;
import logger.Log;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class JGemsSystemResources extends SystemResources {
    public JGemsSystemResources(@NotNull ResourceCache resourceCache) {
        super(resourceCache);
    }

    @Override
    protected @Nullable Consumer<ResLoadSysMessage> getMessagesConsumer() {
        return (e) -> {
            JGems3D.get().getScreen().tryAddLineInLoadingScreen(e.color(), e.text());
            if (e.resLoadSysMessageType().equals(ResLoadSysMessageType.ERR)) {
                Log.get().error(e.text());
            } else {
                Log.get().trace(e.text());
            }
        };
    }
}
