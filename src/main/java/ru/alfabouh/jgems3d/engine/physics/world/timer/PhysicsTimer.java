package ru.alfabouh.jgems3d.engine.physics.world.timer;

import org.bytedeco.bullet.BulletCollision.*;
import org.bytedeco.bullet.BulletDynamics.*;
import org.bytedeco.bullet.LinearMath.btIDebugDraw;
import org.bytedeco.bullet.LinearMath.btVector3;
import org.bytedeco.bullet.global.BulletCollision;
import org.jetbrains.annotations.NotNull;
import ru.alfabouh.jgems3d.engine.JGems;
import ru.alfabouh.jgems3d.engine.physics.entities.BodyGroup;
import ru.alfabouh.jgems3d.engine.physics.world.World;
import ru.alfabouh.jgems3d.engine.render.opengl.scene.debug.bullet.JBDebugDraw;
import ru.alfabouh.jgems3d.engine.system.synchronizing.SyncManager;
import ru.alfabouh.jgems3d.proxy.exception.JGemsException;
import ru.alfabouh.jgems3d.proxy.logger.SystemLogging;

public class PhysicsTimer implements IPhysTimer {
    public static final Object lockObject = new Object();
    public static final Object lockProcess = new Object();
    public static int TPS;
    @SuppressWarnings("all")
    public final JBDebugDraw jbDebugDraw;
    private final World world;
    private final btBroadphaseInterface broadcaster;
    private final btCollisionConfiguration collisionConfiguration;
    private final btCollisionDispatcher collisionDispatcher;
    private final btDiscreteDynamicsWorld discreteDynamicsWorld;
    private final btConstraintSolver constraintSolve;
    private final btGhostPairCallback pairCallback;

    @SuppressWarnings("all")
    public PhysicsTimer() {
        this.broadcaster = new btDbvtBroadphase();
        this.pairCallback = new btGhostPairCallback();
        this.broadcaster.getOverlappingPairCache().setInternalGhostPairCallback(this.pairCallback);
        this.collisionConfiguration = new btDefaultCollisionConfiguration();
        this.collisionDispatcher = new btCollisionDispatcher(collisionConfiguration);
        this.collisionDispatcher.setDispatcherFlags(btCollisionDispatcher.CD_STATIC_STATIC_REPORTED | btCollisionDispatcher.CD_USE_RELATIVE_CONTACT_BREAKING_THRESHOLD);
        btGImpactCollisionAlgorithm.registerAlgorithm(this.collisionDispatcher);
        this.constraintSolve = new btSequentialImpulseConstraintSolver();
        this.discreteDynamicsWorld = new btDiscreteDynamicsWorld(this.getCollisionDispatcher(), this.getBroadcaster(), this.getConstraintSolver(), this.getCollisionConfiguration());
        this.discreteDynamicsWorld.setGravity(new btVector3(0, -9.8f, 0));
        this.discreteDynamicsWorld.getDispatchInfo().m_deterministicOverlappingPairs(true);
        this.discreteDynamicsWorld.getDispatchInfo().m_useConvexConservativeDistanceUtil(true);
        this.discreteDynamicsWorld.getDispatchInfo().m_useContinuous(true);
        this.discreteDynamicsWorld.getDispatchInfo().m_convexConservativeDistanceThreshold(0.01f);
        this.discreteDynamicsWorld.getDispatchInfo().m_allowedCcdPenetration(0.0d);

        this.jbDebugDraw = new JBDebugDraw();
        this.jbDebugDraw.setDebugMode(btIDebugDraw.DBG_DrawWireframe | btIDebugDraw.DBG_DrawAabb);
        this.discreteDynamicsWorld.setDebugDrawer(this.jbDebugDraw);
        this.world = new World();
    }

    @SuppressWarnings("all")
    public void updateTimer(int TPS) {
        final double step = 1.0f / TPS;
        final int explicit = 16;
        final World world1 = this.world;
        final btDiscreteDynamicsWorld discreteDynamicsWorld1 = this.discreteDynamicsWorld;

        if (discreteDynamicsWorld1 == null) {
            throw new JGemsException("Current Dynamics World is NULL!");
        }
        try {
            SystemLogging.get().getLogManager().log("Starting physics!");
            while (!JGems.get().isShouldBeClosed()) {
                SyncManager.SyncPhysics.mark();
                SyncManager.SyncPhysics.blockCurrentThread(true);
                if (JGems.get().getEngineState().isEngineIsReady() && !JGems.get().getEngineState().isPaused()) {
                    synchronized (PhysicsTimer.lockObject) {
                        world1.onWorldUpdate();
                        discreteDynamicsWorld1.stepSimulation(step, explicit, step / (double) explicit);
                    }
                }
                PhysicsTimer.TPS += 1;
            }
            SystemLogging.get().getLogManager().log("Stopping physics!");
        } catch (JGemsException e) {
            throw new JGemsException(e);
        } finally {
            JGems.get().destroyGame();
        }
    }

