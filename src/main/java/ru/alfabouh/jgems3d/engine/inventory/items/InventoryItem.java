package ru.alfabouh.jgems3d.engine.inventory.items;

import ru.alfabouh.jgems3d.engine.inventory.IHasInventory;
import ru.alfabouh.jgems3d.engine.physics.world.IWorld;
import ru.alfabouh.jgems3d.engine.system.exception.JGemsException;

public abstract class InventoryItem {
    private final String name;
    private IHasInventory itemOwner;
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

    public void onAddInInventory(IHasInventory hasInventory) {
        if (this.itemOwner() != null) {
            throw new JGemsException("Item " + this.getName() + " already exists in someone's inventory");
        }
        this.itemOwner = hasInventory;
    }

    protected IHasInventory itemOwner() {
        return this.itemOwner;
    }

    public String getName() {
        return this.name;
    }
}