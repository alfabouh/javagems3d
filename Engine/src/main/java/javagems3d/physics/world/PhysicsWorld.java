package javagems3d.physics.world;

import api.events.EventBus;
import api.scripting.functions.APIScriptsListing;
import api.system.JGemsAPI;
import javagems3d.JGems3D;
import api.events.EventLauncher;
import javagems3d.physics.world.basic.IWorldObject;
import javagems3d.physics.world.basic.WorldItem;
import javagems3d.physics.world.thread.dynamics.DynamicsSystem;
import javagems3d.system.service.graph.Graph;

import java.util.Optional;

/**
 * In the physical world, the logic of the behavior of entities is being updated.
 */

public final class PhysicsWorld implements IWorld {
    private final WorldObjectsContainer worldObjectsContainer;
    private Graph mapNavGraph;
    private int ticks;

    public PhysicsWorld() {
        this.mapNavGraph = null;
        this.worldObjectsContainer = new WorldObjectsContainer(this);
    }

    public void onWorldStart() {
        EventLauncher.pushEvent(new EventBus.PhysicsWorldState(EventBus.State.START, this));
        this.ticks = 0;
    }

    public void onWorldUpdate() {
        if (!EventLauncher.pushEvent(new EventBus.PhysicsWorldUpdate(EventBus.Run.PRE, this)).isCancelled()) {
            this.getWorldObjectsContainer().onUpdate();
            JGemsAPI.executeScriptFunction(null, APIScriptsListing.onPhysicsWorldUpdate, JGemsAPI.getAPIScripting().getGameWorldJS());
            this.ticks += 1;
            EventLauncher.pushEvent(new EventBus.PhysicsWorldUpdate(EventBus.Run.POST, this));
        }
    }

    public void onWorldEnd() {
        EventLauncher.pushEvent(new EventBus.PhysicsWorldState(EventBus.State.END, this));
        this.removeNavGraph();
        this.clear();
    }

    public void killItems() {
        this.getWorldObjectsContainer().killItems();
    }

    public void clear() {
        this.getWorldObjectsContainer().clear();
    }

    public void addObject(IWorldObject worldObject) {
        this.getWorldObjectsContainer().addObjectInWorld(worldObject);
    }

    public void removeItem(IWorldObject worldObject) {
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
