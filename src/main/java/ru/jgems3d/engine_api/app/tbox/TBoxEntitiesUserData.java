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
import ru.jgems3d.engine_api.app.tbox.containers.TUserData;

import java.util.HashMap;

public final class TBoxEntitiesUserData implements ITBoxEntitiesUserData {
    private final HashMap<String, TUserData> entityUserDataHashMap;

    public TBoxEntitiesUserData() {
        this.entityUserDataHashMap = new HashMap<>();
    }

    public void add(@NotNull String id, @NotNull TUserData objectData) {
        this.entityUserDataHashMap.put(id, objectData);
    }

    public HashMap<String, TUserData> getEntityUserDataHashMap() {
        return this.entityUserDataHashMap;
    }
}