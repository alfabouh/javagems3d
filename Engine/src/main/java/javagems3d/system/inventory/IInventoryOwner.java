package javagems3d.system.inventory;

import javagems3d.physics.world.IWorld;

public interface IInventoryOwner {
    Inventory getInventory();

    IWorld getWorld();
}
