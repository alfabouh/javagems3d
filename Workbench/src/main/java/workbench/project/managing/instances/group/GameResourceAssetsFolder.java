package workbench.project.managing.instances.group;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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
        return this.getFoldersInsideMap().get(group);
    }

    public void addFolderThere(@NotNull GameResourceAssetsFolder<T> t) {
        if (!this.getFoldersInsideMap().containsKey(t.getName())) {
            this.getFoldersInsideMap().put(t.getName(), t);
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

    public @Nullable T find(String path) {
        String[] strs = path.replaceFirst("/", "").split("/");
        if (strs.length == 1) {
            return this.getAsset(strs[0]);
        }
        final GameResourceAssetsFolder<T> assetsFolder = this.getFolderThere(strs[1]);
        if (assetsFolder != null) {
            return assetsFolder.find("/" + String.join("/", Arrays.copyOfRange(strs, 1, strs.length)));
        }
        return null;
    }

    public void removeGroupFromThere(String group) {
        this.getFoldersInsideMap().remove(group);
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
        return this.getAssetsThereMap().values();
    }

    @SuppressWarnings("all")
    public Collection<GameResourceAssetsFolder<T>> getFoldersThere() {
        return this.getFoldersInsideMap().values();
    }

    public Map<String, T> getAssetsThereMap() {
        if (this.assetsThere == null) {
            this.assetsThere = new LinkedHashMap<>();
        }
        return this.assetsThere;
    }

    public Map<String, GameResourceAssetsFolder<T>> getFoldersInsideMap() {
        if (this.assetsFoldersInside == null) {
            this.assetsFoldersInside = new LinkedHashMap<>();
        }
        return this.assetsFoldersInside;
    }

    public String getName() {
        return this.name;
    }
}
