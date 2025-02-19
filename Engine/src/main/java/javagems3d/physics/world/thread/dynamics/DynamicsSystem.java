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

package javagems3d.physics.world.thread.dynamics;

import api.events.EventBus;
import com.jme3.bullet.CollisionConfiguration;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.SolverType;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.math.Vector3f;
import javagems3d.JGems3D;
import api.events.EventLauncher;
import javagems3d.JGemsHelper;
import javagems3d.physics.world.thread.dynamics.extractor.NativesExtractor;
import javagems3d.physics.world.triggers.IHasCollisionTrigger;
import javagems3d.physics.world.triggers.ITriggerAction;
import javagems3d.system.service.collections.Pair;
import javagems3d.system.service.exceptions.JGemsRuntimeException;
import javagems3d.system.service.synchronizing.SyncManager;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

public class DynamicsSystem {
    private final Set<PhysicsCollisionObject> objectsWithCollideTriggers;
    private PhysicsSpace physicsSpace;

    public DynamicsSystem() {
        this.objectsWithCollideTriggers = SyncManager.createSyncronisedSet();
    }

    public void init() {
        Path path = Paths.get(JGems3D.getEngineFilesFolder().toString(), "natives");
        try {
            String lib = NativesExtractor.extractNativesAndReturnPath(path, JGems3D.get().getOS());
            System.load(lib);
            JGemsHelper.getLogger().info("Injected lib: " + lib);
        } catch (Exception e) {
            throw new JGemsRuntimeException(e);
        }
        CollisionConfiguration collisionConfiguration = new CollisionConfiguration();
        this.physicsSpace = new PhysicsSpace(new Vector3f(-JGems3D.MAP_MAX_SIZE, -JGems3D.MAP_MAX_SIZE, -JGems3D.MAP_MAX_SIZE), new Vector3f(JGems3D.MAP_MAX_SIZE, JGems3D.MAP_MAX_SIZE, JGems3D.MAP_MAX_SIZE), PhysicsSpace.BroadphaseType.AXIS_SWEEP_3, SolverType.SI, collisionConfiguration);
        this.physicsSpace.setGravity(new Vector3f(0.0f, -10.0f, 0.0f));
    }

    public void collideTest() {
        Set<Pair<IHasCollisionTrigger, Object>> triggerPairs = new HashSet<>();
        for (PhysicsCollisionObject physicsCollisionObject : this.getObjectsWithCollideTriggers()) {
            IHasCollisionTrigger trigger = (IHasCollisionTrigger) physicsCollisionObject.getUserObject();
            if (!trigger.isValid()) {
                continue;
            }
            this.getPhysicsSpace().contactTest(physicsCollisionObject, event -> {
                Object obA = event.getObjectA().getUserObject();
                Object obB = event.getObjectB().getUserObject();
                if (obA instanceof IHasCollisionTrigger) {
                    IHasCollisionTrigger collideTrigger = (IHasCollisionTrigger) obA;
                    triggerPairs.add(new Pair<>(collideTrigger, obB));
                }
            });
        }
        for (Pair<IHasCollisionTrigger, Object> objectPair : triggerPairs) {
            ITriggerAction triggerAction = objectPair.getFirst().onColliding();
            if (triggerAction != null) {
                if (!EventLauncher.pushEvent(new EventBus.CollisionTriggered(objectPair.getFirst(), triggerAction)).isCancelled()) {
                    triggerAction.action(objectPair.getSecond());
                }
            }
        }
    }

    public void step(float time, int maxSteps) {
        this.getPhysicsSpace().update(time, maxSteps);
    }

    public void destroy() {
        this.getPhysicsSpace().destroy();
    }

    public void addCollisionObject(PhysicsCollisionObject collisionObject) {
        this.getPhysicsSpace().addCollisionObject(collisionObject);

        if (collisionObject.getUserObject() instanceof IHasCollisionTrigger) {
            this.getObjectsWithCollideTriggers().add(collisionObject);
        }
    }

    public void removeCollisionObject(PhysicsCollisionObject collisionObject) {
        this.getPhysicsSpace().removeCollisionObject(collisionObject);
        this.getObjectsWithCollideTriggers().remove(collisionObject);
    }

    public Set<PhysicsCollisionObject> getObjectsWithCollideTriggers() {
        return this.objectsWithCollideTriggers;
    }

    public PhysicsSpace getPhysicsSpace() {
        return this.physicsSpace;
    }
}
