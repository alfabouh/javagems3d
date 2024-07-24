package ru.alfabouh.jgems3d.engine.physics.entities.properties.state;

import org.jetbrains.annotations.NotNull;

public interface IEntityState {
    EntityState getEntityState();
    void setEntityState(@NotNull EntityState state);
}
