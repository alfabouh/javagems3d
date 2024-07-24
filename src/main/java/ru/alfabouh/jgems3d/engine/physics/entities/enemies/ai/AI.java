package ru.alfabouh.jgems3d.engine.physics.entities.enemies.ai;

import ru.alfabouh.jgems3d.engine.physics.world.basic.IWorldTicked;
import ru.alfabouh.jgems3d.engine.physics.world.basic.WorldItem;

public interface AI extends IWorldTicked {
    WorldItem target();

    boolean isActive();
}
