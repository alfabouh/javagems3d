package javagems3d.system.controller.base;

import javagems3d.system.inventory.IInventoryOwner;

public interface IInventoryController {
    void updateItemWithInventory(IInventoryOwner hasInventory);
}
