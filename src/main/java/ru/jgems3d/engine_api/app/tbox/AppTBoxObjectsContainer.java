package ru.jgems3d.engine_api.app.tbox;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.jgems3d.engine.math.Pair;
import ru.jgems3d.engine_api.app.tbox.containers.TEntityContainer;
import ru.jgems3d.engine_api.app.tbox.containers.TRenderContainer;
import ru.jgems3d.toolbox.map_table.object.AbstractObjectData;

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
