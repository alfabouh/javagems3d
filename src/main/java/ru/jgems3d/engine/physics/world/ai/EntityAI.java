package ru.jgems3d.engine.physics.world.ai;

import org.jetbrains.annotations.NotNull;
import ru.jgems3d.engine.physics.world.basic.WorldItem;

public interface EntityAI {
    @NotNull State getState();
    int priority();

    void onStartAI(WorldItem worldItem);
    void onUpdateAI(WorldItem worldItem);
    void onEndAI(WorldItem worldItem);

    enum State {
        ENABLED,
        DISABLED
    }
}
