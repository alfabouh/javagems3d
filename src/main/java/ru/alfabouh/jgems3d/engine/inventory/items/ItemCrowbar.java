package ru.alfabouh.jgems3d.engine.inventory.items;

import ru.alfabouh.jgems3d.engine.JGems;
import ru.alfabouh.jgems3d.engine.physics.world.IWorld;

public class ItemCrowbar extends InventoryItem {
    public ItemCrowbar() {
        super("crowbar");
        this.setDescription(JGems.get().I18n("item.description.crowbar"));
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
