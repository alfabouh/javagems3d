package api.application.workbench.resources;

import org.jetbrains.annotations.NotNull;

public abstract class Resource <T, E> {
    private final WFabric<T> fabricWBench;
    private final WFabric<E> fabricGame;
    private String groupId;
    private final String nameId;

    public Resource(@NotNull String nameId, @NotNull WFabric<T> fabricWBench, @NotNull WFabric<E> fabricGame) {
        this.nameId = nameId;
        this.fabricGame = fabricGame;
        this.fabricWBench = fabricWBench;
        this.groupId = null;
    }

    public WFabric<T> getFabricWBench() {
        return this.fabricWBench;
    }

    public WFabric<E> getFabricGame() {
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
    public interface WFabric <Y> {
        Y create();
    }
}
