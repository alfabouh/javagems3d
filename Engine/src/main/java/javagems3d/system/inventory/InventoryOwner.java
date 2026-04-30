package javagems3d.system.inventory;

import javagems3d.physics.world.IWorld;

@Deprecated
public interface InventoryOwner {
    InventoryBase getInventory();

    IWorld getWorld();
}
