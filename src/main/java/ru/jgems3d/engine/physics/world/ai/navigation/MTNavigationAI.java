package ru.jgems3d.engine.physics.world.ai.navigation;

import org.joml.Vector3f;
import ru.jgems3d.engine.physics.world.ai.AbstractAI;
import ru.jgems3d.engine.physics.world.basic.WorldItem;
import ru.jgems3d.engine.system.graph.Graph;
import ru.jgems3d.engine.system.graph.GraphVertex;
import ru.jgems3d.engine.system.map.navigation.pathfind.MapPathFinder;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MTNavigationAI<T extends WorldItem> extends NavigationAI<T> {
    private final ExecutorService executorService;

    public MTNavigationAI(T owner, int priority) {
        super(owner, priority);

        this.executorService = Executors.newSingleThreadExecutor();
    }

    @Override
    protected void tryBuildPath(GraphVertex vertex) {
        if (this.getPath() == null) {
            this.executorService.execute(() -> {
                
            });
        }
    }

    @Override
    public void onUpdateAI(WorldItem worldItem) {

    }
}
