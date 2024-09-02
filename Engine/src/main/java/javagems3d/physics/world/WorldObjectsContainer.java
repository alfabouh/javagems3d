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

package javagems3d.physics.world;

import api.bridge.events.APIEventsLauncher;
import javagems3d.physics.world.basic.IWorldObject;
import javagems3d.physics.world.basic.IWorldTicked;
import javagems3d.physics.world.basic.WorldItem;
import javagems3d.system.inventory.IInventoryOwner;
import javagems3d.system.service.synchronizing.SyncManager;
import api.app.events.bus.Events;

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
                if (worldItem1 instanceof IInventoryOwner) {
                    ((IInventoryOwner) worldItem1).inventory().updateInventory(world);
                }
                worldItem1.setPrevPosition(worldItem1.getPosition());
            }
            if (!APIEventsLauncher.pushEvent(new Events.WorldItemUpdatePre(worldTicked)).isCancelled()) {
                worldTicked.onUpdate(world);
            }
            APIEventsLauncher.pushEvent(new Events.WorldItemUpdatePost(worldTicked));
        }
    }

    public void killItems() {
        new HashSet<>(this.getWorldObjects()).stream().filter(e -> e instanceof WorldItem).map(e -> (WorldItem) e).forEach(WorldItem::setDead);
    }

    public void cleanUp() {
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
