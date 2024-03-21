package ru.BouH.engine.inventory.items;

import ru.BouH.engine.inventory.IHasInventory;
import ru.BouH.engine.physics.world.IWorld;

public abstract class InventoryItem {
    private final String name;

    public InventoryItem(String name) {
        this.name = name;
    }

    public abstract void onLeftClick(IHasInventory iPlayer, IWorld world);
    public abstract void onRightClick(IHasInventory iPlayer, IWorld world);
    public abstract void onUpdate(IHasInventory iPlayer, IWorld world, boolean isCurrent);

    public String getName() {
        return this.name;
    }
}
