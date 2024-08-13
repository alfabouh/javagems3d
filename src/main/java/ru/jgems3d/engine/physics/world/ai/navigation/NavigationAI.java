package ru.jgems3d.engine.physics.world.ai.navigation;

import org.joml.Vector3f;
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
    private Vector3f offsetFromVertexPos;
    private GraphVertex destination;

    private int pathPos;
    private GraphVertex currentVertex;

    public NavigationAI(T owner, int priority) {
        super(owner, priority);
        this.offsetFromVertexPos = new Vector3f(0.0f);
        this.speed = 0.05f;
    }

    private List<GraphVertex> buildPath(GraphVertex destination) {
        if (destination == null || this.getCurrentVertex() == null) {
            return null;
        }
        if (destination == this.getCurrentVertex()) {
            return null;
        }
        Graph graph = this.getAIOwner().getWorld().getMapNavGraph();
        this.pathPos = 0;
        return (new MapPathFinder(graph, this.getCurrentVertex(), destination)).findPath();
    }

    private Vector3f getVertexPosWithOffset(GraphVertex vertex) {
        return new Vector3f(vertex.getPosition()).add(this.getOffsetFromVertexPos());
    }

    private Vector3f getVectorWithOffset(Vector3f vector3f) {
        return new Vector3f(vector3f).add(this.getOffsetFromVertexPos());
    }

    private void setOwnerPos(Vector3f pos) {
        this.getAIOwner().setPosition(pos);
    }

    @Override
    public void onStartAI(WorldItem worldItem) {
        if (this.currentVertex == null) {
            Graph graph = worldItem.getWorld().getMapNavGraph();
            if (graph != null) {
                this.pathPos = 0;
                this.currentVertex = graph.getRandomVertex();
                this.setOwnerPos(this.getVertexPosWithOffset(this.getCurrentVertex()));
            }
        }
    }

    @Override
    public void onUpdateAI(WorldItem worldItem) {
        if (this.getPath() == null) {
            this.path = this.buildPath(this.getDestination());
        }
        if (this.hasPath()) {
            if (this.pathPos >= this.getPath().size()) {
                this.clearPath();
                return;
            }

            GraphVertex nextVertex = this.getPath().get(this.pathPos);
            Vector3f position = this.getVertexPosWithOffset(this.getCurrentVertex());
            Vector3f nextPos = this.getVertexPosWithOffset(nextVertex);
            
            Vector3f interPos = position.lerp(nextPos, this.delta);
            this.setOwnerPos(interPos);

            this.speed = 0.1f;
            this.delta += this.getSpeed();
            if (this.delta > 1.0f) {
                this.currentVertex = nextVertex;
                this.delta %= 1.0f;
                this.pathPos += 1;
            }
        }
    }

    @Override
    public void onEndAI(WorldItem worldItem) {

    }

    public void clearPath() {
        this.path = null;
    }

    protected List<GraphVertex> getPath() {
        return this.path;
    }

    public boolean hasPath() {
        return this.getPath() != null && !this.getPath().isEmpty();
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public void setDestination(WorldItem worldItem) {
        GraphVertex graphVertex = worldItem.getWorld().getMapNavGraph().getClosestVertex(worldItem.getPosition());
        this.setDestination(graphVertex);
    }

    public void setDestination(GraphVertex destination) {
        this.clearPath();
        this.destination = destination;
    }

    public void setOffsetFromVertexPos(Vector3f offsetFromVertexPos) {
        this.offsetFromVertexPos = offsetFromVertexPos;
    }

    public Vector3f getOffsetFromVertexPos() {
        return this.offsetFromVertexPos;
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
