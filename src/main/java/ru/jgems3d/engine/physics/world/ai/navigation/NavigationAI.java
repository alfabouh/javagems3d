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

package ru.jgems3d.engine.physics.world.ai.navigation;

import org.joml.Vector3f;
import ru.jgems3d.engine.physics.world.ai.AbstractAI;
import ru.jgems3d.engine.physics.world.basic.WorldItem;
import ru.jgems3d.engine.system.graph.Graph;
import ru.jgems3d.engine.system.graph.GraphVertex;
import ru.jgems3d.engine.system.map.navigation.pathfind.MapPathFinder;

import java.util.List;

public class NavigationAI<T extends WorldItem> extends AbstractAI<T> {
    protected float speed;
    protected float delta;


    protected List<GraphVertex> path;
    protected Vector3f offsetFromVertexPos;
    protected GraphVertex destination;

    protected int pathPos;
    protected GraphVertex currentVertex;

    public NavigationAI(T owner, int priority) {
        super(owner, priority);
        this.offsetFromVertexPos = new Vector3f(0.0f);
        this.speed = 0.05f;
    }

    protected List<GraphVertex> buildPath() {
        GraphVertex destination = this.getDestination();
        GraphVertex current = this.getCurrentVertex();
        if (destination == null || current == null) {
            return null;
        }
        if (destination == current) {
            return null;
        }
        Graph graph = this.getAIOwner().getWorld().getMapNavGraph();
        this.setPathPos(0);

        List<GraphVertex> vertexList = (new MapPathFinder(graph, current, destination)).findPath();
        vertexList.remove(0);
        return vertexList;
    }

    protected Vector3f getVertexPosWithOffset(GraphVertex vertex) {
        return new Vector3f(vertex.getPosition()).add(this.getOffsetFromVertexPos());
    }

    protected Vector3f getVectorWithOffset(Vector3f vector3f) {
        return new Vector3f(vector3f).add(this.getOffsetFromVertexPos());
    }

    protected void setOwnerPos(Vector3f pos) {
        this.getAIOwner().setPosition(pos);
    }

    @Override
    public void onStartAI(WorldItem worldItem) {
        if (this.currentVertex == null) {
            Graph graph = worldItem.getWorld().getMapNavGraph();
            if (graph != null) {
                this.setPathPos(0);
                this.setCurrentVertex(graph.getClosestVertex(this.getAIOwner().getPosition()));
                this.setOwnerPos(this.getVertexPosWithOffset(this.getCurrentVertex()));
            }
        }
    }

    protected void tryBuildPath() {
        if (this.getPath() == null) {
            this.path = this.buildPath();
        }
    }

    @Override
    public void onUpdateAI(WorldItem worldItem) {
       this.tryBuildPath();
        if (this.hasPath()) {
            if (this.getPathPos() >= this.getPath().size()) {
                this.clearPath();
                return;
            }

            GraphVertex nextVertex = this.getPath().get(this.getPathPos());
            Vector3f position = this.getVertexPosWithOffset(this.getCurrentVertex());
            Vector3f nextPos = this.getVertexPosWithOffset(nextVertex);

            Vector3f interPos = position.lerp(nextPos, this.delta);
            this.setOwnerPos(interPos);

            this.delta += this.getSpeed();
            if (this.delta > 1.0f) {
                this.setCurrentVertex(nextVertex);
                this.delta %= 1.0f;
                this.setPathPos(this.getPathPos() + 1);
            }
        }
    }

    @Override
    public void onEndAI(WorldItem worldItem) {

    }

    public void clearPath() {
        this.destination = null;
        this.path = null;
    }

    public void setCurrentVertex(GraphVertex currentVertex) {
        this.currentVertex = currentVertex;
    }

    protected void setPathPos(int pathPos) {
        this.pathPos = pathPos;
    }

    public void setPath(List<GraphVertex> path) {
        this.path = path;
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
        if (destination == this.getDestination()) {
            return;
        }
        this.clearPath();
        this.destination = destination;
    }

    public void setOffsetFromVertexPos(Vector3f offsetFromVertexPos) {
        this.offsetFromVertexPos = offsetFromVertexPos;
    }

    public int getPathPos() {
        return this.pathPos;
    }

    protected List<GraphVertex> getPath() {
        return this.path;
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
