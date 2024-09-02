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

package javagems3d.physics.world.ai.navigation;

import javagems3d.physics.world.basic.WorldItem;
import javagems3d.system.graph.Graph;
import javagems3d.system.graph.GraphVertex;
import javagems3d.system.map.navigation.pathfind.MapPathFinder;

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
