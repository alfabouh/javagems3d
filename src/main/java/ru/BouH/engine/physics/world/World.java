package ru.BouH.engine.physics.world;

import org.bytedeco.bullet.BulletCollision.btCollisionObject;
import org.bytedeco.bullet.BulletCollision.btCollisionWorld;
import org.bytedeco.bullet.BulletCollision.btGhostObject;
import org.bytedeco.bullet.BulletDynamics.btDynamicsWorld;
import org.bytedeco.bullet.BulletDynamics.btRigidBody;
import ru.BouH.engine.game.Game;
import ru.BouH.engine.game.exception.GameException;
import ru.BouH.engine.physics.entities.BodyGroup;
import ru.BouH.engine.physics.jb_objects.JBulletEntity;
import ru.BouH.engine.physics.triggers.ITrigger;
import ru.BouH.engine.physics.triggers.ITriggerZone;
import ru.BouH.engine.physics.triggers.SimpleTriggerZone;
import ru.BouH.engine.physics.world.object.CollidableWorldItem;
import ru.BouH.engine.physics.world.object.IWorldDynamic;
import ru.BouH.engine.physics.world.object.WorldItem;
import ru.BouH.engine.physics.world.timer.PhysicsTimer;
import ru.BouH.engine.proxy.IWorld;
import ru.BouH.engine.render.environment.light.Light;

import java.util.*;
import java.util.stream.Collectors;

public final class World implements IWorld {
    private final Set<WorldItem> allWorldItems;
    private final Set<IWorldDynamic> allDynamicItems;
    private final Set<CollidableWorldItem> allBulletItems;
    private final Set<ITriggerZone> triggerZones;
    private final Set<WorldItem> toCleanItems;
    private boolean collectionsWaitingRefresh;
    private int ticks;

    public World() {
        this.allWorldItems = new HashSet<>();
        this.allDynamicItems = new HashSet<>();
        this.toCleanItems = new HashSet<>();
        this.triggerZones = new HashSet<>();
        this.allBulletItems = new HashSet<>();
    }

    public static boolean isItemDynamic(WorldItem worldItem) {
        return worldItem instanceof IWorldDynamic;
    }

    public static boolean isItemJBulletObject(WorldItem worldItem) {
        return worldItem instanceof JBulletEntity;
    }

    public PhysicsTimer getBulletTimer() {
        return Game.getGame().getPhysicThreadManager().getPhysicsTimer();
    }

    public void addInBulletWorld(btCollisionObject btCollisionObject, BodyGroup bodyGroup) {
        this.getBulletTimer().addInWorld(btCollisionObject, bodyGroup);
    }

    public void onWorldStart() {
        Game.getGame().getLogManager().log("Creating local player");
        Game.getGame().getProxy().createLocalPlayer();
        Game.getGame().getLogManager().log("Local player created!");
    }

    public void onWorldUpdate() {
        List<WorldItem> copy1 = new ArrayList<>(this.getAllWorldItems());
        if (this.collectionsWaitingRefresh) {
            synchronized (PhysicsTimer.lock) {
                this.getAllDynamicItems().clear();
                this.getAllDynamicItems().addAll(copy1.stream().filter(World::isItemDynamic).map(e -> (IWorldDynamic) e).collect(Collectors.toList()));
                this.getAllBulletItems().clear();
                this.getAllBulletItems().addAll(copy1.stream().filter(World::isItemJBulletObject).map(e -> (CollidableWorldItem) e).collect(Collectors.toList()));
            }
            this.collectionsWaitingRefresh = false;
        }
        List<IWorldDynamic> copy2 = new ArrayList<>(this.getAllDynamicItems());
        for (IWorldDynamic iWorldDynamic : copy2) {
            iWorldDynamic.onUpdate(this);
        }
        List<ITriggerZone> copy3 = new ArrayList<>(this.getTriggerZones());
        for (ITriggerZone triggerZone : copy3) {
            triggerZone.onUpdate(this);
        }
        this.clearItemsCollection(this.toCleanItems);
        this.toCleanItems.clear();
        this.ticks += 1;
    }

    public void onWorldEnd() {
    }

    public void addLight(Light light) {
        Game.getGame().getProxy().addLight(light);
    }

    public void addItem(WorldItem worldItem) {
        this.addItemInWorld(worldItem);
    }

    public void removeItem(WorldItem worldItem) {
        this.toCleanItems.add(worldItem);
    }

    public void clearAllItems() {
        this.getAllWorldItems().forEach(WorldItem::setDead);
    }

    public btDynamicsWorld getDynamicsWorld() {
        return this.getBulletTimer().getDynamicsWorld();
    }

    public btCollisionWorld getCollisionWorld() {
        return this.getBulletTimer().getCollisionWorld();
    }

    private void clearItemsCollection(Collection<? extends WorldItem> collection) {
        for (WorldItem worldItem : collection) {
            this.collectionsWaitingRefresh = true;
            worldItem.onDestroy(this);
            worldItem.clearLights();
            if (World.isItemJBulletObject(worldItem)) {
                JBulletEntity jbItem = (JBulletEntity) worldItem;
                btRigidBody rigidBody = jbItem.getRigidBodyObject();
                if (rigidBody != null) {
                    this.getBulletTimer().removeRigidBodyFromWorld(rigidBody);
                }
            }
            this.getAllWorldItems().remove(worldItem);
        }
    }

    public int getTicks() {
        return this.ticks;
    }

    public void createSimpleTriggerZone(ITriggerZone.Zone zone, ITrigger ITriggerEnter, ITrigger ITriggerLeave) {
        this.addTriggerZone(new SimpleTriggerZone(zone, ITriggerEnter, ITriggerLeave));
    }

    public void addTriggerZone(ITriggerZone iTriggerZone) {
        if (iTriggerZone == null) {
            throw new GameException("Tried to pass NULL triggerZone in world");
        }
        btGhostObject btCollisionObject = iTriggerZone.createGhostZone();
        this.addInBulletWorld(btCollisionObject, BodyGroup.GHOST);
        this.getTriggerZones().add(iTriggerZone);
    }

    private void addItemInWorld(WorldItem worldItem) throws GameException {
        if (worldItem == null) {
            throw new GameException("Tried to pass NULL item in world");
        }
        this.collectionsWaitingRefresh = true;
        worldItem.onSpawn(this);
        this.getAllWorldItems().add(worldItem);
    }

    public int countItems() {
        return this.getAllWorldItems().size();
    }

    public synchronized Set<CollidableWorldItem> getAllBulletItems() {
        return this.allBulletItems;
    }

    public synchronized Set<IWorldDynamic> getAllDynamicItems() {
        return this.allDynamicItems;
    }

    public synchronized Set<WorldItem> getAllWorldItems() {
        return this.allWorldItems;
    }

    public synchronized Set<ITriggerZone> getTriggerZones() {
        return this.triggerZones;
    }
}
