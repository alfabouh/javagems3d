package javagems3d.physics.world.thread.dynamics;

import api.events.EventBus;
import api.scripting.JavaToJsAPI;
import api.scripting.coding.env.internal.game.init.events.rendering.ogl.JSStopRendererOGLEvent;
import api.scripting.coding.env.internal.util.world.render.processing.JSOpenGLRenderer;
import com.jme3.bullet.CollisionConfiguration;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.SolverMode;
import com.jme3.bullet.SolverType;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.math.Vector3f;
import javagems3d.JGems3D;
import api.events.EventLauncher;
import javagems3d.physics.world.scans.PhysicsWorldHitScans;
import javagems3d.physics.world.thread.JGemsPhysics;
import javagems3d.physics.world.thread.dynamics.extractor.NativesExtractor;
import javagems3d.physics.world.triggers.IHasCollisionTrigger;
import javagems3d.physics.world.triggers.ITriggerAction;
import javagems3d.system.resources.assets.initialization.base.IAssetsInitializer;
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
    private final Set<PhysicsCollisionObject> objectsWithCollideTriggers;
    private PhysicsSpace physicsSpace;

    public DynamicsSystem() {
        this.objectsWithCollideTriggers = SyncManager.createSyncronisedSet();
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
        this.physicsSpace = new PhysicsSpace(new Vector3f(-JGems3D.MAP_MAX_SIZE, -JGems3D.MAP_MAX_SIZE, -JGems3D.MAP_MAX_SIZE), new Vector3f(JGems3D.MAP_MAX_SIZE, JGems3D.MAP_MAX_SIZE, JGems3D.MAP_MAX_SIZE), PhysicsSpace.BroadphaseType.AXIS_SWEEP_3, SolverType.SI, collisionConfiguration);
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

    //TODO
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
                if (obA instanceof IHasCollisionTrigger collideTrigger) {
                    triggerPairs.add(new Pair<>(collideTrigger, obB));
                }
            });
        }
        for (Pair<IHasCollisionTrigger, Object> objectPair : triggerPairs) {
            ITriggerAction triggerAction = objectPair.first().onColliding();
            if (triggerAction != null) {
                if (!EventLauncher.pushEvent(new EventBus.CollisionTriggered(objectPair.first(), triggerAction), null).isCancelled()) {
                    triggerAction.action(objectPair.second());
                }
            }
        }
    }

    public void step(float time, int maxSteps) {
        this.getPhysicsSpace().update(time, maxSteps);
    }

    public void destroy() {
        PhysicsWorldHitScans.destroyGhost();
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