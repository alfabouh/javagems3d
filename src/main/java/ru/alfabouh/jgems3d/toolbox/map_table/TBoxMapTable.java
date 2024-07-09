package ru.alfabouh.jgems3d.toolbox.map_table;

public class TBoxMapTable {
    public static final TBoxMapTable INSTANCE = new TBoxMapTable();

    private final ObjectsTable objectsTable;

    public TBoxMapTable() {
        this.objectsTable = new ObjectsTable();
    }

    public void init() {
        this.getObjectTable().init();
    }

    public ObjectsTable getObjectTable() {
        return this.objectsTable;
    }
}
