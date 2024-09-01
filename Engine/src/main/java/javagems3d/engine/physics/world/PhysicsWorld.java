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

package javagems3d.engine.physics.world;

import javagems3d.engine.JGems3D;
import javagems3d.engine.api_bridge.events.APIEventsLauncher;
import javagems3d.engine.graphics.opengl.rendering.JGemsDebugGlobalConstants;
import javagems3d.engine.physics.world.basic.IWorldObject;
import javagems3d.engine.physics.world.basic.WorldItem;
import javagems3d.engine.physics.world.thread.dynamics.DynamicsSystem;
import javagems3d.engine.system.graph.Graph;
import engine_api.events.bus.Events;

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
        APIEventsLauncher.pushEvent(new Events.PhysWorldStart(Events.Stage.PRE, this));
        this.ticks = 0;
        APIEventsLauncher.pushEvent(new Events.PhysWorldStart(Events.Stage.POST, this));
    }

    public void onWorldUpdate() {
        if (!APIEventsLauncher.pushEvent(new Events.PhysWorldTickPre(this)).isCancelled()) {
            this.getWorldObjectsContainer().onUpdate();
            this.ticks += 1;
        }
        APIEventsLauncher.pushEvent(new Events.PhysWorldTickPost(this));
    }

    public void onWorldEnd() {
        APIEventsLauncher.pushEvent(new Events.PhysWorldEnd(Events.Stage.PRE, this));
        this.removeNavGraph();
        this.cleanUp();
        APIEventsLauncher.pushEvent(new Events.PhysWorldEnd(Events.Stage.POST, this));
    }

    public void killItems() {
        this.getWorldObjectsContainer().killItems();
    }

    public void cleanUp() {
        this.getWorldObjectsContainer().cleanUp();
    }

    public void addItem(IWorldObject worldObject) {
        this.getWorldObjectsContainer().addObjectInWorld(worldObject);
        APIEventsLauncher.pushEvent(new Events.ItemSpawnedInPhysicsWorld(worldObject));
    }

    public void removeItem(IWorldObject worldObject) {
        APIEventsLauncher.pushEvent(new Events.ItemDestroyedInPhysicsWorld(worldObject));
        this.getWorldObjectsContainer().removeObjectFromWorld(worldObject);
    }

    public void removeNavGraph() {
        if (JGems3D.DEBUG_MODE) {
            if (this.mapNavGraph != null) {
                JGemsDebugGlobalConstants.linesDebugDraw.destroyNavMeshFloatBuffer();
            }
        }
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
        if (JGems3D.DEBUG_MODE) {
            if (mapNavGraph != null) {
                JGemsDebugGlobalConstants.linesDebugDraw.constructNavMeshFloatBuffer(mapNavGraph);
            }
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
        return JGems3D.get().getPhysicThreadManager().getPhysicsTimer().getDynamicsSystem();
    }
}
