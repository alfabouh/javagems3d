package ru.alfabouh.engine.inventory;

import org.jetbrains.annotations.NotNull;
import ru.alfabouh.engine.inventory.items.InventoryItem;
import ru.alfabouh.engine.physics.world.IWorld;

public class Inventory {
    private final int maxSlots;
    private final IHasInventory owner;
    private final int scrollMaxCd;
    private int currentSlot;
    private InventoryItem currentItem;
    private Slot[] inventorySlots;
    private int scrollCd;

    public Inventory(@NotNull IHasInventory owner, int maxSlots) {
        this.maxSlots = maxSlots;
        this.currentItem = null;
        this.owner = owner;
        this.scrollMaxCd = 2;
        this.scrollCd = 0;
        this.initSlots();
    }

    private void initSlots() {
        this.inventorySlots = new Slot[maxSlots];
        for (int i = 0; i < this.getMaxSlots(); i++) {
            this.getInventorySlots()[i] = new Slot(i);
        }
    }

    public void updateInventory(IWorld world) {
        if (this.scrollCd > 0) {
            this.scrollCd -= 1;
        }
        for (Slot slot : this.getInventorySlots()) {
            if (slot.getInventoryItem() != null) {
                slot.getInventoryItem().onUpdate(world, this.getCurrentSlot() == slot.getId());
            }
        }
    }

    public boolean addItemInInventory(InventoryItem inventoryItem) {
        for (int i = 0; i < this.getMaxSlots(); i++) {
            if (this.getItemInSlot(i) == null) {
                this.setSlotItem(i, inventoryItem);
                return true;
            }
        }
        return false;
    }

    public void onMouseLeftClick(IWorld world) {
        if (this.getCurrentItem() != null) {
            this.getCurrentItem().onLeftClick(world);
        }
    }

    public void onMouseRightClick(IWorld world) {
        if (this.getCurrentItem() != null) {
            this.getCurrentItem().onRightClick(world);
        }
    }

    public IHasInventory getOwner() {
        return this.owner;
    }

    public boolean isEmpty() {
        for (int j = 0; j < this.getMaxSlots(); j++) {
            if (this.getItemInSlot(j) != null) {
                return false;
            }
        }
        return true;
    }

    public void scrollInventoryToNotNullItem(int vector) {
        if (this.scrollCd > 0) {
            return;
        }
        if (this.isEmpty()) {
            return;
        }
        if (vector == 1) {
            this.currentSlot += 1;
            if (this.currentSlot >= this.getMaxSlots()) {
                this.currentSlot = 0;
            }
            int j = this.currentSlot;
            L1:
            while (true) {
                if (this.getItemInSlot(j) != null) {
                    this.currentSlot = j;
                    break;
                }
                j += 1;
                if (j >= this.getMaxSlots()) {
                    for (int i = 0; i < this.getMaxSlots(); i++) {
                        if (this.getItemInSlot(i) != null) {
                            this.currentSlot = i;
                            break L1;
                        }
                    }
                }
            }
            this.scrollCd = this.scrollMaxCd;
            this.checkCurrItem();
        } else if (vector == -1) {
            this.currentSlot -= 1;
            if (this.currentSlot < 0) {
                this.currentSlot = this.getMaxSlots() - 1;
            }
            int j = this.currentSlot;
            L1:
            while (true) {
                if (this.getItemInSlot(j) != null) {
                    this.currentSlot = j;
                    break;
                }
                j -= 1;
                if (j < 0) {
                    for (int i = this.getMaxSlots(); i > 0; i--) {
                        if (this.getItemInSlot(i) != null) {
                            this.currentSlot = i;
                            break L1;
                        }
                    }
                }
            }
            this.scrollCd = this.scrollMaxCd;
            this.checkCurrItem();
        }
    }

    public void scrollInventory(int vector) {
        if (this.scrollCd > 0) {
            return;
        }
        if (vector == 1) {
            this.currentSlot += 1;
            if (this.currentSlot >= this.getMaxSlots()) {
                this.currentSlot = 0;
            }
            this.checkCurrItem();
            this.scrollCd = this.scrollMaxCd;
        } else if (vector == -1) {
            this.currentSlot -= 1;
            if (this.currentSlot < 0) {
                this.currentSlot = this.getMaxSlots() - 1;
            }
            this.checkCurrItem();
            this.scrollCd = this.scrollMaxCd;
        }
    }

    public InventoryItem getItemInSlot(int slot) {
        return this.getInventorySlots()[slot].getInventoryItem();
    }

    public void setSlotItem(int slot, InventoryItem inventoryItem) {
        if (inventoryItem != null) {
            inventoryItem.onAddInInventory(this.getOwner());
        }
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

    public void setCurrentSlot(int currentSlot) {
        this.currentSlot = currentSlot;
        this.checkCurrItem();
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
