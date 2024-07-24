package ru.alfabouh.jgems3d.engine.physics.world;

import ru.alfabouh.jgems3d.engine.JGems;
import ru.alfabouh.jgems3d.engine.physics.world.basic.IWorldObject;
import ru.alfabouh.jgems3d.engine.physics.world.thread.dynamics.DynamicsSystem;
import ru.alfabouh.jgems3d.engine.sysgraph.Graph;
import ru.alfabouh.jgems3d.engine.physics.world.basic.WorldItem;
import ru.alfabouh.jgems3d.engine.physics.world.thread.timer.PhysicsTimer;
import ru.alfabouh.jgems3d.logger.SystemLogging;

import java.util.*;

public final class World implements IWorld {
    private final WorldObjectsContainer worldObjectsContainer;
    private Graph mapNavGraph;
    private int ticks;

    public World() {
        this.mapNavGraph = null;
        this.worldObjectsContainer = new WorldObjectsContainer(this);
    }

    public void onWorldStart() {
        this.ticks = 0;
    }

    public void onWorldUpdate() {
        this.getWorldObjectsContainer().onUpdate();
        this.ticks += 1;
    }

    public void onWorldEnd() {
        this.cleanUp();
    }

    public void killItems() {
        this.getWorldObjectsContainer().killItems();
    }

    public void cleanUp() {
        this.getWorldObjectsContainer().cleanUp();
    }

    public void addItem(IWorldObject worldObject) {
        this.getWorldObjectsContainer().addObjectInWorld(worldObject);
    }

    public void removeItem(IWorldObject worldObject) {
        this.getWorldObjectsContainer().removeObjectFromWorld(worldObject);
    }

    public void setMapNavGraph(Graph mapNavGraph) {
        if (mapNavGraph == null) {
            SystemLogging.get().getLogManager().warn("Map Nav Mesh is NULL");
        }
        this.mapNavGraph = mapNavGraph;
    }

    public WorldItem getItemByID(int id) {
        Optional<IWorldObject> worldItem = this.getWorldObjectsContainer().getWorldObjects().stream().filter(e -> (e instanceof WorldItem) && ((WorldItem) e).getItemId() == id).findFirst();
        IWorldObject worldObject = worldItem.orElse(null);
        if (worldObject == null) {
            return null;
        }
        return (WorldItem) worldObject;
    }

    public synchronized WorldObjectsContainer getWorldObjectsContainer() {
        return this.worldObjectsContainer;
    }

    public synchronized Graph getMapNavGraph() {
        return this.mapNavGraph;
    }

    public int countItems() {
        return this.getWorldObjectsContainer().getWorldObjects().size();
    }

    public int getTicks() {
        return this.ticks;
    }

    public PhysicsTimer getPhysicsSystem() {
        return JGems.get().getPhysicThreadManager().getPhysicsTimer();
    }

    public DynamicsSystem getDynamics() {
        return this.getPhysicsSystem().getDynamicsSystem();
    }
}
