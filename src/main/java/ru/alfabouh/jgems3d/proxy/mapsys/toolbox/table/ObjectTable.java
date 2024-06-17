package ru.alfabouh.jgems3d.proxy.mapsys.toolbox.table;

import ru.alfabouh.jgems3d.proxy.mapsys.toolbox.table.object.ModeledObject;
import ru.alfabouh.jgems3d.proxy.mapsys.toolbox.table.object.base.IMapObject;
import ru.alfabouh.jgems3d.toolbox.resources.ResourceManager;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ObjectTable {
    private final Map<String, IMapObject> objects;

    public ObjectTable() {
        this.objects = new HashMap<>();
    }

    public void init() {
        this.addObject("cube", new ModeledObject(ResourceManager.createModel("/assets/jgems/models/cube/cube.obj")));
        this.addObject("door", new ModeledObject(ResourceManager.createModel("/assets/jgems/models/door2/door2.obj")));
        this.addObject("map01", new ModeledObject(ResourceManager.createModel("/assets/jgems/models/map01/map01.obj")));
    }

    public void addObject(String key, IMapObject mapObject) {
        this.getObjects().put(key, mapObject);
    }

    public Map<String, IMapObject> getObjects() {
        return this.objects;
    }
}
