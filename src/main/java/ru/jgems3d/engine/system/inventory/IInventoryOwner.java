package ru.jgems3d.engine.system.inventory;

import ru.jgems3d.engine.physics.world.IWorld;

public interface IInventoryOwner {
    Inventory inventory();
    IWorld getWorld();
}
