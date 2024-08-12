package ru.jgems3d.engine.physics.world.ai.navigation;

import org.jetbrains.annotations.NotNull;
import ru.jgems3d.engine.physics.world.ai.EntityAI;
import ru.jgems3d.engine.physics.world.basic.WorldItem;

public class NavigationAI implements EntityAI {


    @Override
    public int priority() {
        return 0;
    }

    @Override
    public void onStartAI(WorldItem worldItem) {

    }

    @Override
    public void onUpdateAI(WorldItem worldItem) {

    }

    @Override
    public void onEndAI(WorldItem worldItem) {

    }

    @Override
    public @NotNull State getState() {
        return null;
    }
}
