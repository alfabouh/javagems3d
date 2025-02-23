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

import api.events.EventBus;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import javagems3d.JGems3D;
import api.events.EventLauncher;
import javagems3d.physics.world.PhysicsWorld;
import javagems3d.physics.world.thread.dynamics.DynamicsSystem;
import javagems3d.system.service.exceptions.JGemsRuntimeException;
import javagems3d.system.service.synchronizing.SyncManager;
import logger.Log;

public class PhysicsProcessor implements IPhysicsProcessor {
    public static final Object lockObject = new Object();
    public static int TPS;
    private final DynamicsSystem dynamicsSystem;
    private final PhysicsWorld world;

    @SuppressWarnings("all")
    public PhysicsProcessor() {
        this.dynamicsSystem = new DynamicsSystem();
        this.world = new PhysicsWorld();
    }

    @SuppressWarnings("all")
    public void updateTimer(int TPS) {
        final float time = 1.0f / TPS;
        this.getDynamicsSystem().init();
        Log.get().info("Starting physics");
        try {
            while (!JGems3D.get().isShouldBeClosed()) {
                SyncManager.SyncPhysics.mark();
                SyncManager.SyncPhysics.blockCurrentThread(true);
                if (JGems3D.get().getCore().engineState().isEngineIsReady() && !JGems3D.get().getCore().engineState().isPaused()) {
                    synchronized (PhysicsProcessor.lockObject) {
                        this.world.onWorldUpdate();
                        EventLauncher.pushEvent(new EventBus.BulletUpdate(this.dynamicsSystem));
                        this.dynamicsSystem.step(time, 0);
                        this.dynamicsSystem.collideTest();
                    }
                }
                PhysicsProcessor.TPS += 1;
            }
        } catch (Exception e) {
            throw new JGemsRuntimeException(e);
        }
        Log.get().info("Stopping physics");
    }

    public void clearResources() {
        this.getDynamicsSystem().destroy();
        Log.get().info("Cleaned physics world resources");
    }

    public void removeDynamicsObject(PhysicsCollisionObject physicsCollisionObject) {
        this.getDynamicsSystem().removeCollisionObject(physicsCollisionObject);
    }

    public void addDynamicsObject(PhysicsCollisionObject physicsCollisionObject) {
        this.getDynamicsSystem().addCollisionObject(physicsCollisionObject);
    }

    public PhysicsWorld getPhysicsWorld() {
        return this.world;
    }

    public DynamicsSystem getDynamicsSystem() {
        synchronized (PhysicsProcessor.lockObject) {
            return this.dynamicsSystem;
        }
    }
}
