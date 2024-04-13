package ru.BouH.engine.physics.entities.enemy.ai;

import ru.BouH.engine.physics.world.object.IWorldDynamic;
import ru.BouH.engine.physics.world.object.WorldItem;

public interface AI extends IWorldDynamic {
    WorldItem target();
    boolean isActive();
}
