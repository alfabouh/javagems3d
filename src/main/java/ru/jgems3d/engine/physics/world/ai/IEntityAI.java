package ru.jgems3d.engine.physics.world.ai;

import org.jetbrains.annotations.NotNull;
import ru.jgems3d.engine.physics.world.basic.WorldItem;

public interface IEntityAI<T extends WorldItem> {
    @NotNull State getState();
    int priority();
    T getAIOwner();

    void onStartAI(WorldItem worldItem);
    void onUpdateAI(WorldItem worldItem);
    void onEndAI(WorldItem worldItem);

    enum State {
        ENABLED,
        DISABLED
    }
}
