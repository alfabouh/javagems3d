package api.application.workbench.resources;

import org.jetbrains.annotations.NotNull;

public abstract class APIResource<T, E> {
    private final MapObjectFabric<T> fabricWBench;
    private final MapObjectFabric<E> fabricGame;
    private final String nameId;
    private String groupId;

    public APIResource(@NotNull String nameId, @NotNull APIResource.MapObjectFabric<T> fabricWBench, @NotNull APIResource.MapObjectFabric<E> fabricGame) {
        this.nameId = nameId;
        this.fabricGame = fabricGame;
        this.fabricWBench = fabricWBench;
        this.groupId = null;
    }

    public MapObjectFabric<T> getFabricWBench() {
        return this.fabricWBench;
    }

    public MapObjectFabric<E> getFabricGame() {
        return this.fabricGame;
    }

    public String getGroupId() {
        return this.groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getNameId() {
        return this.nameId;
    }

    @FunctionalInterface
    public interface MapObjectFabric<Y> {
        Y create();
    }
}
