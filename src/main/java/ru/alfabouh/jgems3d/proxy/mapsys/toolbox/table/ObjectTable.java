package ru.alfabouh.jgems3d.proxy.mapsys.toolbox.table;

import ru.alfabouh.jgems3d.proxy.mapsys.toolbox.table.object.ModeledObject;
import ru.alfabouh.jgems3d.proxy.mapsys.toolbox.table.object.base.IMapObject;
import ru.alfabouh.jgems3d.toolbox.resources.ResourceManager;
import ru.alfabouh.jgems3d.toolbox.resources.utils.SimpleModelLoader;

import java.util.HashSet;
import java.util.Set;

public class ObjectTable {
    private final Set<IMapObject> objects;

    public ObjectTable() {
        this.objects = new HashSet<>();
    }

    public void init() {
        this.addObject(new ModeledObject("cube", ResourceManager.createModel("/assets/jgems/models/cube/cube.obj")));
    }

    public void addObject(IMapObject mapObject) {
        this.getObjects().add(mapObject);
    }

    public Set<IMapObject> getObjects() {
        return this.objects;
    }
}
