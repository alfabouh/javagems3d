package ru.alfabouh.jgems3d.engine.physics.objects.entities.items;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import ru.alfabouh.jgems3d.engine.JGems;
import ru.alfabouh.jgems3d.engine.audio.sound.data.SoundType;
import ru.alfabouh.jgems3d.engine.inventory.IHasInventory;
import ru.alfabouh.jgems3d.engine.inventory.items.InventoryItem;
import ru.alfabouh.jgems3d.engine.physics.triggers.Zone;
import ru.alfabouh.jgems3d.engine.physics.triggers.zones.PickUpItemTriggerZone;
import ru.alfabouh.jgems3d.engine.physics.world.IWorld;
import ru.alfabouh.jgems3d.engine.physics.world.World;
import ru.alfabouh.jgems3d.engine.physics.world.object.WorldItem;
import ru.alfabouh.jgems3d.engine.system.resources.ResourceManager;
import ru.alfabouh.jgems3d.logger.SystemLogging;

public class EntityItem extends WorldItem {
    private PickUpItemTriggerZone pickUpItemTriggerZone;
    private InventoryItem inventoryItem;

    public EntityItem(World world, @NotNull InventoryItem inventoryItem, Vector3f pos, String itemName) {
        super(world, pos, itemName);
        this.inventoryItem = inventoryItem;
    }

    public void onSpawn(IWorld iWorld) {
        super.onSpawn(iWorld);
        this.pickUpItemTriggerZone = new PickUpItemTriggerZone(new Zone(this.getPosition(), new Vector3f(1.0f)),
                                                               (e) -> {
                                                                   if (e instanceof IHasInventory) {
                                                                       IHasInventory inventory = (IHasInventory) e;
                                                                       if (inventory.inventory().addItemInInventory(this.getInventoryItem())) {
                                                                           JGems.get().getSoundManager().playLocalSound(ResourceManager.soundAssetsLoader.pick, SoundType.BACKGROUND_SOUND, 2.0f, 1.0f);
                                                                           SystemLogging.get().getLogManager().log("Put " + this.getInventoryItem().getName() + " in inventory!");
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
