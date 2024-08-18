/*
 * *
 *  * @author alfabouh
 *  * @since 2024
 *  * @link https://github.com/alfabouh/JavaGems3D
 *  *
 *  * This software is provided 'as-is', without any express or implied warranty.
 *  * In no event will the authors be held liable for any damages arising from the use of this software.
 *
 */

package ru.jgems3d.engine.system.inventory.items;

import ru.jgems3d.engine.JGemsHelper;
import ru.jgems3d.engine.system.inventory.IInventoryOwner;
import ru.jgems3d.engine.physics.world.IWorld;

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
            JGemsHelper.getLogger().error("Item " + this.getName() + " already exists in someone's inventory");
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
