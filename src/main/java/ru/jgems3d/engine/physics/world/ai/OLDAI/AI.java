package ru.jgems3d.engine.physics.world.ai.OLDAI;

import ru.jgems3d.engine.physics.world.basic.IWorldTicked;
import ru.jgems3d.engine.physics.world.basic.WorldItem;

public interface AI extends IWorldTicked {
    WorldItem target();

    boolean isActive();
}