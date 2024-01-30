package ru.BouH.engine.physics.world.timer;

import org.bytedeco.bullet.BulletCollision.*;
import org.bytedeco.bullet.BulletDynamics.*;
import org.bytedeco.bullet.LinearMath.btVector3;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import ru.BouH.engine.game.Game;
import ru.BouH.engine.game.exception.GameException;
import ru.BouH.engine.game.synchronizing.SyncManger;
import ru.BouH.engine.physics.entities.BodyGroup;
import ru.BouH.engine.physics.world.World;
import ru.BouH.engine.physics.world.object.IWorldDynamic;
import ru.BouH.engine.physics.world.object.WorldItem;

import java.util.Iterator;
import java.util.Set;

public class PhysicsTimer implements IPhysTimer {
    public static final Object lock = new Object();
    public static int TPS;
    private final World world;
    private final btBroadphaseInterface broadcaster;
    private final btCollisionConfiguration collisionConfiguration;
    private final btCollisionDispatcher collisionDispatcher;
    private final btDiscreteDynamicsWorld discreteDynamicsWorld;
    private final btConstraintSolver constraintSolve;
    private final btGhostPairCallback pairCallback;

    public PhysicsTimer() {
        this.broadcaster = new btAxisSweep3(new btVector3(PhysicThreadManager.WORLD_BORDERS.getA1(), PhysicThreadManager.WORLD_BORDERS.getA1(), PhysicThreadManager.WORLD_BORDERS.getA1()), new btVector3(PhysicThreadManager.WORLD_BORDERS.getA2(), PhysicThreadManager.WORLD_BORDERS.getA2(), PhysicThreadManager.WORLD_BORDERS.getA2()));
        this.pairCallback = new btGhostPairCallback();
        this.broadcaster.getOverlappingPairCache().setInternalGhostPairCallback(this.pairCallback);
        this.collisionConfiguration = new btDefaultCollisionConfiguration();
        this.collisionDispatcher = new btCollisionDispatcher(collisionConfiguration);
        this.collisionDispatcher.setDispatcherFlags(btCollisionDispatcher.CD_STATIC_STATIC_REPORTED | btCollisionDispatcher.CD_DISABLE_CONTACTPOOL_DYNAMIC_ALLOCATION | btCollisionDispatcher.CD_USE_RELATIVE_CONTACT_BREAKING_THRESHOLD);
        this.constraintSolve = new btConstraintSolverPoolMt(2);
        this.discreteDynamicsWorld = new btDiscreteDynamicsWorld(this.getCollisionDispatcher(), this.getBroadcaster(), this.getConstraintSolver(), this.getCollisionConfiguration());
        this.discreteDynamicsWorld.setGravity(new btVector3(0, -9.8f, 0));
        this.discreteDynamicsWorld.getDispatchInfo().m_deterministicOverlappingPairs(false);
        this.discreteDynamicsWorld.getDispatchInfo().m_useConvexConservativeDistanceUtil(true);
        this.discreteDynamicsWorld.getDispatchInfo().m_useContinuous(true);
        this.discreteDynamicsWorld.getDispatchInfo().m_convexConservativeDistanceThreshold(0.01f);
        this.discreteDynamicsWorld.getDispatchInfo().m_allowedCcdPenetration(0.0d);
        this.discreteDynamicsWorld.performDiscreteCollisionDetection();

        this.world = new World();
    }

