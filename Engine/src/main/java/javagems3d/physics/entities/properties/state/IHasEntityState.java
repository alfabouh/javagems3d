package javagems3d.physics.entities.properties.state;

import org.jetbrains.annotations.NotNull;

public interface IHasEntityState {
    EntityState getEntityState();

    void setEntityState(@NotNull EntityState state);
}
