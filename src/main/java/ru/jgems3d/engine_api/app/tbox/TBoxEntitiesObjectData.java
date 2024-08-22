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

package ru.jgems3d.engine_api.app.tbox;

import org.jetbrains.annotations.NotNull;
import ru.jgems3d.engine_api.app.tbox.containers.TObjectData;

import java.util.HashMap;

public final class TBoxEntitiesObjectData implements ITBoxEntitiesObjectData {
    private final HashMap<String, TObjectData> entityObjectDataHashMap;

    public TBoxEntitiesObjectData() {
        this.entityObjectDataHashMap = new HashMap<>();
    }

    public void add(@NotNull String id, @NotNull TObjectData objectData) {
        this.entityObjectDataHashMap.put(id, objectData);
    }

    public HashMap<String, TObjectData> getEntityObjectDataHashMap() {
        return this.entityObjectDataHashMap;
    }
}