package ru.jgems3d.engine.physics.entities.enemies.ai;

import org.joml.Vector3f;
import ru.jgems3d.engine.JGems3D;
import ru.jgems3d.engine.physics.world.PhysicsWorld;
import ru.jgems3d.engine.system.graph.Graph;
import ru.jgems3d.engine.system.graph.GraphVertex;
import ru.jgems3d.engine.system.map.navigation.pathfind.MapPathFinder;
import ru.jgems3d.engine.physics.entities.player.Player;
import ru.jgems3d.engine.physics.world.IWorld;
import ru.jgems3d.engine.physics.world.basic.WorldItem;
import ru.jgems3d.engine.JGemsHelper;
import ru.jgems3d.engine.system.service.exceptions.JGemsRuntimeException;
import ru.jgems3d.logger.managers.JGemsLogging;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class NavigationToPlayerAI extends NavigationAI {
    private final Vector3f playerPos;
    private final List<GraphVertex> queuePath;
    private final AtomicBoolean atomicBoolean;
    private boolean forceChangeWayDirection;
    private Player player;
    private float maxSpeed;
    private int currentMemory;
    private int currentChasingTicks;
    private int chasingBlockTicks;
    private int maxMemory;
    private float maxSeekDist;
    private int ticksBeforeRefreshPath;
    private float randomPlayerChasePercent;

    public NavigationToPlayerAI(double speed, WorldItem worldItem, final PhysicsWorld world) {
        super(speed, worldItem, world);
        this.queuePath = new ArrayList<>();
        this.atomicBoolean = new AtomicBoolean(false);
        this.playerPos = new Vector3f(0.0f);

        Thread seekPathThread = new Thread(() -> {
            try {
                GraphVertex randomVertex = world.getMapNavGraph() == null ? null : world.getMapNavGraph().getRandomVertex();
                while (JGems3D.get().isCurrentMapIsValid()) {
                    try {
                        if (worldItem.getWorld().getMapNavGraph() == null || this.getCurrentSyncVertex() == null || !this.isActive()) {
                            continue;
                        }

                        this.setSpeed(this.getAtomicBoolean().get() ? this.getMaxSpeed() : 0.125f);

                        if ((!this.getAtomicBoolean().get() && this.reachedDestination()) || this.isForceChangeWayDirection()) {
                            randomVertex = JGems3D.random.nextFloat() <= this.getRandomPlayerChasePercent() ? this.findClosestPlayerVertex(worldItem.getWorld().getMapNavGraph()) : world.getMapNavGraph().getRandomVertex();
                            this.setForceChangeWayDirection(false);
                        }

                        MapPathFinder mapPathFinder = new MapPathFinder(worldItem.getWorld().getMapNavGraph(), this.getCurrentSyncVertex(), this.getAtomicBoolean().get() ? this.findClosestPlayerVertex(worldItem.getWorld().getMapNavGraph()) : randomVertex);
                        List<GraphVertex> path = mapPathFinder.findPath();
                        this.getQueuePath().clear();

                        if (path != null) {
                            this.getQueuePath().addAll(path);
                        } else {
                            JGemsHelper.getLogger().warn("Nav pathfind problems!");
                        }

                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        throw new JGemsRuntimeException(e);
                    }
                }
            } catch (Exception e) {
                JGemsHelper.getLogger().exception(e);
                JGemsLogging.showExceptionDialog("An service occurred inside the system. Open the logs folder for details.");
            }
        });
        seekPathThread.setDaemon(true);
        seekPathThread.setName("PathThread");
        seekPathThread.start();
    }

    private void refreshPath() {
        if (this.getQueuePath() != null && this.getNextVertex() == null) {
            ArrayList<GraphVertex> gVertices = new ArrayList<>(this.getQueuePath());
            if (!gVertices.isEmpty()) {
                if (gVertices.get(0) != null && !gVertices.get(0).equals(this.getCurrentVertex())) {
                    Iterator<GraphVertex> gVertexIterator = gVertices.iterator();
                    while (gVertexIterator.hasNext()) {
                        GraphVertex vertex = gVertexIterator.next();
                        if (!vertex.equals(this.getCurrentVertex())) {
                            gVertexIterator.remove();
                        } else {
                            break;
                        }
                    }
                }
                this.setPathToVertex(gVertices);
            }
        }
    }

    @Override
    public void onUpdate(IWorld iWorld) {
        super.onUpdate(iWorld);
        this.refreshPath();
        if (this.getPlayer() != null) {
            WorldItem player = (WorldItem) this.getPlayer();
            double distToPlayer = this.distanceToPlayer(player);

            if (this.ticksBeforeRefreshPath++ >= Math.min(distToPlayer, 30)) {
                this.getPlayerPos().set(player.getPosition());
                this.ticksBeforeRefreshPath = 0;
            }

            if (this.currentChasingTicks >= this.getMaxMemory() * 2.0f) {
                this.chasingBlockTicks = (int) (250 - (50 * this.getRandomPlayerChasePercent()));
                this.setForceChangeWayDirection(true);
            }

            double seekDist = this.getMaxSeekDist();

            double yDelta = Math.abs(player.getPosition().y - this.target().getPosition().y);

            if (this.chasingBlockTicks-- > 0) {
                this.currentMemory = 0;
                if (this.canSeePlayer(player) && distToPlayer <= seekDist / 3.0f && yDelta <= 4.0f) {
                    this.getAtomicBoolean().set(true);
                    this.chasingBlockTicks = 0;
                } else {
                    this.getAtomicBoolean().set(false);
                }
            } else {
                this.getAtomicBoolean().set(this.currentMemory-- > 0);
                if ((distToPlayer <= seekDist / 2.0f || (this.canSeePlayer(player) && distToPlayer <= seekDist)) && yDelta <= 4.0f) {
                    this.getAtomicBoolean().set(true);
                    this.currentMemory = this.getMaxMemory();
                }
            }

            this.updateNavOnAggression(player);
        }
    }

    private void updateNavOnAggression(WorldItem worldItem) {
        float aggressionPercent = 1.0f;
        boolean hasEnabledRadio = false;

        final float minSeekDist = 16.0f;
        final float minSpeed = 0.09f;
        final int minMemory = 200;

        final float maxSeekDist = 50.0f;
        final float maxSpeed = 0.12f;
        final int maxMemory = 600;

        this.setRandomPlayerChasePercent(0.375f * aggressionPercent);
        this.maxMemory = (int) (minMemory + (maxMemory - minMemory) * aggressionPercent);
        this.setMaxSpeed((minSpeed + (maxSpeed - minSpeed) * aggressionPercent));
        this.maxSeekDist = (minSeekDist + (maxSeekDist - minSeekDist) * aggressionPercent);
    }

    public float getMaxSeekDist() {
        return this.maxSeekDist;
    }

    public synchronized boolean isForceChangeWayDirection() {
        return this.forceChangeWayDirection;
    }

    public synchronized void setForceChangeWayDirection(boolean forceChangeWayDirection) {
        this.forceChangeWayDirection = forceChangeWayDirection;
    }

    public synchronized float getRandomPlayerChasePercent() {
        return this.randomPlayerChasePercent;
    }

    public synchronized void setRandomPlayerChasePercent(float randomPlayerChasePercent) {
        this.randomPlayerChasePercent = randomPlayerChasePercent;
    }

    public synchronized float getMaxSpeed() {
        return this.maxSpeed;
    }

    public synchronized void setMaxSpeed(float maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public int getMaxMemory() {
        return this.maxMemory;
    }

    private double distanceToPlayer(WorldItem worldItem) {
        return this.target().getPosition().distance(worldItem.getPosition());
    }

    private boolean canSeePlayer(WorldItem worldItem) {
        return true;
    }

    private GraphVertex findClosestPlayerVertex(Graph graph) {
        GraphVertex closest = graph.getStart();
        double closestDist = this.getPlayerPos().distance(closest.getPosition());
        for (GraphVertex vertex : graph.getGraphContainer().keySet()) {
            double currDist = this.getPlayerPos().distance(vertex.getPosition());
            if (currDist < closestDist) {
                closest = vertex;
                closestDist = currDist;
            }
        }
        return closest;
    }

    private synchronized Vector3f getPlayerPos() {
        return this.playerPos;
    }

    private AtomicBoolean getAtomicBoolean() {
        return this.atomicBoolean;
    }

    private synchronized GraphVertex getCurrentSyncVertex() {
        return this.getCurrentVertex();
    }

    private synchronized List<GraphVertex> getQueuePath() {
        return this.queuePath;
    }

    public final synchronized Player getPlayer() {
        return this.player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
}
