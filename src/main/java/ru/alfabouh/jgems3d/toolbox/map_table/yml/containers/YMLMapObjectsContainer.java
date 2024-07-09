package ru.alfabouh.jgems3d.toolbox.map_table.yml.containers;

import ru.alfabouh.jgems3d.engine.math.Pair;
import ru.alfabouh.jgems3d.toolbox.map_table.object.ObjectType;

import java.util.HashMap;
import java.util.Map;

public class YMLMapObjectsContainer {
    private final Map<String, Pair<String, ObjectType>> map;

    public YMLMapObjectsContainer() {
        this.map = new HashMap<>();
    }

    public void addObject(String id, String meshDataGroupName, ObjectType type) {
        this.getMap().put(id, new Pair<>(meshDataGroupName, type));
    }

    public Map<String, Pair<String, ObjectType>> getMap() {
        return this.map;
    }
}
