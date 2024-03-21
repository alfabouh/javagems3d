package ru.BouH.engine.physics.entities.enemy.ai;

import org.checkerframework.checker.units.qual.A;
import org.joml.Vector3d;
import ru.BouH.engine.game.Game;
import ru.BouH.engine.graph.Graph;
import ru.BouH.engine.graph.pathfind.AStar;
import ru.BouH.engine.physics.entities.player.IPlayer;
import ru.BouH.engine.physics.world.IWorld;
import ru.BouH.engine.physics.world.object.WorldItem;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class NavigationToPlayerAI extends NavigationAI {
    private IPlayer player;
    private final Vector3d playerPos;
    private int ticksBeforeRefreshPath;
    private final List<Graph.GVertex> queuePath;
    private final AtomicBoolean atomicBoolean;

    public NavigationToPlayerAI(double speed, WorldItem worldItem, Graph mapGraph) {
        super(speed, worldItem, mapGraph);
        this.queuePath = new ArrayList<>();
        final Graph graph = worldItem.getWorld().getGraph();
        this.atomicBoolean = new AtomicBoolean(true);
        this.playerPos = new Vector3d(0.0d);
        Thread seekPathThread = new Thread(() -> {
            int i = 0;
            Graph.GVertex randomVertex = graph.getRandomVertex();
            while (true) {
                try {
                    if (this.getCurrentSyncVertex() == null) {
                        continue;
                    }
                    AStar aStar = new AStar(graph, this.getCurrentSyncVertex(), this.getAtomicBoolean().get() ? this.findClosestPlayerVertex(graph) : randomVertex);
                    List<Graph.GVertex> path = aStar.findPath();
                    this.getQueuePath().clear();
                    this.getQueuePath().addAll(path);
                    if (i++ >= 200 && Game.random.nextBoolean()) {
                        randomVertex = graph.getRandomVertex();
                        i = 0;
                    }
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        seekPathThread.setDaemon(true);
        seekPathThread.setName("PathThread");
        seekPathThread.start();
    }

    protected void reachedVertex() {
        super.reachedVertex();
        this.setPathToVertex(this.getQueuePath());
    }

    @Override
    public void onUpdate(IWorld iWorld) {
        super.onUpdate(iWorld);
        if (this.getPlayer() != null) {
            WorldItem player = (WorldItem) this.getPlayer();
            if (this.ticksBeforeRefreshPath++ >= Math.min(this.target().getPosition().distance(((WorldItem) this.getPlayer()).getPosition()), 50)) {
                this.getAtomicBoolean().set(true);
                this.getPlayerPos().set(player.getPosition());
                this.ticksBeforeRefreshPath = 0;
            }
        }
    }

    private Graph.GVertex findClosestPlayerVertex(Graph graph) {
        Graph.GVertex closest = graph.getStart();
        double closestDist = this.getPlayerPos().distance(closest.getX(), closest.getY(), closest.getZ());
        for (Graph.GVertex vertex : graph.getGraphContainer().keySet()) {
            double currDist = this.getPlayerPos().distance(vertex.getX(), vertex.getY(), vertex.getZ());
            if (currDist < closestDist) {
                closest = vertex;
                closestDist = currDist;
            }
        }
        return closest;
    }

    private synchronized Vector3d getPlayerPos() {
        return this.playerPos;
    }

    private AtomicBoolean getAtomicBoolean() {
        return this.atomicBoolean;
    }

    private synchronized Graph.GVertex getCurrentSyncVertex() {
        return this.getCurrentVertex();
    }

    private synchronized List<Graph.GVertex> getQueuePath() {
        return this.queuePath;
    }

    public void setPlayer(IPlayer player) {
        this.player = player;
    }

    public final synchronized IPlayer getPlayer() {
        return this.player;
    }
}
