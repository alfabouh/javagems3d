package ru.jgems3d.engine.physics.world;

import ru.jgems3d.engine.inventory.IInventoryOwner;
import ru.jgems3d.engine.physics.world.basic.IWorldObject;
import ru.jgems3d.engine.physics.world.basic.IWorldTicked;
import ru.jgems3d.engine.physics.world.basic.WorldItem;
import ru.jgems3d.engine.system.synchronizing.SyncManager;
import ru.jgems3d.engine.system.exceptions.JGemsException;

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
            worldTicked.onUpdate(world);
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
        if (worldObject == null) {
            throw new JGemsException("Tried to pass NULL item in world");
        }
        worldObject.onSpawn(this.getWorld());

        this.getWorldObjects().add(worldObject);
        if (worldObject instanceof IWorldTicked) {
            this.getWorldTickedObjects().add(((IWorldTicked) worldObject));
        }
    }

    public void removeObjectFromWorld(IWorldObject worldObject) {
        if (worldObject == null) {
            throw new JGemsException("Tried to pass NULL item in world");
        }
        worldObject.onDestroy(this.getWorld());

        this.getWorldObjects().remove(worldObject);
        this.getWorldTickedObjects().remove(((IWorldTicked) worldObject));
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
