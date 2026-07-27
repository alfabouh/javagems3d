/*
 *
 *  * javagems3d
 *  * Copyright (C) 2026 gltexture
 *  *
 *  * This program is free software: you can redistribute it and/or modify
 *  * it under the terms of the GNU General Public License as published by
 *  * the Free Software Foundation, either version 3 of the License, or
 *  * (at your option) any later version.
 *  *
 *  * This program is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  * GNU General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU General Public License
 *  * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 */

package javagems3d.physics.world;

import api.events.EventLauncher;
import api.events.EventBus;
import javagems3d.physics.world.basic.IWorldObject;
import javagems3d.physics.world.basic.IWorldTicked;
import javagems3d.physics.world.basic.WorldItem;
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
            if (worldTicked instanceof WorldItem worldItem1) {
                worldItem1.setPrevPosition(worldItem1.getPosition());
            }
            worldTicked.onUpdate(world);
        }
    }

    public void killItems() {
        new HashSet<>(this.getWorldObjects()).stream().filter(e -> e instanceof WorldItem).map(e -> (WorldItem) e).forEach(WorldItem::setDead);
    }

    public void clear() {
        this.getWorldObjects().forEach(e -> e.onDestroy(this.getWorld()));
        this.getWorldObjects().clear();
        this.getWorldTickedObjects().clear();
        WorldItem.globalId = 0;
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
