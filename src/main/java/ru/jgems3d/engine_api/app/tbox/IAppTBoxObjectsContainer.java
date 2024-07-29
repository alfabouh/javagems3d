package ru.jgems3d.engine_api.app.tbox;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.jgems3d.engine_api.app.tbox.containers.TEntityContainer;
import ru.jgems3d.engine_api.app.tbox.containers.TRenderContainer;

public interface IAppTBoxObjectsContainer {
    void addObject(@NotNull String id, @NotNull TEntityContainer tEntityContainer, @Nullable TRenderContainer tRenderContainer);
}
