package javagems3d.physics.world.ai;

import org.jetbrains.annotations.NotNull;
import javagems3d.physics.world.basic.WorldItem;

public interface IEntityAI<T extends WorldItem> {
    @NotNull
    State getState();

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
