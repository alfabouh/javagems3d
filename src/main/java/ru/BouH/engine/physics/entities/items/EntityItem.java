package ru.BouH.engine.physics.entities.items;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import ru.BouH.engine.audio.sound.data.SoundType;
import ru.BouH.engine.game.Game;
import ru.BouH.engine.game.resources.ResourceManager;
import ru.BouH.engine.inventory.IHasInventory;
import ru.BouH.engine.inventory.items.InventoryItem;
import ru.BouH.engine.physics.triggers.Zone;
import ru.BouH.engine.physics.triggers.zones.PickUpItemTriggerZone;
import ru.BouH.engine.physics.world.IWorld;
import ru.BouH.engine.physics.world.World;
import ru.BouH.engine.physics.world.object.WorldItem;

public class EntityItem extends WorldItem {
    private PickUpItemTriggerZone pickUpItemTriggerZone;
    private InventoryItem inventoryItem;

    public EntityItem(World world, @NotNull InventoryItem inventoryItem, Vector3d pos, String itemName) {
        super(world, pos, itemName);
        this.inventoryItem = inventoryItem;
    }

    public void onSpawn(IWorld iWorld) {
        super.onSpawn(iWorld);
        this.pickUpItemTriggerZone = new PickUpItemTriggerZone(new Zone(this.getPosition(), new Vector3d(1.0d)),
                                                               (e) -> {
                                                                   if (e instanceof IHasInventory) {
                                                                       IHasInventory inventory = (IHasInventory) e;
                                                                       if (inventory.inventory().addItemInInventory(this.getInventoryItem())) {
                                                                           Game.getGame().getSoundManager().playLocalSound(ResourceManager.soundAssetsLoader.pick, SoundType.BACKGROUND_SOUND, 1.0f, 1.0f);
                                                                           Game.getGame().getLogManager().log("Put " + this.getInventoryItem().getName() + " in inventory!");
                                                                           this.setDead();
                                                                       }
                                                                   }
                                                               });
        this.getWorld().addTriggerZone(this.getPickUpItemTriggerZone());
    }

    public void onDestroy(IWorld iWorld) {
        super.onDestroy(iWorld);
        this.getWorld().removeTriggerZone(this.getPickUpItemTriggerZone());
    }

    public InventoryItem getInventoryItem() {
        return this.inventoryItem;
    }

    public void setInventoryItem(@NotNull InventoryItem inventoryItem) {
        this.inventoryItem = inventoryItem;
    }

    public PickUpItemTriggerZone getPickUpItemTriggerZone() {
        return this.pickUpItemTriggerZone;
    }
}
