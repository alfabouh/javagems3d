package javagems3d.physics.entities.collectabes;

import javagems3d.help.JGemsHelper;
import javagems3d.system.inventory.Inventory;
import javagems3d.system.inventory.InventoryOwner;
import logger.Log;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import javagems3d.audio.sound.data.SoundType;
import javagems3d.physics.entities.properties.collision.CollisionType;
import javagems3d.physics.world.IWorld;
import javagems3d.physics.world.PhysicsWorld;
import javagems3d.physics.world.basic.WorldItem;
import javagems3d.physics.world.triggers.ITriggerAction;
import javagems3d.physics.world.triggers.Zone;
import javagems3d.physics.world.triggers.zones.SimpleTriggerZone;
import javagems3d.system.resources.managing.JGemsResourceManager;

public class EntityCollectableItem extends WorldItem {
    private final SimpleTriggerZone pickUpItemTriggerZone;
    private Inventory.Item item;

    public EntityCollectableItem(PhysicsWorld world, Inventory.Item item, Vector3f pos, String itemName) {
        super(world, pos, itemName);
        this.item = item;

        this.pickUpItemTriggerZone = new SimpleTriggerZone(new Zone(this.getPosition(), new Vector3f(1.0f)));
        this.pickUpItemTriggerZone.setCollisionFilter(CollisionType.PLAYER);
        this.pickUpItemTriggerZone.setTriggerAction(this.action());
    }

    protected ITriggerAction action() {
        return (e) -> {
            if (e instanceof InventoryOwner) {
                InventoryOwner inventory = (InventoryOwner) e;
                if (inventory.getInventory().addItemInInventory(this.getInventoryItem())) {
                    JGemsHelper.get().getSoundManager().playSoundAt(JGemsResourceManager.globalSoundAssets.pick, SoundType.WORLD_SOUND, 1.5f, 1.0f, 1.0f, this.getPosition());
                    Log.get().trace("Put " + this.getInventoryItem().getName() + " in inventory");
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

    public Inventory.Item getInventoryItem() {
        return this.item;
    }

    public void setInventoryItem(@NotNull Inventory.Item item) {
        this.item = item;
    }

    public SimpleTriggerZone getPickUpItemTriggerZone() {
        return this.pickUpItemTriggerZone;
    }
}
