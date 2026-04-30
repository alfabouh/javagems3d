package javagems3d.system.inventory;

import javagems3d.physics.world.IWorld;
import logger.Log;

@Deprecated
public interface Inventory {
    abstract class Item {
        private final String name;
        private InventoryOwner itemOwner;
        private String description;

        public Item(String name) {
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

        public void onAddInInventory(InventoryOwner hasInventory) {
            if (this.itemOwner() != null) {
                Log.get().error("Item " + this.getName() + " already exists in someone's inventory");
                return;
            }
            this.itemOwner = hasInventory;
        }

        protected InventoryOwner itemOwner() {
            return this.itemOwner;
        }

        public String getName() {
            return this.name;
        }
    }
}
