package ru.alfabouh.engine.physics.entities.enemy.ai;

import org.bytedeco.bullet.BulletCollision.btBroadphaseProxy;
import org.bytedeco.bullet.BulletCollision.btCollisionWorld;
import org.bytedeco.bullet.LinearMath.btVector3;
import org.joml.Vector3d;
import ru.alfabouh.engine.game.Game;
import ru.alfabouh.engine.game.exception.GameException;
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
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class NavigationToPlayerAI extends NavigationAI {
    private final Vector3d playerPos;
    private final List<Graph.GVertex> queuePath;
    private final AtomicBoolean atomicBoolean;
    private IPlayer player;
    private int ticksBeforeRefreshPath;
    private int memory;
    private double maxSpeed;
    private int forceSeekPlayerCd;
    private int maxPlayerChasingTicks;
    private int chasingTicks;
    private int penalty;
    private int rageCd;
    private boolean canRage;

    public NavigationToPlayerAI(double speed, WorldItem worldItem, final World world) {
        super(speed, worldItem, world);
        this.queuePath = new ArrayList<>();
        this.atomicBoolean = new AtomicBoolean(false);
        this.playerPos = new Vector3d(0.0d);
        Thread seekPathThread = new Thread(() -> {
            int i = 0;
            Graph.GVertex randomVertex = world.getGraph() == null ? null : world.getGraph().getRandomVertex();
            while (Game.getGame().isCurrentMapIsValid()) {
                try {
                    if (worldItem.getWorld().getGraph() == null || this.getCurrentSyncVertex() == null || !this.isActive()) {
                        continue;
                    }
                    if (randomVertex == null) {
                        randomVertex = worldItem.getWorld().getGraph().getRandomVertex();
                    }
                    if (this.rageCd > 0) {
                        this.setSpeed(Math.min(maxSpeed * 1.5d, 0.2d));
                    } else {
                        this.setSpeed(!this.getAtomicBoolean().get() ? Math.min(maxSpeed * 2.0d, 0.2d) : maxSpeed);
                    }

                    AStar aStar = new AStar(worldItem.getWorld().getGraph(), this.getCurrentSyncVertex(), this.getAtomicBoolean().get() ? this.findClosestPlayerVertex(worldItem.getWorld().getGraph()) : randomVertex);
                    List<Graph.GVertex> path = aStar.findPath();
                    this.getQueuePath().clear();
                    if (path != null) {
                        this.getQueuePath().addAll(path);
                    } else {
                        Game.getGame().getLogManager().warn("Nav pathfind problems!");
                    }
                    if (i++ >= 200 && Game.random.nextBoolean()) {
                        randomVertex = worldItem.getWorld().getGraph().getRandomVertex();
                        i = 0;
                    }
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    throw new GameException(e);
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
            if (this.getAtomicBoolean().get()) {
                //System.out.println(this.chasingTicks + " " + this.maxPlayerChasingTicks);
                if (this.penalty <= 0 && this.chasingTicks++ >= this.maxPlayerChasingTicks) {
                    this.penalty = 300;
                    this.chasingTicks = 0;
                }
            } else {
                this.chasingTicks = 0;
            }

            if (this.ticksBeforeRefreshPath++ >= Math.min(this.distanceToPlayer(player), 30)) {
                this.getPlayerPos().set(player.getPosition());
                this.ticksBeforeRefreshPath = 0;
            }

            if (this.penalty-- > 0) {
                if (this.distanceToPlayer(player) > 8.0d) {
                    this.canRage = true;
                    this.getAtomicBoolean().set(false);
                    this.maxSpeed = 0;
                    this.penalty = 0;
                    return;
                } else if (this.canRage) {
                    this.rageCd = 100;
                }
            } else {
                this.canRage = false;
            }
            if (this.rageCd > 0) {
                this.getAtomicBoolean().set(true);
            }

            this.updateNavOnAggression(player);
        }
    }

    protected void reachedVertex() {
        super.reachedVertex();
        this.setPathToVertex(this.getQueuePath());
    }

    private void updateNavOnAggression(WorldItem worldItem) {
        int aggression = 9;
        boolean hasEnabledRadio = false;
        if (worldItem instanceof KinematicPlayerSP) {
            KinematicPlayerSP kinematicPlayerSP = (KinematicPlayerSP) worldItem;
            aggression = (int) ((double) (kinematicPlayerSP.getPickedCds() + kinematicPlayerSP.getPrickedCassettes()) / (kinematicPlayerSP.getMaxCds() + kinematicPlayerSP.getMaxCassettes()) * 10.0f);
            hasEnabledRadio = (kinematicPlayerSP.inventory().getCurrentItem() instanceof ItemRadio) && ((ItemRadio) kinematicPlayerSP.inventory().getCurrentItem()).isOpened();
        }

        double maxDist = 8.0d;
        double maxXrayVision = 4.0d;
        int maxMemory = 20;
        double speed = 0.08d;
        int maxForceSeekPlayerCd = -1;
        int maxChasingTicks = 350;

        switch (aggression) {
            case 1: {
                speed = 0.1d;
                maxMemory = 300;
                maxDist = 16.0d;
                maxXrayVision = 8.0d;
                break;
            }
            case 2: {
                speed = 0.1d;
                maxMemory = 320;
                maxDist = 20.0d;
                maxXrayVision = 10.0d;
                maxForceSeekPlayerCd = 500;
                maxChasingTicks = 400;
                break;
            }
            case 3: {
                speed = 0.1d;
                maxMemory = 350;
                maxDist = 24.0d;
                maxXrayVision = 12.0d;
                maxForceSeekPlayerCd = 450;
                maxChasingTicks = 450;
                break;
            }
            case 4: {
                speed = 0.11d;
                maxMemory = 400;
                maxDist = 28.0d;
                maxXrayVision = 14.0d;
                maxForceSeekPlayerCd = 400;
                maxChasingTicks = 500;
                break;
            }
            case 5: {
                speed = 0.115d;
                maxMemory = 450;
                maxDist = 32.0d;
                maxXrayVision = 16.0d;
                maxForceSeekPlayerCd = 400;
                maxChasingTicks = 550;
                break;
            }
            case 6: {
                speed = 0.12d;
                maxMemory = 500;
                maxDist = 36.0d;
                maxXrayVision = 18.0d;
                maxForceSeekPlayerCd = 350;
                maxChasingTicks = 600;
                break;
            }
            case 7: {
                speed = 0.1225d;
                maxMemory = 600;
                maxDist = 40.0d;
                maxXrayVision = 20.0d;
                maxForceSeekPlayerCd = 300;
                maxChasingTicks = 650;
                break;
            }
            case 8: {
                speed = 0.125d;
                maxMemory = 680;
                maxDist = 44.0d;
                maxXrayVision = 22.0d;
                maxForceSeekPlayerCd = 300;
                maxChasingTicks = 700;
                break;
            }
            case 9: {
                speed = 0.14d;
                maxMemory = 800;
                maxDist = 48.0d;
                maxXrayVision = 24.0d;
                maxForceSeekPlayerCd = 200;
                maxChasingTicks = 800;
                break;
            }
        }

        if (hasEnabledRadio) {
            maxChasingTicks += 100;
            maxDist += 8.0d;
            maxXrayVision += 8.0d;
            maxMemory += 10;
        }

        this.maxPlayerChasingTicks = maxChasingTicks;

        if (maxForceSeekPlayerCd > 0 && this.forceSeekPlayerCd-- < 0) {
            this.memory = aggression * 50;
            this.getAtomicBoolean().set(true);
            this.forceSeekPlayerCd = maxForceSeekPlayerCd;
        }

        this.maxSpeed = speed;
        if (this.memory-- > 0) {
            this.getAtomicBoolean().set(true);
            return;
        }

        this.getAtomicBoolean().set(false);
        if (this.canSeePlayer(worldItem)) {
            if (this.distanceToPlayer(worldItem) <= maxDist) {
                this.memory = maxMemory;
                this.getAtomicBoolean().set(true);
            }
        } else {
            if (this.distanceToPlayer(worldItem) <= maxXrayVision) {
                this.memory = maxMemory;
                this.getAtomicBoolean().set(true);
            }
        }
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
