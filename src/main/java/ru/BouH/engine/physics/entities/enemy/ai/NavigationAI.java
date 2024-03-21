package ru.BouH.engine.physics.entities.enemy.ai;

import org.joml.Vector3d;
import ru.BouH.engine.graph.Graph;
import ru.BouH.engine.physics.world.IWorld;
import ru.BouH.engine.physics.world.object.WorldItem;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class NavigationAI implements AI {
    private final Graph graph;
    private final WorldItem worldItem;
    private double currentPosDelta;
    private Graph.GVertex currentVertex;
    private Graph.GVertex nextVertex;
    private List<Graph.GVertex> pathToVertex;
    private double speed;

    public NavigationAI(double speed, WorldItem worldItem, Graph mapGraph) {
        this.speed = speed;
        this.graph = mapGraph;
        this.worldItem = worldItem;
        this.currentPosDelta = 0.0d;
        this.currentVertex = null;
        this.nextVertex = null;
        this.pathToVertex = new ArrayList<>();
    }

    public double getSpeed() {
        return this.speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public Graph.GVertex getCurrentVertex() {
        return this.currentVertex;
    }

    public Graph.GVertex getNextVertex() {
        return this.nextVertex;
    }

    public List<Graph.GVertex> getPathToVertex() {
        return this.pathToVertex;
    }

    public void setPathToVertex(List<Graph.GVertex> pathToVertex) {
        this.pathToVertex = new ArrayList<>(pathToVertex);
        Iterator<Graph.GVertex> gVertexIterator = this.pathToVertex.iterator();
        while (gVertexIterator.hasNext()) {
            Graph.GVertex c = gVertexIterator.next();
            if (!c.equals(this.getCurrentVertex())) {
                gVertexIterator.remove();
            } else {
                break;
            }
        }
    }

    @Override
    public void onUpdate(IWorld iWorld) {
        if (this.getCurrentVertex() == null) {
            this.setCurrentVertex(this.getGraph().getStart());
        }
        List<Graph.GVertex> path = this.getPathToVertex();
        if (path != null && !path.isEmpty()) {
            if (this.getNextVertex() == null) {
                if (this.getCurrentVertex().equals(path.get(0))) {
                    path.remove(0);
                }
                if (!path.isEmpty()){
                    this.nextVertex = path.get(0);
                }
            }
            if (this.getNextVertex() != null) {
                Vector3d target = new Vector3d(this.getNextVertex().getX(), this.getNextVertex().getY(), this.getNextVertex().getZ());
                Vector3d dist = new Vector3d(this.getCurrentVertex().getX(), this.getCurrentVertex().getY(), this.getCurrentVertex().getZ()).lerp(target, Math.min(this.currentPosDelta, 1.0d));
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

    public void setCurrentVertex(Graph.GVertex currentVertex) {
        this.target().setPosition(new Vector3d(currentVertex.getX(), currentVertex.getY(), currentVertex.getZ()));
        this.currentVertex = currentVertex;
    }

    public Graph getGraph() {
        return this.graph;
    }

    @Override
    public WorldItem target() {
        return this.worldItem;
    }
}
