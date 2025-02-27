package javagems3d.system.inventory.items;

import javagems3d.physics.world.IWorld;
import javagems3d.system.inventory.IInventoryOwner;
import logger.Log;

public abstract class InventoryItem {
    private final String name;
    private IInventoryOwner itemOwner;
    private String description;

    public InventoryItem(String name) {
        this.name = name;
        this.itemOwner = null;
        this.description = null;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public abstract void onLeftClick(IWorld world);

    public abstract void onRightClick(IWorld world);

    public abstract void onUpdate(IWorld world, boolean isCurrent);

    public void onAddInInventory(IInventoryOwner hasInventory) {
        if (this.itemOwner() != null) {
            Log.get().error("Item " + this.getName() + " already exists in someone's inventory");
            return;
        }
        this.itemOwner = hasInventory;
    }

    protected IInventoryOwner itemOwner() {
        return this.itemOwner;
    }

    public String getName() {
        return this.name;
    }
}
