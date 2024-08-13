package ru.jgems3d.engine.physics.world.ai.navigation;

import ru.jgems3d.engine.physics.world.ai.AbstractAI;
import ru.jgems3d.engine.physics.world.basic.WorldItem;
import ru.jgems3d.engine.system.graph.Graph;
import ru.jgems3d.engine.system.graph.GraphVertex;
import ru.jgems3d.engine.system.map.navigation.pathfind.MapPathFinder;

import java.util.List;

public class NavigationAI<T extends WorldItem> extends AbstractAI<T> {
    private float speed;
    private float delta;
    private List<GraphVertex> path;
    private GraphVertex destination;

    private GraphVertex nextVertex;
    private GraphVertex currentVertex;

    public NavigationAI(T owner, int priority) {
        super(owner, priority);
    }

    private List<GraphVertex> buildPath(GraphVertex vertex) {
        if (vertex == null) {
            return null;
        }
        Graph graph = this.getAIOwner().getWorld().getMapNavGraph();
        return (new MapPathFinder(graph, this.getCurrentVertex(), vertex)).findPath();
    }

    @Override
    public void onStartAI(WorldItem worldItem) {
        if (this.currentVertex == null) {
            Graph graph = worldItem.getWorld().getMapNavGraph();
            if (graph != null) {
                this.currentVertex = graph.getClosestVertex(worldItem.getPosition());
            }
        }
    }

    @Override
    public void onUpdateAI(WorldItem worldItem) {
        if (this.path == null) {
            this.path = this.buildPath(this.getDestination());
        }
        if (this.hasPath()) {

        }
    }

    @Override
    public void onEndAI(WorldItem worldItem) {

    }

    public List<GraphVertex> getPath() {
        return this.path;
    }

    public boolean hasPath() {
        return this.getPath() != null && !this.getPath().isEmpty();
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public void setDestination(GraphVertex destination) {
        this.destination = destination;
    }

    public GraphVertex getCurrentVertex() {
        return this.currentVertex;
    }

    public GraphVertex getDestination() {
        return this.destination;
    }

    public float getSpeed() {
        return this.speed;
    }
}
