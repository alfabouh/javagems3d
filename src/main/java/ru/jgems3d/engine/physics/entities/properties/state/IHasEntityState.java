package ru.jgems3d.engine.physics.entities.properties.state;

import org.jetbrains.annotations.NotNull;

public interface IHasEntityState {
    EntityState getEntityState();
    void setEntityState(@NotNull EntityState state);
}
