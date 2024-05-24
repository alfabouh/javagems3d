package ru.alfabouh.engine.physics.entities.items;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import ru.alfabouh.engine.audio.sound.data.SoundType;
import ru.alfabouh.engine.game.Game;
import ru.alfabouh.engine.game.resources.ResourceManager;
import ru.alfabouh.engine.inventory.IHasInventory;
import ru.alfabouh.engine.inventory.items.InventoryItem;
import ru.alfabouh.engine.physics.triggers.Zone;
import ru.alfabouh.engine.physics.triggers.zones.PickUpItemTriggerZone;
import ru.alfabouh.engine.physics.world.IWorld;
import ru.alfabouh.engine.physics.world.World;
import ru.alfabouh.engine.physics.world.object.WorldItem;

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
                                                                           Game.getGame().getSoundManager().playLocalSound(ResourceManager.soundAssetsLoader.pick, SoundType.BACKGROUND_SOUND, 2.0f, 1.0f);
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
