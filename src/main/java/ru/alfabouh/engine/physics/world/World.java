package ru.alfabouh.engine.physics.world;

import org.bytedeco.bullet.BulletCollision.btCollisionObject;
import org.bytedeco.bullet.BulletCollision.btCollisionWorld;
import org.bytedeco.bullet.BulletCollision.btGhostObject;
import org.bytedeco.bullet.BulletDynamics.btDynamicsWorld;
import org.joml.Vector3d;
import ru.alfabouh.engine.game.Game;
import ru.alfabouh.engine.game.exception.GameException;
import ru.alfabouh.engine.graph.Graph;
import ru.alfabouh.engine.inventory.IHasInventory;
import ru.alfabouh.engine.physics.entities.BodyGroup;
import ru.alfabouh.engine.physics.jb_objects.JBulletEntity;
import ru.alfabouh.engine.physics.liquids.ILiquid;
import ru.alfabouh.engine.physics.liquids.Water;
import ru.alfabouh.engine.physics.triggers.ITrigger;
import ru.alfabouh.engine.physics.triggers.ITriggerZone;
import ru.alfabouh.engine.physics.triggers.Zone;
import ru.alfabouh.engine.physics.triggers.zones.SimpleTriggerZone;
import ru.alfabouh.engine.physics.world.object.IWorldDynamic;
import ru.alfabouh.engine.physics.world.object.WorldItem;
import ru.alfabouh.engine.physics.world.timer.PhysicsTimer;
import ru.alfabouh.engine.render.environment.light.Light;

import java.util.*;
import java.util.stream.Collectors;

public final class World implements IWorld {
    private final Set<WorldItem> allWorldItems;
    private final Set<IWorldDynamic> allDynamicItems;
    private final Set<JBulletEntity> allBulletItems;
    private final Set<ITriggerZone> triggerZones;
    private final Set<ILiquid> liquids;
    private final Set<WorldItem> toCleanItems;
    private Graph graph;
    private boolean collectionsWaitingRefresh;
    private int ticks;

    public World() {
        this.graph = null;
        this.allWorldItems = new HashSet<>();
        this.allDynamicItems = new HashSet<>();
        this.toCleanItems = new HashSet<>();
        this.triggerZones = new HashSet<>();
        this.allBulletItems = new HashSet<>();
        this.liquids = new HashSet<>();
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
        this.ticks = 0;
    }

    public void onWorldUpdate() {
        Game.getGame().getEngineSystem().getMapLoader().onMapUpdate(this);

        List<WorldItem> copy1 = new ArrayList<>(this.getAllWorldItems());
        if (this.collectionsWaitingRefresh) {
            synchronized (PhysicsTimer.lockObject) {
                this.allDynamicItems.clear();
                this.allBulletItems.clear();
                this.allDynamicItems.addAll(copy1.stream().filter(World::isItemDynamic).map(e -> (IWorldDynamic) e).collect(Collectors.toList()));
                this.allDynamicItems.addAll(this.liquids);
                this.allDynamicItems.addAll(this.triggerZones);
                this.allBulletItems.addAll(copy1.stream().filter(World::isItemJBulletObject).map(e -> (JBulletEntity) e).collect(Collectors.toList()));
            }
            this.collectionsWaitingRefresh = false;
        }
        Set<IWorldDynamic> toUpdate = new HashSet<>(this.allDynamicItems);
        for (IWorldDynamic iWorldDynamic : toUpdate) {
            if (iWorldDynamic instanceof WorldItem) {
                WorldItem worldItem1 = (WorldItem) iWorldDynamic;
                if (worldItem1 instanceof IHasInventory) {
                    ((IHasInventory) worldItem1).inventory().updateInventory(this);
                }
                worldItem1.setPrevPosition(new Vector3d(worldItem1.getPosition()));
            }
            iWorldDynamic.onUpdate(this);
        }
        this.clearItemsCollection(this.toCleanItems);
        this.toCleanItems.clear();
        this.ticks += 1;
    }

    public void onWorldEnd() {
        this.cleanAll();
    }

    public int getTicks() {
        return this.ticks;
    }

    public synchronized Graph getGraph() {
        return this.graph;
    }

