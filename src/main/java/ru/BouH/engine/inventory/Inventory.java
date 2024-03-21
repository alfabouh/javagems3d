package ru.BouH.engine.inventory;

import ru.BouH.engine.inventory.items.InventoryItem;
import ru.BouH.engine.physics.world.IWorld;

public class Inventory {
    private final int maxSlots;
    private int currentSlot;
    private InventoryItem currentItem;
    private Slot[] inventorySlots;

    public Inventory(int maxSlots) {
        this.maxSlots = maxSlots;
        this.currentItem = null;
        this.initSlots();
    }

    private void initSlots() {
        this.inventorySlots = new Slot[maxSlots];
        for (int i = 0; i < this.getMaxSlots(); i++) {
            this.getInventorySlots()[i] = new Slot(i);
        }
    }

    public void updateInventory(IHasInventory player, IWorld world) {
        for (Slot slot : this.getInventorySlots()) {
            if (slot.getInventoryItem() != null) {
                slot.getInventoryItem().onUpdate(player, world, this.getCurrentSlot() == slot.getId());
            }
        }
    }

    public void setCurrentSlot(int currentSlot) {
        this.currentSlot = currentSlot;
        this.checkCurrItem();
    }

    public void onMouseLeftClick(IHasInventory iPlayer, IWorld world) {
        if (this.getCurrentItem() != null) {
            this.getCurrentItem().onLeftClick(iPlayer, world);
        }
    }

    public void onMouseRightClick(IHasInventory iPlayer, IWorld world) {
        if (this.getCurrentItem() != null) {
            this.getCurrentItem().onRightClick(iPlayer, world);
        }
    }

    public void scrollInventory(int vector) {
        vector *= -1;
        if (vector == 1) {
            this.currentSlot += 1;
            if (this.currentSlot >= this.getMaxSlots()) {
                this.currentSlot = 0;
            }
        } else if (vector == -1) {
            this.currentSlot -= 1;
            if (this.currentSlot < 0) {
                this.currentSlot = this.getMaxSlots() - 1;
            }
        }
        this.checkCurrItem();
    }

    public InventoryItem getItemInSlot(int slot) {
        return this.getInventorySlots()[slot].getInventoryItem();
    }

    public void setSlotItem(int slot, InventoryItem inventoryItem) {
        this.getInventorySlots()[slot].setInventoryItem(inventoryItem);
        this.checkCurrItem();
    }

    private void checkCurrItem() {
        this.currentItem = this.getInventorySlots()[this.getCurrentSlot()].getInventoryItem();
    }

    public Slot[] getInventorySlots() {
        return this.inventorySlots;
    }

    public int getMaxSlots() {
        return this.maxSlots;
    }

    public InventoryItem getCurrentItem() {
        return this.currentItem;
    }

    public int getCurrentSlot() {
        return this.currentSlot;
    }

    public static class Slot {
        private final int id;
        private InventoryItem inventoryItem;

        public Slot(int id) {
            this.id = id;
            this.inventoryItem = null;
        }

        public int getId() {
            return this.id;
        }

        public InventoryItem getInventoryItem() {
            return this.inventoryItem;
        }

        public void setInventoryItem(InventoryItem inventoryItem) {
            this.inventoryItem = inventoryItem;
        }
    }
}
