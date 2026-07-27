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

import api.events.EventBus;
import api.scripting.coding.env.internal.game.init.events.physics.*;
import api.scripting.coding.env.internal.util.events.JSEventRun;
import api.scripting.coding.env.internal.util.events.JSEventState;
import api.scripting.coding.env.internal.util.world.physical.JSPhysicsWorld;
import api.scripting.JavaToJsAPI;
import javagems3d.JGems3D;
import api.events.EventLauncher;
import javagems3d.physics.world.basic.IWorldObject;
import javagems3d.physics.world.basic.WorldItem;
import javagems3d.physics.world.thread.dynamics.DynamicsSystem;
import javagems3d.system.core.transmitter.ThreadActionsTransmitter;
import javagems3d.system.core.transmitter.actions.Physics_Render__Action;
import javagems3d.system.core.transmitter.actions.Render_Physics__Action;
import javagems3d.system.service.collections.Pair;
import javagems3d.system.service.graph.Graph;

import java.util.Optional;

public final class PhysicsWorld implements IWorld {
    private final WorldObjectsContainer worldObjectsContainer;
    private Graph mapNavGraph;
    private int ticks;

    public PhysicsWorld() {
        this.mapNavGraph = null;
        this.worldObjectsContainer = new WorldObjectsContainer(this);
    }

    public void onWorldStart() {
        EventBus.PhysicsWorldStateEvent event = new EventBus.PhysicsWorldStateEvent(EventBus.State.START, this);
        EventLauncher.pushEvent(event, new Pair<>(new JSPhysicsWorldStateEvent(new JSPhysicsWorld(this), JSEventState.START), JavaToJsAPI.Target.Game));
        if (event.isCancelled()) {
            return;
        }
        this.ticks = 0;
    }

    public void onWorldUpdate() {
        {
            Render_Physics__Action action;
            while ((action = ThreadActionsTransmitter.INSTANCE.getActions__RENDER_TO_PHYSICS().poll()) != null) {
                action.action(this);
            }
        }
        {
            EventBus.PhysicsWorldUpdateEvent preEvent = new EventBus.PhysicsWorldUpdateEvent(EventBus.Run.PRE, this);
            EventLauncher.pushEvent(preEvent, new Pair<>(new JSPhysicsWorldUpdateEvent(new JSPhysicsWorld(this), JSEventRun.PRE), JavaToJsAPI.Target.Game));
            if (!preEvent.isCancelled()) {
                this.getWorldObjectsContainer().onUpdate();
                this.ticks += 1;
            }
            EventBus.PhysicsWorldUpdateEvent postEvent = new EventBus.PhysicsWorldUpdateEvent(EventBus.Run.POST, this);
            EventLauncher.pushEvent(postEvent, new Pair<>(new JSPhysicsWorldUpdateEvent(new JSPhysicsWorld(this), JSEventRun.POST), JavaToJsAPI.Target.Game));
        }
    }

    public void onWorldEnd() {
        EventBus.PhysicsWorldStateEvent event = new EventBus.PhysicsWorldStateEvent(EventBus.State.END, this);
        EventLauncher.pushEvent(event, new Pair<>(new JSPhysicsWorldStateEvent(new JSPhysicsWorld(this), JSEventState.END), JavaToJsAPI.Target.Game));
        if (event.isCancelled()) {
            return;
        }
        this.removeNavGraph();
        this.clear();
    }

    public void killItems() {
        this.getWorldObjectsContainer().killItems();
    }

    public void clear() {
        EventBus.PhysicsWorldClearEvent event = new EventBus.PhysicsWorldClearEvent(this);
        EventLauncher.pushEvent(event, new Pair<>(new JSPhysicsWorldClearEvent(new JSPhysicsWorld(this)), JavaToJsAPI.Target.Game));
        if (event.isCancelled()) {
            return;
        }
        this.getWorldObjectsContainer().clear();
    }

    public void addObject(IWorldObject worldObject) {
        EventBus.PhysicsWorldObjectAddEvent event = new EventBus.PhysicsWorldObjectAddEvent(this, worldObject);
        EventLauncher.pushEvent(event, new Pair<>(new JSPhysicsWorldObjectAddEvent(new JSPhysicsWorld(this), () -> worldObject), JavaToJsAPI.Target.Game));
        if (event.isCancelled()) {
            return;
        }
        this.getWorldObjectsContainer().addObjectInWorld(worldObject);
    }

    public void removeObject(IWorldObject worldObject) {
        EventBus.PhysicsWorldObjectRemoveEvent event = new EventBus.PhysicsWorldObjectRemoveEvent(this, worldObject);
        EventLauncher.pushEvent(event, new Pair<>(new JSPhysicsWorldObjectRemoveEvent(new JSPhysicsWorld(this), () -> worldObject), JavaToJsAPI.Target.Game));
        if (event.isCancelled()) {
            return;
        }
        this.getWorldObjectsContainer().removeObjectFromWorld(worldObject);
    }

    public void removeNavGraph() {
        synchronized (this) {
            this.mapNavGraph = null;
        }
    }

    public Graph getMapNavGraph() {
        synchronized (this) {
            return this.mapNavGraph;
        }
    }

    public void setMapNavGraph(Graph mapNavGraph) {
        synchronized (this) {
            this.mapNavGraph = mapNavGraph;
        }
    }

    public WorldItem getItemByID(int id) {
        Optional<IWorldObject> worldItem = this.getWorldObjectsContainer().getWorldObjects().stream().filter(e -> (e instanceof WorldItem) && ((WorldItem) e).getItemId() == id).findFirst();
        IWorldObject worldObject = worldItem.orElse(null);
        if (worldObject == null) {
            return null;
        }
        return (WorldItem) worldObject;
    }

    public WorldObjectsContainer getWorldObjectsContainer() {
        return this.worldObjectsContainer;
    }

    public int countItems() {
        return this.getWorldObjectsContainer().getWorldObjects().size();
    }

    public int getTicks() {
        return this.ticks;
    }

    public DynamicsSystem getDynamics() {
        return JGems3D.get().getPhysics().getPhysicsProcessor().getDynamicsSystem();
    }

    public boolean contains(WorldItem worldItem) {
        return this.getWorldObjectsContainer().getWorldObjects().contains(worldItem);
    }
}