    public void setGraph(Graph graph) {
        if (graph == null) {
            Game.getGame().getLogManager().warn("Map Nav Mesh is NULL");
        }
        this.graph = graph;
    }

    public WorldItem getItemByID(int id) {
        Optional<WorldItem> worldItem = this.getAllWorldItems().stream().filter(e -> e.getItemId() == id).findFirst();
        return worldItem.orElse(null);
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

    private void cleanAll() {
        Iterator<WorldItem> worldItemIterator = this.getAllWorldItems().iterator();
        while (worldItemIterator.hasNext()) {
            WorldItem worldItem = worldItemIterator.next();
            worldItem.onDestroy(this);
            worldItemIterator.remove();
        }

        Iterator<ITriggerZone> triggerZoneIterator = this.getTriggerZones().iterator();
        while (triggerZoneIterator.hasNext()) {
            ITriggerZone triggerZone = triggerZoneIterator.next();
            btGhostObject btCollisionObject = triggerZone.triggerZoneGhostCollision();
            if (btCollisionObject != null) {
                this.getDynamicsWorld().removeCollisionObject(btCollisionObject);
                triggerZone.onDestroy(this);
                triggerZoneIterator.remove();
            }
        }

        Iterator<ILiquid> liquidIterator = this.getLiquids().iterator();
        while (liquidIterator.hasNext()) {
            ILiquid liquid = liquidIterator.next();
            btGhostObject btCollisionObject = liquid.triggerZoneGhostCollision();
            if (btCollisionObject != null) {
                this.getDynamicsWorld().removeCollisionObject(btCollisionObject);
                liquid.onDestroy(this);
                liquidIterator.remove();
            }
        }
    }

    private void clearItemsCollection(Collection<? extends WorldItem> collection) {
        for (WorldItem worldItem : collection) {
            this.collectionsWaitingRefresh = true;
            worldItem.onDestroy(this);
            this.getAllWorldItems().remove(worldItem);
        }
    }

    public void createSimpleTriggerZone(Zone zone, ITrigger ITriggerEnter, ITrigger ITriggerLeave) {
        this.addTriggerZone(new SimpleTriggerZone(zone, ITriggerEnter, ITriggerLeave));
    }

    public void createWater(Zone zone) {
        this.addLiquid(new Water(zone));
    }

    public void removeLiquid(ILiquid liquid) {
        if (liquid == null) {
            throw new GameException("Tried to pass NULL liquid in world");
        }
        btGhostObject btCollisionObject = liquid.triggerZoneGhostCollision();
        if (btCollisionObject != null) {
            this.getDynamicsWorld().removeCollisionObject(btCollisionObject);
            liquid.onDestroy(this);
            this.getLiquids().remove(liquid);
        }
    }

    public void addLiquid(ILiquid liquid) {
        if (liquid == null) {
            throw new GameException("Tried to pass NULL liquid in world");
        }
        liquid.onSpawn(this);
        btGhostObject btCollisionObject = liquid.triggerZoneGhostCollision();
        this.addInBulletWorld(btCollisionObject, liquid.getBodyGroup());
        this.getLiquids().add(liquid);
    }

    public void removeTriggerZone(ITriggerZone iTriggerZone) {
        if (iTriggerZone == null) {
            throw new GameException("Tried to pass NULL triggerZone in world");
        }
        btGhostObject btCollisionObject = iTriggerZone.triggerZoneGhostCollision();
        if (btCollisionObject != null) {
            this.getDynamicsWorld().removeCollisionObject(btCollisionObject);
            iTriggerZone.onDestroy(this);
            this.getTriggerZones().remove(iTriggerZone);
        }
    }

    public void addTriggerZone(ITriggerZone iTriggerZone) {
        if (iTriggerZone == null) {
            throw new GameException("Tried to pass NULL triggerZone in world");
        }
        iTriggerZone.onSpawn(this);
        btGhostObject btCollisionObject = iTriggerZone.triggerZoneGhostCollision();
        if (btCollisionObject != null) {
            this.addInBulletWorld(btCollisionObject, iTriggerZone.getBodyGroup());
            this.getTriggerZones().add(iTriggerZone);
        }
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


    public synchronized Set<ILiquid> getLiquids() {
        return this.liquids;
    }

    public synchronized Set<JBulletEntity> getAllBulletItems() {
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
