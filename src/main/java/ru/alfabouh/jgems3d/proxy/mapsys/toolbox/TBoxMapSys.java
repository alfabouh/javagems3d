package ru.alfabouh.jgems3d.proxy.mapsys.toolbox;

import ru.alfabouh.jgems3d.proxy.mapsys.toolbox.table.ObjectTable;

public class TBoxMapSys {
    public static final TBoxMapSys INSTANCE = new TBoxMapSys();

    private final ObjectTable objectTable;

    public TBoxMapSys() {
        this.objectTable = new ObjectTable();
    }

    public void init() {
        this.getObjectTable().init();
    }

    public ObjectTable getObjectTable() {
        return this.objectTable;
    }
}
