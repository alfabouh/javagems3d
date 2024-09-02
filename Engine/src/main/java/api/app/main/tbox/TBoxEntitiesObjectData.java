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

package api.app.main.tbox;

import api.app.main.tbox.containers.TObjectData;
import org.jetbrains.annotations.NotNull;

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