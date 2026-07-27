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

import javagems3d.physics.world.basic.WorldItem;
import javagems3d.system.service.graph.Graph;
import javagems3d.system.service.graph.GraphVertex;
import javagems3d.system.navigation.pathfind.MapPathFinder;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class MTNavigationAI<T extends WorldItem> extends NavigationAI<T> {
    private final ExecutorService executorService;
    private final AtomicBoolean block;


    public MTNavigationAI(T owner, int priority) {
        super(owner, priority);
        this.executorService = Executors.newSingleThreadExecutor();
        this.block = new AtomicBoolean();
    }

    protected void buildPathMT() {
        final GraphVertex destination = this.getDestination();
        final GraphVertex current = this.getCurrentVertex();
        if (destination == null || current == null) {
            return;
        }
        if (destination == current) {
            return;
        }
        Graph graph = this.getAIOwner().getWorld().getMapNavGraph();
        this.setPathPos(0);
        this.executorService.execute(() -> {
            this.block.set(true);
            List<GraphVertex> vertexList = (new MapPathFinder(graph, current, destination)).findPath();
            vertexList.remove(0);
            this.setPath(vertexList);
            this.block.set(false);
        });
    }

    protected void tryBuildPath() {
        if (this.block.get()) {
            return;
        }
        if (this.getPath() == null) {
            this.buildPathMT();
        }
    }

    @Override
    public void onEndAI(WorldItem worldItem) {
        super.onEndAI(worldItem);
        this.executorService.shutdown();
    }

    @Override
    public void onUpdateAI(WorldItem worldItem) {
        super.onUpdateAI(worldItem);
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

    protected List<GraphVertex> getPath() {
        synchronized (this) {
            return super.getPath();
        }
    }

    public void setPath(List<GraphVertex> path) {
        synchronized (this) {
            super.setPath(path);
        }
    }
}
