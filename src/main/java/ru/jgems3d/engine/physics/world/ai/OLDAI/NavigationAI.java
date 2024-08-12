package ru.jgems3d.engine.physics.world.ai.OLDAI;

import org.joml.Vector3f;
import ru.jgems3d.engine.physics.world.IWorld;
import ru.jgems3d.engine.physics.world.PhysicsWorld;
import ru.jgems3d.engine.physics.world.basic.WorldItem;
import ru.jgems3d.engine.system.graph.GraphVertex;

import java.util.ArrayList;
import java.util.List;

public class NavigationAI implements AI {
    private final PhysicsWorld world;
    private final WorldItem worldItem;
    private boolean isActive;
    private double currentPosDelta;
    private GraphVertex currentVertex;
    private GraphVertex nextVertex;
    private List<GraphVertex> pathToVertex;
    private double speed;

    public NavigationAI(double speed, WorldItem worldItem, PhysicsWorld world) {
        this.speed = speed;
        this.world = world;
        this.worldItem = worldItem;
        this.currentPosDelta = 0.0d;
        this.currentVertex = null;
        this.nextVertex = null;
        this.isActive = true;
        this.pathToVertex = new ArrayList<>();
    }

    public double getSpeed() {
        return this.speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public GraphVertex getCurrentVertex() {
        return this.currentVertex;
    }

    public void setCurrentVertex(GraphVertex currentVertex) {
        this.target().setPosition(new Vector3f(currentVertex.getPosition()));
        this.currentVertex = currentVertex;
    }

    public synchronized boolean reachedDestination() {
        if (this.getPathToVertex() == null) {
            return false;
        }
        return this.getPathToVertex().size() <= 1;
    }

    public GraphVertex getNextVertex() {
        return this.nextVertex;
    }

    public List<GraphVertex> getPathToVertex() {
        return this.pathToVertex;
    }

    public void setPathToVertex(List<GraphVertex> pathToVertex) {
        if (pathToVertex == null || pathToVertex.isEmpty()) {
            return;
        }
        this.pathToVertex = new ArrayList<>(pathToVertex);
        if (this.getNextVertex() != null) {
            this.reachedVertex();
        }
    }

    @Override
    public void onUpdate(IWorld iWorld) {
        if (this.getWorld().getMapNavGraph() == null) {
            return;
        }
        if (this.getCurrentVertex() == null) {
            this.setCurrentVertex(this.getWorld().getMapNavGraph().getClosestVertex(this.target().getPosition()));
        }
        List<GraphVertex> path = this.getPathToVertex();
        if (path != null && !path.isEmpty()) {
            if (this.getNextVertex() == null) {
                if (this.getCurrentVertex().equals(path.get(0))) {
                    path.remove(0);
                }
                if (!path.isEmpty()) {
                    this.nextVertex = path.get(0);
                }
            }
            if (this.getNextVertex() != null) {
                Vector3f target = new Vector3f(this.getNextVertex().getPosition());
                Vector3f dist = new Vector3f(this.getCurrentVertex().getPosition()).lerp(target, (float) Math.min(this.currentPosDelta, 1.0f));
                this.currentPosDelta += this.getSpeed();
                if (this.currentPosDelta >= 1.0d) {
                    this.reachedVertex();
                }
                this.target().setPosition(dist);
            }
        } else {
            this.reachedVertex();
        }
    }

    protected void reachedVertex() {
        if (this.getNextVertex() != null) {
            this.setCurrentVertex(this.getNextVertex());
        }
        this.nextVertex = null;
        this.currentPosDelta %= 1.0d;
    }

    public PhysicsWorld getWorld() {
        return this.world;
    }

    @Override
    public WorldItem target() {
        return this.worldItem;
    }

    public synchronized boolean isActive() {
        return this.isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