    private void pairsTick(final btDiscreteDynamicsWorld discreteDynamicsWorld1) {
        for (int i = 0; i < discreteDynamicsWorld1.getDispatcher().getNumManifolds(); i++) {
            btPersistentManifold btPersistentManifold = discreteDynamicsWorld1.getDispatcher().getManifoldByIndexInternal(i);
            int contacts = btPersistentManifold.getNumContacts();
            btCollisionObject btCollisionObject1 = btPersistentManifold.getBody0();
            btCollisionObject btCollisionObject2 = btPersistentManifold.getBody1();
            if (contacts > 0) {
                btCollisionObjectWrapper btCollisionObjectWrapper1 = new btCollisionObjectWrapper(discreteDynamicsWorld1);
                btCollisionObjectWrapper1.m_collisionObject(btCollisionObject1);
                btCollisionObjectWrapper btCollisionObjectWrapper2 = new btCollisionObjectWrapper(discreteDynamicsWorld1);
                btCollisionObjectWrapper2.m_collisionObject(btCollisionObject2);
                for (int j = 0; j < contacts; j++) {
                    if (btCollisionObject2.getCollisionShape().getShapeType() == BulletCollision.TRIANGLE_MESH_SHAPE_PROXYTYPE) {
                        BulletCollision.btAdjustInternalEdgeContacts(btPersistentManifold.getContactPoint(j), btCollisionObjectWrapper2, btCollisionObjectWrapper1, btPersistentManifold.getContactPoint(j).m_partId0(), btPersistentManifold.getContactPoint(j).m_index0(), 1 | 2 | 4);
                    }
                }
            }
        }
    }

    public World getWorld() {
        synchronized (PhysicsTimer.lockObject) {
            return this.world;
        }
    }

    public void cleanResources() {
        SystemLogging.get().getLogManager().log("Cleaning physics world resources...");
        this.getDiscreteDynamicsWorld().deallocate();
    }

    public final btCollisionWorld getCollisionWorld() {
        synchronized (PhysicsTimer.lockObject) {
            return this.getDiscreteDynamicsWorld().getCollisionWorld();
        }
    }

    public void addInWorld(btCollisionObject btCollisionObject, BodyGroup bodyGroup) {
        synchronized (PhysicsTimer.lockObject) {
            btCollisionObject.setUserIndex(bodyGroup.getIndex());
        }
        if (btCollisionObject instanceof btRigidBody) {
            this.getDiscreteDynamicsWorld().addRigidBody((btRigidBody) btCollisionObject, bodyGroup.getGroup(), bodyGroup.getMask());
        } else {
            this.getDiscreteDynamicsWorld().addCollisionObject(btCollisionObject, bodyGroup.getGroup(), bodyGroup.getMask());
        }
    }

    public void addRigidBodyInWorld(@NotNull btRigidBody rigidBody) {
        this.getDiscreteDynamicsWorld().addRigidBody(rigidBody);
    }

    public void addCollisionObjectInWorld(@NotNull btCollisionObject collisionObject) {
        this.getDiscreteDynamicsWorld().addCollisionObject(collisionObject);
    }

    public void removeRigidBodyFromWorld(@NotNull btRigidBody rigidBody) {
        this.getDiscreteDynamicsWorld().removeRigidBody(rigidBody);
    }

    public void removeCollisionObjectFromWorld(@NotNull btCollisionObject collisionObject) {
        this.getDiscreteDynamicsWorld().removeCollisionObject(collisionObject);
    }

    public void removeActionObjectFromWorld(@NotNull btActionInterface actionInterface) {
        this.getDiscreteDynamicsWorld().removeAction(actionInterface);
    }

    public void updateAabb(@NotNull btCollisionObject rigidBody) {
        this.getDiscreteDynamicsWorld().updateSingleAabb(rigidBody);
    }

    public final btDynamicsWorld getDynamicsWorld() {
        return this.getDiscreteDynamicsWorld();
    }

    private btDiscreteDynamicsWorld getDiscreteDynamicsWorld() {
        synchronized (PhysicsTimer.lockObject) {
            return this.discreteDynamicsWorld;
        }
    }

    public btConstraintSolver getConstraintSolver() {
        synchronized (PhysicsTimer.lockObject) {
            return this.constraintSolve;
        }
    }

    public btBroadphaseInterface getBroadcaster() {
        synchronized (PhysicsTimer.lockObject) {
            return this.broadcaster;
        }
    }

    public btCollisionConfiguration getCollisionConfiguration() {
        synchronized (PhysicsTimer.lockObject) {
            return this.collisionConfiguration;
        }
    }

    public btCollisionDispatcher getCollisionDispatcher() {
        synchronized (PhysicsTimer.lockObject) {
            return this.collisionDispatcher;
        }
    }
}
