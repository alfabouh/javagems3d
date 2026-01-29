package workbench.resources;

import javagems3d.system.resources.cache.ResourceCache;
import javagems3d.system.resources.managing.resources.SystemResources;
import javagems3d.system.service.collections.Pair;
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
                LoadingInterfaceSwing.setResource(e.getText());
            }
            if (e.getResLoadSysMessageType().equals(ResLoadSysMessageType.ERR)) {
                Log.get().error(e.getText());
            } else {
                Log.get().trace(e.getText());
            }
        };
    }
}
