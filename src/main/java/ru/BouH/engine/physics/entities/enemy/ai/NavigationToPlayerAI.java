package ru.BouH.engine.physics.entities.enemy.ai;

import org.bytedeco.bullet.BulletCollision.btBroadphaseProxy;
import org.bytedeco.bullet.BulletCollision.btCollisionWorld;
import org.bytedeco.bullet.LinearMath.btVector3;
import org.joml.Vector3d;
import ru.BouH.engine.game.Game;
import ru.BouH.engine.graph.Graph;
import ru.BouH.engine.graph.pathfind.AStar;
import ru.BouH.engine.physics.entities.BodyGroup;
import ru.BouH.engine.physics.entities.player.IPlayer;
import ru.BouH.engine.physics.world.IWorld;
import ru.BouH.engine.physics.world.World;
import ru.BouH.engine.physics.world.object.WorldItem;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class NavigationToPlayerAI extends NavigationAI {
    private final Vector3d playerPos;
    private final List<Graph.GVertex> queuePath;
    private final AtomicBoolean atomicBoolean;
    private IPlayer player;
    private int ticksBeforeRefreshPath;
    private int aggressionLevel;
    private int aggressionTick;
    private int randomTick;
    private int memory;

    public NavigationToPlayerAI(double speed, WorldItem worldItem, final World world) {
        super(speed, worldItem, world);
        this.queuePath = new ArrayList<>();
        this.atomicBoolean = new AtomicBoolean(false);
        this.playerPos = new Vector3d(0.0d);
        this.setAggressionLevel(5);
        Thread seekPathThread = new Thread(() -> {
            int i = 0;
            Graph.GVertex randomVertex = world.getGraph() == null ? null : world.getGraph().getRandomVertex();
            while (Game.getGame().isCurrentMapIsValid()) {
                try {
                    if (worldItem.getWorld().getGraph() == null || this.getCurrentSyncVertex() == null) {
                        continue;
                    }
                    if (randomVertex == null) {
                        randomVertex = worldItem.getWorld().getGraph().getRandomVertex();
                    }
                    AStar aStar = new AStar(worldItem.getWorld().getGraph(), this.getCurrentSyncVertex(), this.getAtomicBoolean().get() ? this.findClosestPlayerVertex(worldItem.getWorld().getGraph()) : randomVertex);
                    List<Graph.GVertex> path = aStar.findPath();
                    this.getQueuePath().clear();
                    this.getQueuePath().addAll(path);
                    if ((i++ >= 200 && Game.random.nextBoolean())) {
                        randomVertex = worldItem.getWorld().getGraph().getRandomVertex();
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

    @Override
    public void onUpdate(IWorld iWorld) {
        super.onUpdate(iWorld);
        if (this.getPlayer() != null) {
            WorldItem player = (WorldItem) this.getPlayer();
            if (this.aggressionTick++ >= (40 - this.getAggressionLevel())) {
                if (Game.random.nextFloat() <= 0.01 * this.getAggressionLevel()) {
                    this.randomTick = (this.getAggressionLevel() + 1) * 30;
                }
                this.aggressionTick = 0;
            }
            if (this.ticksBeforeRefreshPath++ >= Math.min(this.target().getPosition().distance(((WorldItem) this.getPlayer()).getPosition()), 30)) {
                this.getPlayerPos().set(player.getPosition());
                this.ticksBeforeRefreshPath = 0;
            }
            this.updateNavOnAggression(player);
        }
    }

    protected void reachedVertex() {
        super.reachedVertex();
        this.setPathToVertex(this.getQueuePath());
    }

    private void updateNavOnAggression(WorldItem worldItem) {
        if (this.memory-- > 0 || this.randomTick-- > 0 || (this.distanceToPlayer(worldItem) <= (this.getAggressionLevel() * 3))) {
            this.getAtomicBoolean().set(true);
            return;
        }
        if (this.distanceToPlayer(worldItem) <= (this.getAggressionLevel() * 6) && this.canSeePlayer(worldItem)) {
            this.memory = 30 * this.getAggressionLevel();
            this.getAtomicBoolean().set(true);
            return;
        }
        this.getAtomicBoolean().set(false);
    }

    private double distanceToPlayer(WorldItem worldItem) {
        return this.target().getPosition().distance(worldItem.getPosition());
    }

    private boolean canSeePlayer(WorldItem worldItem) {
        btVector3 va1 = new btVector3(this.target().getPosition().x, this.target().getPosition().y, this.target().getPosition().z);
        btVector3 va2 = new btVector3(worldItem.getPosition().x, worldItem.getPosition().y, worldItem.getPosition().z);

        boolean flag = true;

        btCollisionWorld.ClosestRayResultCallback rayResultCallback = new btCollisionWorld.ClosestRayResultCallback(va1, va2);
        rayResultCallback.m_collisionFilterMask(btBroadphaseProxy.DefaultFilter & ~BodyGroup.PlayerFilter & ~BodyGroup.LiquidFilter & ~BodyGroup.GhostFilter);
        this.getWorld().getDynamicsWorld().rayTest(va1, va2, rayResultCallback);

        if (rayResultCallback.hasHit()) {
            flag = false;
        }

        va1.deallocate();
        va2.deallocate();
        rayResultCallback.deallocate();

        return flag;
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

    public int getAggressionLevel() {
        return this.aggressionLevel;
    }

    public void setAggressionLevel(int aggressionLevel) {
        this.aggressionLevel = aggressionLevel;
        this.setSpeed(0.1d + this.getAggressionLevel() * 0.01d);
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

    public final synchronized IPlayer getPlayer() {
        return this.player;
    }

    public void setPlayer(IPlayer player) {
        this.player = player;
    }
}
