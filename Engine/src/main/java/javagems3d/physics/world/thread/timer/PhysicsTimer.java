/*
 * *
 *  * @author alfabouh
 *  * @since 2024
 *  * @link https://github.com/alfabouh/JavaGems3D
 *  *
 *  * This software is provided 'as-is', without any express or implied warranty.
 *  * In no event will the authors be held liable for any damages arising from the use of this software.
 *
 */

package javagems3d.physics.world.thread.timer;

import com.jme3.bullet.collision.PhysicsCollisionObject;
import javagems3d.JGems3D;
import javagems3d.JGemsHelper;
import api.bridge.events.APIEventsLauncher;
import javagems3d.physics.world.PhysicsWorld;
import javagems3d.physics.world.thread.dynamics.DynamicsSystem;
import javagems3d.system.service.exceptions.JGemsException;
import javagems3d.system.service.exceptions.JGemsNullException;
import javagems3d.system.service.exceptions.JGemsRuntimeException;
import javagems3d.system.service.synchronizing.SyncManager;
import api.app.events.bus.Events;

public class PhysicsTimer implements IPhysTimer {
    public static final Object lockObject = new Object();
    public static int TPS;
    private final DynamicsSystem dynamicsSystem;
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
            throw new JGemsNullException("Current Dynamics PhysicsWorld is NULL!");
        }
        try {
            JGemsHelper.getLogger().log("Starting physics!");
            while (!JGems3D.get().isShouldBeClosed()) {
                SyncManager.SyncPhysics.mark();
                SyncManager.SyncPhysics.blockCurrentThread(true);
                if (JGems3D.get().getEngineSystem().engineState().isEngineIsReady() && !JGems3D.get().getEngineSystem().engineState().isPaused()) {
                    synchronized (PhysicsTimer.lockObject) {
                        this.world.onWorldUpdate();
                        APIEventsLauncher.pushEvent(new Events.BulletUpdate(this.dynamicsSystem));
                        this.dynamicsSystem.step(time, 5);
                        this.dynamicsSystem.collideTest();
                    }
                }
                PhysicsTimer.TPS += 1;
            }
            JGemsHelper.getLogger().log("Stopping physics!");
        } catch (JGemsException e) {
            throw new JGemsRuntimeException(e);
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

    public PhysicsWorld getWorld() {
        return this.world;
    }

    public DynamicsSystem getDynamicsSystem() {
        synchronized (PhysicsTimer.lockObject) {
            return this.dynamicsSystem;
        }
    }
}
