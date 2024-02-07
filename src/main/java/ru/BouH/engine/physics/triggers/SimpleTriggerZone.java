package ru.BouH.engine.physics.triggers;

import org.bytedeco.bullet.BulletCollision.*;
import org.bytedeco.bullet.LinearMath.btTransform;
import org.bytedeco.bullet.LinearMath.btVector3;
import org.jetbrains.annotations.NotNull;
import ru.BouH.engine.game.Game;
import ru.BouH.engine.physics.entities.BodyGroup;
import ru.BouH.engine.physics.world.World;
import ru.BouH.engine.physics.world.object.CollidableWorldItem;
import ru.BouH.engine.proxy.IWorld;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class SimpleTriggerZone implements ITriggerZone {
    protected final Zone zone;
    private final Set<CollidableWorldItem> btEnteredBodies;
    private btGhostObject ghostObject;
    private btCollisionShape collisionShape;
    private ITrigger ITriggerEntering;
    private ITrigger ITriggerLeaving;

    public SimpleTriggerZone(Zone zone, ITrigger ITriggerEntering, ITrigger ITriggerLeaving) {
        this.ITriggerEntering = ITriggerEntering;
        this.ITriggerLeaving = ITriggerLeaving;
        this.zone = zone;
        this.btEnteredBodies = new HashSet<>();
    }

    public void onSpawn(IWorld iWorld) {
        this.createGhostZone();
        Game.getGame().getLogManager().log("Created new ITrigger zone: Location=" + this.getZone().getLocation() + " | Size=" + this.getZone().getSize());
    }

    public void onDestroy(IWorld iWorld) {
        this.collisionShape.deallocate();
        Game.getGame().getLogManager().log("Destroyed ITrigger zone: Location=" + this.getZone().getLocation() + " | Size=" + this.getZone().getSize());
    }

    public BodyGroup getBodyGroup() {
        return BodyGroup.GHOST;
    }

    public Zone getZone() {
        return this.zone;
    }

    private void createGhostZone() {
        this.ghostObject = new btGhostObject();
        double d1_1 = this.getZone().getSize().x / 2.0d;
        double d1_2 = this.getZone().getSize().y / 2.0d;
        double d1_3 = this.getZone().getSize().z / 2.0d;
        this.collisionShape = new btBoxShape(new btVector3(d1_1, d1_2, d1_3));
        this.ghostObject.setCollisionShape(this.collisionShape);
        this.ghostObject.setCollisionFlags(btCollisionObject.CF_NO_CONTACT_RESPONSE);
        try (btTransform transform = this.ghostObject.getWorldTransform()) {
            transform.setOrigin(new btVector3(this.getZone().getLocation().x, this.getZone().getLocation().y, this.getZone().getLocation().z));
            this.ghostObject.setWorldTransform(transform);
        }
    }

    public btGhostObject triggerZoneGhostCollision() {
        return this.ghostObject;
    }

    public void onUpdate(IWorld iWorld) {
        World world = (World) iWorld;
        Set<CollidableWorldItem> temp = new HashSet<>();
        for (CollidableWorldItem collidableWorldItem : world.getAllBulletItems()) {
            btOverlappingPairCache btHashedOverlappingPairCache = world.getDynamicsWorld().getPairCache();
            boolean collided = btHashedOverlappingPairCache.findPair(collidableWorldItem.getRigidBodyObject().getBroadphaseHandle(), this.ghostObject.getBroadphaseHandle()) != null;
            if (collided) {
                this.onEnter(collidableWorldItem);
                temp.add(collidableWorldItem);
            }
            btHashedOverlappingPairCache.deallocate();
        }
        if (!this.btEnteredBodies.isEmpty()) {
            for (CollidableWorldItem collidableWorldItem : temp) {
                this.btEnteredBodies.remove(collidableWorldItem);
            }
            Iterator<CollidableWorldItem> collidableWorldItemIterator = this.btEnteredBodies.iterator();
            while (collidableWorldItemIterator.hasNext()) {
                CollidableWorldItem collidableWorldItem = collidableWorldItemIterator.next();
                this.onLeave(collidableWorldItem);
                collidableWorldItemIterator.remove();
            }
        }
        this.btEnteredBodies.addAll(temp);
    }

    public void onEnter(CollidableWorldItem collidableWorldItem) {
        this.getTriggerEntering().trigger(collidableWorldItem);
    }

    public void onLeave(CollidableWorldItem collidableWorldItem) {
        this.getTriggerLeaving().trigger(collidableWorldItem);
    }

    public ITrigger getTriggerEntering() {
        return this.ITriggerEntering;
    }

    public void setTriggerEntering(@NotNull ITrigger ITrigger) {
        this.ITriggerEntering = ITrigger;
    }

    public ITrigger getTriggerLeaving() {
        return this.ITriggerLeaving;
    }

    public void setTriggerLeaving(@NotNull ITrigger ITrigger) {
        this.ITriggerLeaving = ITrigger;
    }
}
