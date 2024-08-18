package ru.jgems3d.engine.physics.world.thread.dynamics;
import com.jme3.bullet.CollisionConfiguration;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.SolverType;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.math.Vector3f;
import com.jme3.system.NativeLibraryLoader;
import ru.jgems3d.engine.api_bridge.events.APIEventsLauncher;
import ru.jgems3d.engine.physics.world.triggers.IHasCollisionTrigger;
import ru.jgems3d.engine.physics.world.triggers.ITriggerAction;
import ru.jgems3d.engine.system.service.synchronizing.SyncManager;
import ru.jgems3d.engine_api.events.bus.Events;

import java.io.File;
import java.util.*;

public class DynamicsSystem {
    private PhysicsSpace physicsSpace;
    private final Set<PhysicsCollisionObject> objectsWithCollideTriggers;

    public DynamicsSystem() {
        this.objectsWithCollideTriggers = SyncManager.createSyncronisedSet();
    }

    public void init() {
        boolean dist = true;
        String buildType = "Debug";
        String flavor = "Sp";
        NativeLibraryLoader.loadLibbulletjme(dist, new File("dlls"), buildType, flavor);

        CollisionConfiguration collisionConfiguration = new CollisionConfiguration();
        this.physicsSpace = new PhysicsSpace(new Vector3f(-1024.0f, -1024.0f, -1024.0f), new Vector3f(1024.0f, 1024.0f, 1024.0f), PhysicsSpace.BroadphaseType.AXIS_SWEEP_3, SolverType.SI, collisionConfiguration);
        this.physicsSpace.setGravity(new Vector3f(0.0f, -10.0f, 0.0f));
    }

    public void collideTest() {
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
                    ITriggerAction triggerAction = collideTrigger.onColliding();
                    if (triggerAction != null) {
                        if (!APIEventsLauncher.pushEvent(new Events.CollisionTriggered(collideTrigger, triggerAction)).isCancelled()) {
                            triggerAction.action(obB);
                        }
                    }
                }
                if (obB instanceof IHasCollisionTrigger) {
                    IHasCollisionTrigger collideTrigger = (IHasCollisionTrigger) obB;
                    ITriggerAction triggerAction = collideTrigger.onColliding();
                    if (triggerAction != null) {
                        if (!APIEventsLauncher.pushEvent(new Events.CollisionTriggered(collideTrigger, triggerAction)).isCancelled()) {
                            triggerAction.action(obA);
                        }
                    }
                }
            });
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
