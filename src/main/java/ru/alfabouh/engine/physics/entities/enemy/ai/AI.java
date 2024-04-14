package ru.alfabouh.engine.physics.entities.enemy.ai;

import ru.alfabouh.engine.physics.world.object.IWorldDynamic;
import ru.alfabouh.engine.physics.world.object.WorldItem;

public interface AI extends IWorldDynamic {
    WorldItem target();

    boolean isActive();
}
