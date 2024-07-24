package ru.alfabouh.jgems3d.engine.inventory;

import ru.alfabouh.jgems3d.engine.physics.world.IWorld;

public interface IInventoryOwner {
    Inventory inventory();
    IWorld getWorld();
}
