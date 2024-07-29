package ru.jgems3d.toolbox.map_table;

import ru.jgems3d.toolbox.resources.TBoxResourceManager;

import java.lang.reflect.InvocationTargetException;

public class TBoxMapTable {
    public static final TBoxMapTable INSTANCE = new TBoxMapTable();

    private final ObjectsTable objectsTable;

    public TBoxMapTable() {
        this.objectsTable = new ObjectsTable();
    }

    public void init(TBoxResourceManager tBoxResourceManager) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        this.getObjectTable().init(tBoxResourceManager);
    }

    public ObjectsTable getObjectTable() {
        return this.objectsTable;
    }
}