    @SuppressWarnings("all")
    public void updateTimer(int TPS) {
        final double step = 1.0f / TPS;
        final int explicit = 16;
        final btDiscreteDynamicsWorld discreteDynamicsWorld1 = this.getDiscreteDynamicsWorld();
        final World world1 = this.getWorld();
        if (discreteDynamicsWorld1 == null) {
            throw new GameException("Current Dynamics World is NULL!");
        }
        try {
            Game.getGame().getLogManager().debug("Starting physics!");
            this.getWorld().onWorldStart();
            synchronized (Game.EngineStarter.logicLocker) {
                Game.EngineStarter.logicLocker.wait();
            }
            while (!Game.getGame().isShouldBeClosed()) {
                synchronized (PhysicThreadManager.locker) {
                    PhysicThreadManager.locker.wait();
                }
                this.getWorld().onWorldUpdate();
                synchronized (PhysicsTimer.lock) {
                    for (IWorldDynamic worldItem : this.world.getAllDynamicItems()) {
                        WorldItem worldItem1 = (WorldItem) worldItem;
                        ((WorldItem) worldItem).setPrevPosition(new Vector3d(worldItem1.getPosition()));
                        worldItem.onUpdate(world1);
                    }
                    discreteDynamicsWorld1.stepSimulation(step, explicit, step / (double) explicit);
                    SyncManger.SyncPhysicsAndRender.free();
                }
                PhysicsTimer.TPS += 1;
            }
            this.getWorld().onWorldEnd();
            Game.getGame().getLogManager().debug("Stopping physics!");
        } catch (InterruptedException | GameException e) {
            throw new RuntimeException(e);
        } finally {
            this.cleanResources();
        }
    }

    public World getWorld() {
        return this.world;
    }

    public void cleanResources() {
        Game.getGame().getLogManager().log("Cleaning physics world resources...");
        this.getDiscreteDynamicsWorld().deallocate();
        Set<WorldItem> worldItems = this.getWorld().getAllWorldItems();
        Iterator<WorldItem> worldItemIterator = worldItems.iterator();
        while (worldItemIterator.hasNext()) {
            WorldItem worldItem = worldItemIterator.next();
            worldItem.onDestroy(this.getWorld());
            worldItemIterator.remove();
        }
    }

    public synchronized btGhostPairCallback getPairCallback() {
        return this.pairCallback;
    }

    public synchronized final btDynamicsWorld getDynamicsWorld() {
        return this.getDiscreteDynamicsWorld();
    }

    public synchronized final btCollisionWorld getCollisionWorld() {
        return this.getDiscreteDynamicsWorld().getCollisionWorld();
    }

    public void addInWorld(btCollisionObject btCollisionObject, BodyGroup bodyGroup) {
        synchronized (PhysicsTimer.lock) {
            btCollisionObject.setUserIndex(bodyGroup.getIndex());
            if (btCollisionObject instanceof btRigidBody) {
                this.getDiscreteDynamicsWorld().addRigidBody((btRigidBody) btCollisionObject, bodyGroup.getGroup(), bodyGroup.getMask());
            } else {
                this.getDiscreteDynamicsWorld().addCollisionObject(btCollisionObject, bodyGroup.getGroup(), bodyGroup.getMask());
            }
        }
    }

    public void addRigidBodyInWorld(@NotNull btRigidBody rigidBody) {
        synchronized (PhysicsTimer.lock) {
            this.getDiscreteDynamicsWorld().addRigidBody(rigidBody);
        }
    }

    public void addCollisionObjectInWorld(@NotNull btCollisionObject collisionObject) {
        synchronized (PhysicsTimer.lock) {
            this.getDiscreteDynamicsWorld().addCollisionObject(collisionObject);
        }
    }

    public void removeRigidBodyFromWorld(@NotNull btRigidBody rigidBody) {
        synchronized (PhysicsTimer.lock) {
            this.getDiscreteDynamicsWorld().removeRigidBody(rigidBody);
        }
    }

    public void removeCollisionObjectFromWorld(@NotNull btCollisionObject collisionObject) {
        synchronized (PhysicsTimer.lock) {
            this.getDiscreteDynamicsWorld().removeCollisionObject(collisionObject);
        }
    }

    public void updateRigidBodyAabb(@NotNull btRigidBody rigidBody) {
        synchronized (PhysicsTimer.lock) {
            this.getDiscreteDynamicsWorld().updateSingleAabb(rigidBody);
        }
    }

    private synchronized btDiscreteDynamicsWorld getDiscreteDynamicsWorld() {
        return this.discreteDynamicsWorld;
    }

    public btConstraintSolver getConstraintSolver() {
        return this.constraintSolve;
    }

    public btBroadphaseInterface getBroadcaster() {
        return this.broadcaster;
    }

    public btCollisionConfiguration getCollisionConfiguration() {
        return this.collisionConfiguration;
    }

    public btCollisionDispatcher getCollisionDispatcher() {
        return this.collisionDispatcher;
    }
}
