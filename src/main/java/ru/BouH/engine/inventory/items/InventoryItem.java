package ru.BouH.engine.inventory.items;

import ru.BouH.engine.game.exception.GameException;
import ru.BouH.engine.inventory.IHasInventory;
import ru.BouH.engine.physics.world.IWorld;

public abstract class InventoryItem {
    private final String name;
    private IHasInventory itemOwner;

    public InventoryItem(String name) {
        this.name = name;
        this.itemOwner = null;
    }

    public abstract void onLeftClick(IWorld world);

    public abstract void onRightClick(IWorld world);

    public abstract void onUpdate(IWorld world, boolean isCurrent);

    public void onAddInInventory(IHasInventory hasInventory) {
        if (this.itemOwner() != null) {
            throw new GameException("Item " + this.getName() + " already exists in someone's inventory");
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
