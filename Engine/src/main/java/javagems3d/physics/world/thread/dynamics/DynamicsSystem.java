package javagems3d.physics.world.thread.dynamics;

import api.events.EventBus;
import com.jme3.bullet.CollisionConfiguration;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.SolverType;
import com.jme3.bullet.StepFlag;
import com.jme3.bullet.collision.PersistentManifolds;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.math.Vector3f;
import javagems3d.JGems3D;
import api.events.EventLauncher;
import javagems3d.physics.world.scans.PhysicsWorldHitScans;
import javagems3d.physics.world.thread.JGemsPhysics;
import javagems3d.physics.world.thread.dynamics.extractor.NativesExtractor;
import javagems3d.physics.world.triggers.IHasCollisionTrigger;
import javagems3d.physics.world.triggers.ITriggerAction;
import javagems3d.system.resources.managing.ResourceManager;
import javagems3d.system.service.collections.Pair;
import javagems3d.system.service.exceptions.JGemsRuntimeException;
import javagems3d.system.service.synchronizing.SyncManager;
import logger.Log;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

public class DynamicsSystem {
    public static boolean VALID = false;
    private PhysicsSpace physicsSpace;

    public DynamicsSystem() {
    }

    public void init() {
        if (!DynamicsSystem.VALID) {
            Path path = Paths.get(JGems3D.getEngineFilesFolder().toString(), "natives");
            try {
                String lib = NativesExtractor.extractNativesAndReturnPath(path, JGems3D.get().getOS());
                DynamicsSystem.VALID = true;
                System.load(lib);
                Log.get().info("Injected lib: " + lib);
            } catch (Exception e) {
                throw new JGemsRuntimeException(e);
            }
        }
        EventBus.InitDynamicBulletSpaceEvent dynamicBulletSpaceEvent = new EventBus.InitDynamicBulletSpaceEvent(this, this.physicsSpace);
        CollisionConfiguration collisionConfiguration = new CollisionConfiguration();
        this.physicsSpace = new PhysicsSpace(new Vector3f(-JGems3D.MAP_MAX_SIZE, -JGems3D.MAP_MAX_SIZE, -JGems3D.MAP_MAX_SIZE), new Vector3f(JGems3D.MAP_MAX_SIZE, JGems3D.MAP_MAX_SIZE, JGems3D.MAP_MAX_SIZE), PhysicsSpace.BroadphaseType.AXIS_SWEEP_3, SolverType.SI, collisionConfiguration) {
            @Override
            public boolean needsCollision(PhysicsCollisionObject pcoA, PhysicsCollisionObject pcoB) {
                if (EventLauncher.pushEvent(new EventBus.BulletNeedCollisionEvent(pcoA, pcoB), null).isCancelled()) {
                    return false;
                }
                return (pcoA.getCollisionGroup() & pcoB.getCollideWithGroups()) != 0 &&
                        (pcoB.getCollideWithGroups() & pcoA.getCollisionGroup()) != 0;
            }

            @Override
            public boolean onContactConceived(long manifold_pointId, long persistent_manifoldId, PhysicsCollisionObject pcoA, PhysicsCollisionObject pcoB) {
                EventBus.BulletContactEvent event = new EventBus.BulletContactEvent(EventBus.BulletContactEvent.ContactType.CONCEIVED, pcoA, pcoB, persistent_manifoldId, manifold_pointId);
                if (EventLauncher.pushEvent(event, null).isCancelled()) {
                    return false;
                }
                if (pcoA.getUserObject() instanceof IHasCollisionTrigger hasCollisionTrigger) {
                    if (hasCollisionTrigger.isValid() && !hasCollisionTrigger.collisionTriggerFunc().contractPointCreated(pcoB.getUserObject(), manifold_pointId, persistent_manifoldId)) {
                        return false;
                    }
                }
                if (pcoB.getUserObject() instanceof IHasCollisionTrigger hasCollisionTrigger) {
                    if (hasCollisionTrigger.isValid() && !hasCollisionTrigger.collisionTriggerFunc().contractPointCreated(pcoA.getUserObject(), manifold_pointId, persistent_manifoldId)) {
                        return false;
                    }
                }
                return super.onContactConceived(manifold_pointId, persistent_manifoldId, pcoA, pcoB);
            }

            @Override
            public void onContactEnded(long persistent_manifoldId) {
                PhysicsCollisionObject a = PhysicsCollisionObject.findInstance(PersistentManifolds.getBodyAId(persistent_manifoldId));
                PhysicsCollisionObject b = PhysicsCollisionObject.findInstance(PersistentManifolds.getBodyBId(persistent_manifoldId));
                EventLauncher.pushEvent(new EventBus.BulletContactEvent(EventBus.BulletContactEvent.ContactType.ENDED, a, b, persistent_manifoldId, 0L), null);
                if (a.getUserObject() instanceof IHasCollisionTrigger hasCollisionTrigger) {
                    if (hasCollisionTrigger.isValid()) {
                        hasCollisionTrigger.collisionTriggerFunc().contactEnded(b.getUserObject(), persistent_manifoldId);
                    }
                }
                if (b.getUserObject() instanceof IHasCollisionTrigger hasCollisionTrigger) {
                    if (hasCollisionTrigger.isValid()) {
                        hasCollisionTrigger.collisionTriggerFunc().contactEnded(a.getUserObject(), persistent_manifoldId);
                    }
                }
                super.onContactEnded(persistent_manifoldId);
            }

            @Override
            public void onContactProcessed(PhysicsCollisionObject pcoA, PhysicsCollisionObject pcoB, long manifold_pointId) {
                EventLauncher.pushEvent(new EventBus.BulletContactEvent(EventBus.BulletContactEvent.ContactType.PROCESSED, pcoA, pcoB, 0L, manifold_pointId), null);
                if (pcoA.getUserObject() instanceof IHasCollisionTrigger hasCollisionTrigger) {
                    if (hasCollisionTrigger.isValid()) {
                        hasCollisionTrigger.collisionTriggerFunc().contactContinue(pcoB.getUserObject(), manifold_pointId);
                    }
                }
                if (pcoB.getUserObject() instanceof IHasCollisionTrigger hasCollisionTrigger) {
                    if (hasCollisionTrigger.isValid()) {
                        hasCollisionTrigger.collisionTriggerFunc().contactContinue(pcoA.getUserObject(), manifold_pointId);
                    }
                }
                super.onContactProcessed(pcoA, pcoB, manifold_pointId);
            }

            @Override
            public void onContactStarted(long persistent_manifoldId) {
                PhysicsCollisionObject a = PhysicsCollisionObject.findInstance(PersistentManifolds.getBodyAId(persistent_manifoldId));
                PhysicsCollisionObject b = PhysicsCollisionObject.findInstance(PersistentManifolds.getBodyBId(persistent_manifoldId));
                if (a.getUserObject() instanceof IHasCollisionTrigger hasCollisionTrigger) {
                    if (hasCollisionTrigger.isValid()) {
                        hasCollisionTrigger.collisionTriggerFunc().contactStarted(b.getUserObject(), persistent_manifoldId);
                    }
                }
                if (b.getUserObject() instanceof IHasCollisionTrigger hasCollisionTrigger) {
                    if (hasCollisionTrigger.isValid()) {
                        hasCollisionTrigger.collisionTriggerFunc().contactStarted(a.getUserObject(), persistent_manifoldId);
                    }
                }
                EventLauncher.pushEvent(new EventBus.BulletContactEvent(EventBus.BulletContactEvent.ContactType.STARTED, a, b, persistent_manifoldId, 0L), null);
                super.onContactStarted(persistent_manifoldId);
            }
        };
        this.physicsSpace.setGravity(new Vector3f(0.0f, -10.0f, 0.0f));
        this.physicsSpace.setMaxSubSteps(JGemsPhysics.SUBSTEPS);
        EventLauncher.pushEvent(dynamicBulletSpaceEvent, null);
        if (dynamicBulletSpaceEvent.getNewPhysicsSpace() != null) {
            this.physicsSpace.destroy();
            this.physicsSpace = dynamicBulletSpaceEvent.getNewPhysicsSpace();
        }
        ResourceManager.CREATE_PHYS_FOR_DEFAULT_MODELS();
        PhysicsWorldHitScans.createGhost();
    }

    public void step(float time, int maxSteps) {
        this.getPhysicsSpace().update(time, maxSteps, StepFlag.contactEnded | StepFlag.contactStarted | StepFlag.contactConceived | StepFlag.contactProcessed);
    }

    public void destroy() {
        PhysicsWorldHitScans.destroyGhost();
        this.getPhysicsSpace().destroy();
    }

    public void addCollisionObject(PhysicsCollisionObject collisionObject) {
        this.getPhysicsSpace().addCollisionObject(collisionObject);
    }

    public void removeCollisionObject(PhysicsCollisionObject collisionObject) {
        this.getPhysicsSpace().removeCollisionObject(collisionObject);
    }

    public PhysicsSpace getPhysicsSpace() {
        return this.physicsSpace;
    }
}