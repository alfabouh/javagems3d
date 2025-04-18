package javagems3d.physics.world;

import api.events.EventLauncher;
import api.events.EventBus;
import javagems3d.physics.world.basic.IWorldObject;
import javagems3d.physics.world.basic.IWorldTicked;
import javagems3d.physics.world.basic.WorldItem;
import javagems3d.system.inventory.InventoryOwner;
import javagems3d.system.service.synchronizing.SyncManager;

import java.util.HashSet;
import java.util.Set;

public final class WorldObjectsContainer {
    private final Set<IWorldObject> worldObjects;
    private final Set<IWorldTicked> worldTickedObjects;
    private final PhysicsWorld world;

    public WorldObjectsContainer(PhysicsWorld world) {
        this.world = world;

        this.worldObjects = SyncManager.createSyncronisedSet();
        this.worldTickedObjects = SyncManager.createSyncronisedSet();
    }

    public void onUpdate() {
        for (IWorldTicked worldTicked : this.getWorldTickedObjects()) {
            if (worldTicked instanceof WorldItem) {
                WorldItem worldItem1 = (WorldItem) worldTicked;
                if (worldItem1 instanceof InventoryOwner) {
                    ((InventoryOwner) worldItem1).getInventory().updateInventory(world);
                }
                worldItem1.setPrevPosition(worldItem1.getPosition());
            }
            if (!EventLauncher.pushEvent(new EventBus.WorldItemUpdatePre(worldTicked)).isCancelled()) {
                worldTicked.onUpdate(world);
            }
            EventLauncher.pushEvent(new EventBus.WorldItemUpdatePost(worldTicked));
        }
    }

    public void killItems() {
        new HashSet<>(this.getWorldObjects()).stream().filter(e -> e instanceof WorldItem).map(e -> (WorldItem) e).forEach(WorldItem::setDead);
    }

    public void clear() {
        this.getWorldObjects().forEach(e -> e.onDestroy(this.getWorld()));
        this.getWorldObjects().clear();
        this.getWorldTickedObjects().clear();
    }

    public void addObjectInWorld(IWorldObject worldObject) {
        worldObject.onSpawn(this.getWorld());
        this.getWorldObjects().add(worldObject);
        if (worldObject instanceof IWorldTicked) {
            this.getWorldTickedObjects().add(((IWorldTicked) worldObject));
        }
    }

    public void removeObjectFromWorld(IWorldObject worldObject) {
        worldObject.onDestroy(this.getWorld());
        this.getWorldObjects().remove(worldObject);
        if (worldObject instanceof IWorldTicked) {
            this.getWorldTickedObjects().remove(((IWorldTicked) worldObject));
        }
    }

    public Set<IWorldObject> getWorldObjects() {
        return this.worldObjects;
    }

    public Set<IWorldTicked> getWorldTickedObjects() {
        return this.worldTickedObjects;
    }

    public PhysicsWorld getWorld() {
        return this.world;
    }
}
