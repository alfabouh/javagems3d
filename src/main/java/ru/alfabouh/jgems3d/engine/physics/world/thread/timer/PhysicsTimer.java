package ru.alfabouh.jgems3d.engine.physics.world.thread.timer;

import com.jme3.bullet.collision.PhysicsCollisionObject;
import ru.alfabouh.jgems3d.engine.JGems;
import ru.alfabouh.jgems3d.engine.physics.world.World;
import ru.alfabouh.jgems3d.engine.physics.world.thread.dynamics.DynamicsSystem;
import ru.alfabouh.jgems3d.engine.system.exception.JGemsException;
import ru.alfabouh.jgems3d.engine.system.synchronizing.SyncManager;
import ru.alfabouh.jgems3d.logger.SystemLogging;

public class PhysicsTimer implements IPhysTimer {
    private final DynamicsSystem dynamicsSystem;

    public static final Object lockObject = new Object();
    public static int TPS;

    private final World world;

    @SuppressWarnings("all")
    public PhysicsTimer() {
        this.dynamicsSystem = new DynamicsSystem();
        this.world = new World();
    }

    @SuppressWarnings("all")
    public void updateTimer(int TPS) {
        final float time = 1.0f / TPS;
        final World world1 = this.world;
        this.getDynamicsSystem().init();

        if (dynamicsSystem == null) {
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
                        this.dynamicsSystem.step(time, 0);
                        this.dynamicsSystem.collideTest();
                    }
                }
                PhysicsTimer.TPS += 1;
            }
            SystemLogging.get().getLogManager().log("Stopping physics!");
        } catch (JGemsException e) {
            throw new JGemsException(e);
        }
    }

    public World getWorld() {
        synchronized (PhysicsTimer.lockObject) {
            return this.world;
        }
    }

    public void cleanResources() {
        SystemLogging.get().getLogManager().log("Cleaning physics world resources...");
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
