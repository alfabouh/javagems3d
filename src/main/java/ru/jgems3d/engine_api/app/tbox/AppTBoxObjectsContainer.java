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
import org.jetbrains.annotations.Nullable;
import ru.jgems3d.engine.system.service.collections.Pair;
import ru.jgems3d.engine_api.app.tbox.containers.TEntityContainer;
import ru.jgems3d.engine_api.app.tbox.containers.TRenderContainer;

import java.util.HashMap;

public final class AppTBoxObjectsContainer implements IAppTBoxObjectsContainer {
    private final HashMap<String, Pair<TEntityContainer, TRenderContainer>> map;

    public AppTBoxObjectsContainer() {
        this.map = new HashMap<>();
    }

    public void addObject(@NotNull String id, @NotNull TEntityContainer tEntityContainer, @Nullable TRenderContainer tRenderContainer) {
        this.map.put(id, new Pair<>(tEntityContainer, tRenderContainer));
    }

    public HashMap<String, Pair<TEntityContainer, TRenderContainer>> getMap() {
        return this.map;
    }

}
