package ru.jgems3d.engine.physics.world.thread.timer;

import com.jme3.bullet.collision.PhysicsCollisionObject;
import ru.jgems3d.engine.JGems3D;
import ru.jgems3d.engine.physics.world.PhysicsWorld;
import ru.jgems3d.engine.physics.world.thread.dynamics.DynamicsSystem;
import ru.jgems3d.engine.JGemsHelper;
import ru.jgems3d.exceptions.JGemsException;
import ru.jgems3d.engine.system.synchronizing.SyncManager;

public class PhysicsTimer implements IPhysTimer {
    private final DynamicsSystem dynamicsSystem;

    public static final Object lockObject = new Object();
    public static int TPS;

    private final PhysicsWorld world;

    @SuppressWarnings("all")
    public PhysicsTimer() {
        this.dynamicsSystem = new DynamicsSystem();
        this.world = new PhysicsWorld();
    }

    @SuppressWarnings("all")
    public void updateTimer(int TPS) {
        final float time = 1.0f / TPS;
        this.getDynamicsSystem().init();

        if (dynamicsSystem == null) {
            throw new JGemsException("Current Dynamics PhysicsWorld is NULL!");
        }
        try {
            JGemsHelper.getLogger().log("Starting physics!");
            while (!JGems3D.get().isShouldBeClosed()) {
                SyncManager.SyncPhysics.mark();
                SyncManager.SyncPhysics.blockCurrentThread(true);
                if (JGems3D.get().getEngineState().isEngineIsReady() && !JGems3D.get().getEngineState().isPaused()) {
                    synchronized (PhysicsTimer.lockObject) {
                        this.world.onWorldUpdate();
                        this.dynamicsSystem.step(time, 0);
                        this.dynamicsSystem.collideTest();
                    }
                }
                PhysicsTimer.TPS += 1;
            }
            JGemsHelper.getLogger().log("Stopping physics!");
        } catch (JGemsException e) {
            throw new JGemsException(e);
        }
    }

    public PhysicsWorld getWorld() {
        synchronized (PhysicsTimer.lockObject) {
            return this.world;
        }
    }

    public void cleanResources() {
        JGemsHelper.getLogger().log("Cleaning physics world resources...");
        this.getDynamicsSystem().destroy();
    }

    public void removeDynamicsObject(PhysicsCollisionObject physicsCollisionObject) {
        this.getDynamicsSystem().removeCollisionObject(physicsCollisionObject);
    }

    public void addDynamicsObject(PhysicsCollisionObject physicsCollisionObject) {
        this.getDynamicsSystem().addCollisionObject(physicsCollisionObject);
    }

    public DynamicsSystem getDynamicsSystem() {
        synchronized (PhysicsTimer.lockObject) {
            return this.dynamicsSystem;
        }
    }
}