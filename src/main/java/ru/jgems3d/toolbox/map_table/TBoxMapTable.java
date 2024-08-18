/*
 * *
 *  * @author alfabouh
 *  * @since 2024
 *  * @link https://github.com/alfabouh/JavaGems3D
 *  *
 *  * This software is provided 'as-is', without any express or implied warranty.
 *  * In no event will the authors be held liable for any damages arising from the use of this software.
 *
 */

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
