/*
 *
 *  * javagems3d
 *  * Copyright (C) 2026 gltexture
 *  *
 *  * This program is free software: you can redistribute it and/or modify
 *  * it under the terms of the GNU General Public License as published by
 *  * the Free Software Foundation, either version 3 of the License, or
 *  * (at your option) any later version.
 *  *
 *  * This program is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  * GNU General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU General Public License
 *  * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 */

package javagems3d.physics.world.thread.timer;

import com.jme3.bullet.collision.PhysicsCollisionObject;
import javagems3d.JGems3D;
import javagems3d.physics.world.PhysicsWorld;
import javagems3d.physics.world.thread.JGemsPhysics;
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
                        this.dynamicsSystem.step(time, JGemsPhysics.SUBSTEPS);
                        //this.dynamicsSystem.collideTest();
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
