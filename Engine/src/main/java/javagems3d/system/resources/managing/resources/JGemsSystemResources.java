package javagems3d.system.resources.managing.resources;

import javagems3d.JGems3D;
import javagems3d.system.resources.cache.ResourceCache;
import javagems3d.system.service.collections.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class JGemsSystemResources extends SystemResources {
    public JGemsSystemResources(@NotNull ResourceCache resourceCache) {
        super(resourceCache);
    }

    @Override
    protected @Nullable Consumer<Pair<String, Integer>> getMessagesConsumer() {
        return (e) ->  JGems3D.get().getScreen().tryAddLineInLoadingScreen(e.getSecond(), e.getFirst());
    }
}
