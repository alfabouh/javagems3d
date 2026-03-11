package api.application.workbench.resources;

import javagems3d.system.service.files.AbstractObjectsFolder;
import org.jetbrains.annotations.NotNull;

public abstract class APIResource<T, E> implements AbstractObjectsFolder.ObjectWithName {
    private final MapObjectFabric<T> fabricWBench;
    private final MapObjectFabric<E> fabricGame;
    private final String name;

    public APIResource(@NotNull String name, @NotNull APIResource.MapObjectFabric<T> fabricWBench, @NotNull APIResource.MapObjectFabric<E> fabricGame) {
        this.name = name;
        this.fabricGame = fabricGame;
        this.fabricWBench = fabricWBench;
    }

    public MapObjectFabric<T> getFabricWBench() {
        return this.fabricWBench;
    }

    public MapObjectFabric<E> getFabricGame() {
        return this.fabricGame;
    }

    @Override
    public String name() {
        return this.name;
    }

    @FunctionalInterface
    public interface MapObjectFabric<Y> {
        Y create();
    }
}
