package ru.alfabouh.jgems3d.engine.physics.world;

import ru.alfabouh.jgems3d.engine.inventory.IInventoryOwner;
import ru.alfabouh.jgems3d.engine.physics.world.basic.IWorldObject;
import ru.alfabouh.jgems3d.engine.physics.world.basic.IWorldTicked;
import ru.alfabouh.jgems3d.engine.physics.world.basic.WorldItem;
import ru.alfabouh.jgems3d.engine.system.exception.JGemsException;

import java.util.HashSet;
import java.util.Set;

public final class WorldObjectsContainer {
    private final Set<IWorldObject> worldObjects;
    private final Set<IWorldTicked> worldTickedObjects;
    private final World world;

    public WorldObjectsContainer(World world) {
        this.world = world;

        this.worldObjects = new HashSet<>();
        this.worldTickedObjects = new HashSet<>();
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

    public World getWorld() {
        return this.world;
    }
}
