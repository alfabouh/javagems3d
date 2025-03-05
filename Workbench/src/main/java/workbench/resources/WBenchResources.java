package workbench.resources;

import javagems3d.system.resources.cache.ResourceCache;
import javagems3d.system.resources.managing.resources.SystemResources;
import javagems3d.system.service.collections.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import workbench.graphics.scene.ui.LoadingInterface;

import java.util.function.Consumer;

public class WBenchResources extends SystemResources {
    public WBenchResources(@NotNull ResourceCache resourceCache) {
        super(resourceCache);
    }

    @Override
    protected @Nullable Consumer<Pair<String, Integer>> getMessagesConsumer() {
        return (e) -> LoadingInterface.setResource(e.getFirst());
    }
}
