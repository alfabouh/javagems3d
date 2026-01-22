package workbench.project.managing.instances.group;

import org.jetbrains.annotations.NotNull;
import workbench.project.managing.instances.IAsset;

import java.util.*;

public class GameResourceAssetsFolder<T extends IAsset> {
    private final String name;
    private Map<String, T> assetsThere;
    private Map<String, GameResourceAssetsFolder<T>> assetsFoldersInside;
    private transient GameResourceAssetsFolder<T> parent;

    public GameResourceAssetsFolder(@NotNull String name) {
        this.name = name;
        this.assetsThere = new LinkedHashMap<>();
        this.assetsFoldersInside = new LinkedHashMap<>();
    }

    public GameResourceAssetsFolder<T> setParent(GameResourceAssetsFolder<T> parent) {
        this.parent = parent;
        return this;
    }

    public void buildRelations() {
        for (GameResourceAssetsFolder<T> leaves : this.getFoldersThere()) {
            leaves.setParent(this);
            leaves.buildRelations();
        }
    }

    public GameResourceAssetsFolder<T> getFolderThere(String group) {
        return this.getAssetsFoldersInsideMap().get(group);
    }

    public void addFolderThere(@NotNull GameResourceAssetsFolder<T> t) {
        if (!this.getAssetsFoldersInsideMap().containsKey(t.getName())) {
            this.getAssetsFoldersInsideMap().put(t.getName(), t);
            t.setParent(this);
        }
    }

    public String getHierarchy() {
        if (this.parent == null) {
            return "";
        }
        String current = this.getName();
        return this.parent.getHierarchy() + "/" + current;
    }

    public void removeGroupFromThere(String group) {
        this.getAssetsFoldersInsideMap().remove(group);
    }

    public void addAssetThere(@NotNull T t) {
        this.getAssetsThereMap().put(t.getName(), t);
    }

    public void removeAssetFromThere(String id) {
        this.getAssetsThereMap().remove(id);
    }

    public T getAsset(String id) {
        return this.assetsThere.get(id);
    }

    public Collection<T> getAssetsThere() {
        return this.assetsThere.values();
    }

    @SuppressWarnings("all")
    public Collection<GameResourceAssetsFolder<T>> getFoldersThere() {
        return this.assetsFoldersInside == null ? new ArrayList<>() : this.assetsFoldersInside.values();
    }

    public Map<String, T> getAssetsThereMap() {
        if (this.assetsThere == null) {
            this.assetsThere = new LinkedHashMap<>();
        }
        return this.assetsThere;
    }

    public Map<String, GameResourceAssetsFolder<T>> getAssetsFoldersInsideMap() {
        if (this.assetsFoldersInside == null) {
            this.assetsFoldersInside = new LinkedHashMap<>();
        }
        return this.assetsFoldersInside;
    }

    public String getName() {
        return this.name;
    }
}
