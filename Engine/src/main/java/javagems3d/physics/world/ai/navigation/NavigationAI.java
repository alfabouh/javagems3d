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

package javagems3d.physics.world.ai.navigation;

import org.joml.Vector3f;
import javagems3d.physics.world.ai.AbstractAI;
import javagems3d.physics.world.basic.WorldItem;
import javagems3d.system.service.graph.Graph;
import javagems3d.system.service.graph.GraphVertex;
import javagems3d.system.navigation.pathfind.MapPathFinder;

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

    public boolean hasPath() {
        return this.getPath() != null && !this.getPath().isEmpty();
    }

    public int getPathPos() {
        return this.pathPos;
    }

    protected void setPathPos(int pathPos) {
        this.pathPos = pathPos;
    }

    protected List<GraphVertex> getPath() {
        return this.path;
    }

    public void setPath(List<GraphVertex> path) {
        this.path = path;
    }

    public Vector3f getOffsetFromVertexPos() {
        return this.offsetFromVertexPos;
    }

    public void setOffsetFromVertexPos(Vector3f offsetFromVertexPos) {
        this.offsetFromVertexPos = offsetFromVertexPos;
    }

    public GraphVertex getCurrentVertex() {
        return this.currentVertex;
    }

    public void setCurrentVertex(GraphVertex currentVertex) {
        this.currentVertex = currentVertex;
    }

    public GraphVertex getDestination() {
        return this.destination;
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

    public float getSpeed() {
        return this.speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }
}
