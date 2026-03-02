package javagems3d.system.external.mapping.processing.base;

import javagems3d.system.resources.managing.resources.SystemResources;
import org.jetbrains.annotations.NotNull;

public abstract class MapProcessor implements IMapProcessor {
    private SystemResources globalResources;
    private SystemResources localResources;

    public MapProcessor() {
    }

    public void setGlobalResources(@NotNull SystemResources globalResources) {
        this.globalResources = globalResources;
    }

    public void setLocalResources(@NotNull SystemResources localResources) {
        this.localResources = localResources;
    }

    @Override
    public @NotNull SystemResources getGlobalResources() {
        return this.globalResources;
    }

    @Override
    public @NotNull SystemResources getLocalResources() {
        return this.localResources;
    }
}
