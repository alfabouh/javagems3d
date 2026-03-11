package javagems3d.system.service.files;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;

public abstract class AbstractObjectsFolder<T extends AbstractObjectsFolder.ObjectWithName> {
    public static final String DEF_PATH = "root";
    private final String name;
    private Map<String, T> objectsThere;
    private Map<String, AbstractObjectsFolder<T>> objectFoldersInside;
    private transient AbstractObjectsFolder<T> parent;

    public AbstractObjectsFolder(@NotNull String name) {
        this.name = name;
        this.objectsThere = new LinkedHashMap<>();
        this.objectFoldersInside = new LinkedHashMap<>();
    }

    public void reset() {
        this.getFoldersThere().clear();
        this.getObjectsThere().clear();
    }

    public AbstractObjectsFolder<T> setParent(AbstractObjectsFolder<T> parent) {
        this.parent = parent;
        return this;
    }

    public void buildRelations() {
        for (AbstractObjectsFolder<T> leaves : this.getFoldersThere()) {
            leaves.setParent(this);
            leaves.buildRelations();
        }
    }

    public AbstractObjectsFolder<T> getFolderThere(String group) {
        return this.getFoldersThereMap().get(group);
    }

    public void putFolderThere(@NotNull AbstractObjectsFolder<T> t) {
        if (!this.getFoldersThereMap().containsKey(t.getName())) {
            this.getFoldersThereMap().put(t.getName(), t);
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

    private int totalObjectsThere(boolean countFolders, int max) {
        int total = 0;
        if (countFolders) {
            total += this.getFoldersThere().size();
            if (total >= max) {
                return max;
            }
        }
        total += this.getObjectsThere().size();
        if (total >= max) {
            return max;
        }
        for (AbstractObjectsFolder<T> folder : this.getFoldersThere()) {
            int subTotal = folder.totalObjectsThere(countFolders, max - total);
            total += subTotal;
            if (total >= max) {
                return max;
            }
        }
        return total;
    }

    public int totalObjectsThere(int max) {
        return this.totalObjectsThere(false, max);
    }

    public int totalObjectsAndFoldersThere(int max) {
        return this.totalObjectsThere(true, max);
    }

    public <E extends AbstractObjectsFolder<T>> void putObjectInside(@NotNull String path, @NotNull T t, Function<String, E> createNewFolder) {
        AbstractObjectsFolder<T> next = this;
        String[] pathNodes = path.split("/");
        for (String node : pathNodes) {
            if (!node.isEmpty()) {
                if (!next.getFoldersThereMap().containsKey(node)) {
                    AbstractObjectsFolder<T> tMapObjectTemplatesFolder = createNewFolder.apply(node);
                    next.putFolderThere(tMapObjectTemplatesFolder);
                    next = tMapObjectTemplatesFolder;
                } else {
                    next = next.getFoldersThereMap().get(node);
                }
            }
        }
        next.putObjectThere(t);
    }

    public @Nullable T find(String absPath, String name) {
        return this.find(absPath + "/" + name);
    }

    public @Nullable T find(String fullPath) {
        String[] strs = fullPath.replaceFirst("/", "").split("/");
        if (strs.length == 1) {
            return this.getObject(strs[0]);
        }
        final AbstractObjectsFolder<T> objectsFolder = this.getFolderThere(strs[0]);
        if (objectsFolder != null) {
            return objectsFolder.find("/" + String.join("/", Arrays.copyOfRange(strs, 1, strs.length)));
        }
        return null;
    }

    public void removeFolderFromThere(String group) {
        this.getFoldersThereMap().remove(group);
    }

    public void putObjectThere(@NotNull T t) {
        this.getObjectsThereMap().put(t.name(), t);
    }

    public void removeObjectFromThere(String id) {
        this.getObjectsThereMap().remove(id);
    }

    public T getObject(String id) {
        return this.objectsThere.get(id);
    }

    public Collection<T> getObjectsThere() {
        return this.getObjectsThereMap().values();
    }

    @SuppressWarnings("all")
    public Collection<AbstractObjectsFolder<T>> getFoldersThere() {
        return this.getFoldersThereMap().values();
    }

    public Map<String, T> getObjectsThereMap() {
        if (this.objectsThere == null) {
            this.objectsThere = new LinkedHashMap<>();
        }
        return this.objectsThere;
    }

    public Map<String, AbstractObjectsFolder<T>> getFoldersThereMap() {
        if (this.objectFoldersInside == null) {
            this.objectFoldersInside = new LinkedHashMap<>();
        }
        return this.objectFoldersInside;
    }

    public String getName() {
        return this.name;
    }

    public interface ObjectWithName {
        String name();
    }
}