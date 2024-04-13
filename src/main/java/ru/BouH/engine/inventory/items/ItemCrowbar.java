package ru.BouH.engine.inventory.items;

import ru.BouH.engine.physics.world.IWorld;

public class ItemCrowbar extends InventoryItem {
    public ItemCrowbar() {
        super("crowbar");
        this.setDescription("[Plank Removal Tool]");
    }

    @Override
    public void onLeftClick(IWorld world) {
    }

    @Override
    public void onRightClick(IWorld world) {
    }

    @Override
    public void onUpdate(IWorld world, boolean isCurrent) {
    }
}
