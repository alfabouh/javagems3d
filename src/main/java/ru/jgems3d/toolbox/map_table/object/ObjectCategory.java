package ru.jgems3d.toolbox.map_table.object;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public final class ObjectCategory {
    public static final Set<ObjectCategory> values = new HashSet<>();

    public static ObjectCategory GENERIC = new ObjectCategory("Generic");

    private final String groupName;

    public ObjectCategory(String name) {
        this.groupName = name;
        ObjectCategory.values.add(this);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.groupName);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj.getClass() != this.getClass()) {
            return false;
        }
        return this.getGroupName().equals(((ObjectCategory) obj).getGroupName());
    }

    public String getGroupName() {
        return this.groupName;
    }
}