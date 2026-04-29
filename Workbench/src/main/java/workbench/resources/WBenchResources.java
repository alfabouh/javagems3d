package workbench.resources;

import javagems3d.system.resources.cache.ResourceCache;
import javagems3d.system.resources.managing.resources.SystemResources;
import logger.Log;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import workbench.resources.frame.LoadingInterfaceSwing;

import java.util.function.Consumer;

public class WBenchResources extends SystemResources {
    public WBenchResources(@NotNull ResourceCache resourceCache) {
        super(resourceCache);
    }

    @Override
    protected @Nullable Consumer<ResLoadSysMessage> getMessagesConsumer() {
        return (e) -> {
            if (LoadingInterfaceSwing.valid()) {
                LoadingInterfaceSwing.setResource(e.text());
            }
            if (e.resLoadSysMessageType().equals(ResLoadSysMessageType.ERR)) {
                Log.get().error(e.text());
            } else {
                Log.get().trace(e.text());
            }
        };
    }
}
