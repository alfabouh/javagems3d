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

package ru.jgems3d.engine.physics.entities.collectabes;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import ru.jgems3d.engine.JGemsHelper;
import ru.jgems3d.engine.audio.sound.data.SoundType;
import ru.jgems3d.engine.physics.entities.properties.collision.CollisionFilter;
import ru.jgems3d.engine.physics.world.IWorld;
import ru.jgems3d.engine.physics.world.PhysicsWorld;
import ru.jgems3d.engine.physics.world.basic.WorldItem;
import ru.jgems3d.engine.physics.world.triggers.ITriggerAction;
import ru.jgems3d.engine.physics.world.triggers.Zone;
import ru.jgems3d.engine.physics.world.triggers.zones.SimpleTriggerZone;
import ru.jgems3d.engine.system.inventory.IInventoryOwner;
import ru.jgems3d.engine.system.inventory.items.InventoryItem;
import ru.jgems3d.engine.system.resources.manager.JGemsResourceManager;

public class EntityCollectableItem extends WorldItem {
    private final SimpleTriggerZone pickUpItemTriggerZone;
    private InventoryItem inventoryItem;

    public EntityCollectableItem(PhysicsWorld world, InventoryItem inventoryItem, Vector3f pos, String itemName) {
        super(world, pos, itemName);
        this.inventoryItem = inventoryItem;

        this.pickUpItemTriggerZone = new SimpleTriggerZone(new Zone(this.getPosition(), new Vector3f(1.0f)));
        this.pickUpItemTriggerZone.setCollisionFilter(CollisionFilter.PLAYER);
        this.pickUpItemTriggerZone.setTriggerAction(this.action());
    }

    protected ITriggerAction action() {
        return (e) -> {
            if (e instanceof IInventoryOwner) {
                IInventoryOwner inventory = (IInventoryOwner) e;
                if (inventory.inventory().addItemInInventory(this.getInventoryItem())) {
                    JGemsHelper.getSoundManager().playSoundAt(JGemsResourceManager.globalSoundAssets.pick, SoundType.WORLD_SOUND, 1.5f, 1.0f, 1.0f, this.getPosition());
                    JGemsHelper.getLogger().log("Put " + this.getInventoryItem().getName() + " in inventory!");
                    this.setDead();
                }
            }
        };
    }

    @Override
    public void setPosition(Vector3f vector3f) {
        super.setPosition(vector3f);
        this.getPickUpItemTriggerZone().setLocation(vector3f);
    }

    public void onSpawn(IWorld iWorld) {
        super.onSpawn(iWorld);
        this.getPickUpItemTriggerZone().onSpawn(iWorld);
    }

    public void onDestroy(IWorld iWorld) {
        super.onDestroy(iWorld);
        this.getPickUpItemTriggerZone().onDestroy(iWorld);
    }

    public InventoryItem getInventoryItem() {
        return this.inventoryItem;
    }

    public void setInventoryItem(@NotNull InventoryItem inventoryItem) {
        this.inventoryItem = inventoryItem;
    }

    public SimpleTriggerZone getPickUpItemTriggerZone() {
        return this.pickUpItemTriggerZone;
    }
}
