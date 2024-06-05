package ru.alfabouh.engine.physics.entities.enemy.ai;

import org.bytedeco.bullet.BulletCollision.btBroadphaseProxy;
import org.bytedeco.bullet.BulletCollision.btCollisionWorld;
import org.bytedeco.bullet.LinearMath.btVector3;
import org.joml.Vector3d;
import ru.alfabouh.engine.audio.sound.GameSound;
import ru.alfabouh.engine.audio.sound.data.SoundType;
import ru.alfabouh.engine.JGems;
import ru.alfabouh.engine.system.exception.GameException;
import ru.alfabouh.engine.system.logger.JGemsLogging;
import ru.alfabouh.engine.system.resources.ResourceManager;
import ru.alfabouh.engine.graph.Graph;
import ru.alfabouh.engine.graph.pathfind.AStar;
import ru.alfabouh.engine.inventory.items.ItemRadio;
import ru.alfabouh.engine.physics.entities.BodyGroup;
import ru.alfabouh.engine.physics.entities.player.IPlayer;
import ru.alfabouh.engine.physics.entities.player.KinematicPlayerSP;
import ru.alfabouh.engine.physics.world.IWorld;
import ru.alfabouh.engine.physics.world.World;
import ru.alfabouh.engine.physics.world.object.WorldItem;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class NavigationToPlayerAI extends NavigationAI {
    private boolean forceChangeWayDirection;

    private final Vector3d playerPos;
    private final List<Graph.GVertex> queuePath;
    private final AtomicBoolean atomicBoolean;
    private IPlayer player;

    private float maxSpeed;
    private int currentMemory;
    private int currentChasingTicks;
    private int chasingBlockTicks;
    private int maxMemory;
    private float maxSeekDist;
    private int ticksBeforeRefreshPath;
    private float randomPlayerChasePercent;

    private final GameSound chasingSound;

    public NavigationToPlayerAI(double speed, WorldItem worldItem, final World world) {
        super(speed, worldItem, world);
        this.queuePath = new ArrayList<>();
        this.atomicBoolean = new AtomicBoolean(false);
        this.playerPos = new Vector3d(0.0d);
        this.chasingSound = JGems.get().getSoundManager().createSound(ResourceManager.soundAssetsLoader.saw, SoundType.WORLD_AMBIENT_SOUND, 2.0f, 4.0f, 3.0f);

        if (this.chasingSound != null) {
            this.chasingSound.setAttachedTo(worldItem);
        }

        Thread seekPathThread = new Thread(() -> {
            try {
                Graph.GVertex randomVertex = world.getGraph() == null ? null : world.getGraph().getRandomVertex();
                while (JGems.get().isCurrentMapIsValid()) {
                    try {
                        if (worldItem.getWorld().getGraph() == null || this.getCurrentSyncVertex() == null || !this.isActive()) {
                            continue;
                        }

                        this.setSpeed(this.getAtomicBoolean().get() ? this.getMaxSpeed() : 0.125f);

                        if ((!this.getAtomicBoolean().get() && this.reachedDestination()) || this.isForceChangeWayDirection()) {
                            randomVertex = JGems.random.nextFloat() <= this.getRandomPlayerChasePercent() ? this.findClosestPlayerVertex(worldItem.getWorld().getGraph()) : world.getGraph().getRandomVertex();
                            this.setForceChangeWayDirection(false);
                        }

                        AStar aStar = new AStar(worldItem.getWorld().getGraph(), this.getCurrentSyncVertex(), this.getAtomicBoolean().get() ? this.findClosestPlayerVertex(worldItem.getWorld().getGraph()) : randomVertex);
                        List<Graph.GVertex> path = aStar.findPath();
                        this.getQueuePath().clear();

                        if (path != null) {
                            this.getQueuePath().addAll(path);
                        } else {
                            JGems.get().getLogManager().warn("Nav pathfind problems!");
                        }

                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        throw new GameException(e);
                    }
                }
            } catch (Exception e) {
                JGems.get().getLogManager().exception(e);
                JGemsLogging.showExceptionDialog("An exception occurred inside the system. Open the logs folder for details.");
            }
        });
        seekPathThread.setDaemon(true);
        seekPathThread.setName("PathThread");
        seekPathThread.start();
    }

    private void refreshPath() {
        if (this.getQueuePath() != null && this.getNextVertex() == null) {
            ArrayList<Graph.GVertex> gVertices = new ArrayList<>(this.getQueuePath());
            if (!gVertices.isEmpty()) {
                if (gVertices.get(0) != null && !gVertices.get(0).equals(this.getCurrentVertex())) {
                    Iterator<Graph.GVertex> gVertexIterator = gVertices.iterator();
                    while (gVertexIterator.hasNext()) {
                        Graph.GVertex vertex = gVertexIterator.next();
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
            if (((KinematicPlayerSP) player).getScalarSpeed() <= 0.0f) {
                seekDist *= 0.75f;
            }

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

            if (this.getAtomicBoolean().get()) {
                if (!this.chasingSound.isPlaying()) {
                    this.chasingSound.playSound();
                }
                this.currentChasingTicks += 1;
            } else {
                this.currentChasingTicks = 0;
                this.chasingSound.stopSound();
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

        if (worldItem instanceof KinematicPlayerSP) {
            KinematicPlayerSP kinematicPlayerSP = (KinematicPlayerSP) worldItem;
            aggressionPercent = (float) (kinematicPlayerSP.getPickedCds() + kinematicPlayerSP.getPrickedCassettes()) / (kinematicPlayerSP.getMaxCds() + kinematicPlayerSP.getMaxCassettes());
            hasEnabledRadio = (kinematicPlayerSP.inventory().getCurrentItem() instanceof ItemRadio) && ((ItemRadio) kinematicPlayerSP.inventory().getCurrentItem()).isOpened();
        }
        aggressionPercent = (float) Math.min(aggressionPercent + (hasEnabledRadio ? 0.1f : 0.0f), 1.0);

        this.setRandomPlayerChasePercent(0.375f * aggressionPercent);
        this.maxMemory = (int) (minMemory + (maxMemory - minMemory) * aggressionPercent);
        this.setMaxSpeed((minSpeed + (maxSpeed - minSpeed) * aggressionPercent));
        this.maxSeekDist = (minSeekDist + (maxSeekDist - minSeekDist) * aggressionPercent);
    }

    public float getMaxSeekDist() {
        return this.maxSeekDist;
    }

    public synchronized void setRandomPlayerChasePercent(float randomPlayerChasePercent) {
        this.randomPlayerChasePercent = randomPlayerChasePercent;
    }

    public synchronized void setForceChangeWayDirection(boolean forceChangeWayDirection) {
        this.forceChangeWayDirection = forceChangeWayDirection;
    }

    public synchronized boolean isForceChangeWayDirection() {
        return this.forceChangeWayDirection;
    }

    public synchronized float getRandomPlayerChasePercent() {
        return this.randomPlayerChasePercent;
    }

    public synchronized void setMaxSpeed(float maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public synchronized float getMaxSpeed() {
        return this.maxSpeed;
    }

    public int getMaxMemory() {
        return this.maxMemory;
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
