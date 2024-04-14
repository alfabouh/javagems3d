package ru.alfabouh.engine.physics.triggers.zones;

import org.bytedeco.bullet.BulletCollision.btBoxShape;
import org.bytedeco.bullet.BulletCollision.btCollisionObject;
import org.bytedeco.bullet.BulletCollision.btCollisionShape;
import org.bytedeco.bullet.BulletCollision.btPairCachingGhostObject;
import org.bytedeco.bullet.LinearMath.btTransform;
import org.bytedeco.bullet.LinearMath.btVector3;
import org.jetbrains.annotations.NotNull;
import ru.alfabouh.engine.game.Game;
import ru.alfabouh.engine.physics.entities.BodyGroup;
import ru.alfabouh.engine.physics.jb_objects.JBulletEntity;
import ru.alfabouh.engine.physics.triggers.ITrigger;
import ru.alfabouh.engine.physics.triggers.ITriggerZone;
import ru.alfabouh.engine.physics.triggers.Zone;
import ru.alfabouh.engine.physics.world.IWorld;
import ru.alfabouh.engine.physics.world.World;
import ru.alfabouh.engine.physics.world.object.WorldItem;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class SimpleTriggerZone implements ITriggerZone {
    protected final Zone zone;
    protected final Set<JBulletEntity> btEnteredBodies;
    private btPairCachingGhostObject ghostObject;
    private btCollisionShape collisionShape;
    private ITrigger ITriggerEntering;
    private ITrigger ITriggerLeaving;
    private int filter;

    public SimpleTriggerZone(Zone zone, ITrigger ITriggerEntering, ITrigger ITriggerLeaving) {
        this.ITriggerEntering = ITriggerEntering;
        this.ITriggerLeaving = ITriggerLeaving;
        this.zone = zone;
        this.filter = 0;
        this.btEnteredBodies = new HashSet<>();
    }

    public void onSpawn(IWorld iWorld) {
        this.createGhostZone();
        Game.getGame().getLogManager().log("Created new ITrigger zone: Location=" + this.getZone().getLocation() + " | Size=" + this.getZone().getSize());
    }

    public void onDestroy(IWorld iWorld) {
        this.collisionShape.deallocate();
        this.ghostObject = null;
        Game.getGame().getLogManager().log("Destroyed ITrigger zone: Location=" + this.getZone().getLocation() + " | Size=" + this.getZone().getSize());
    }

    public int getFilter() {
        return this.filter;
    }

    public void setFilter(int filter) {
        this.filter = filter;
    }

    public Zone getZone() {
        return this.zone;
    }

    public btPairCachingGhostObject triggerZoneGhostCollision() {
        return this.ghostObject;
    }

    public BodyGroup getBodyGroup() {
        return BodyGroup.GHOST;
    }

    private void createGhostZone() {
        this.ghostObject = new btPairCachingGhostObject();

        double d1_1 = this.getZone().getSize().x / 2.0d;
        double d1_2 = this.getZone().getSize().y / 2.0d;
        double d1_3 = this.getZone().getSize().z / 2.0d;
        this.collisionShape = new btBoxShape(new btVector3(d1_1, d1_2, d1_3));
        this.ghostObject.setCollisionShape(this.collisionShape);
        this.ghostObject.setCollisionFlags(btCollisionObject.CF_NO_CONTACT_RESPONSE | btCollisionObject.CF_STATIC_OBJECT | btCollisionObject.CF_ANISOTROPIC_FRICTION_DISABLED);
        try (btTransform transform = this.ghostObject.getWorldTransform()) {
            transform.setOrigin(new btVector3(this.getZone().getLocation().x, this.getZone().getLocation().y, this.getZone().getLocation().z));
            this.ghostObject.setWorldTransform(transform);
        }
    }

    public void onUpdate(IWorld iWorld) {
        World world = (World) iWorld;
        Set<JBulletEntity> temp = new HashSet<>();
        int num = this.triggerZoneGhostCollision().getOverlappingPairCache().getNumOverlappingPairs();
        for (int i = 0; i < num; i++) {
            btCollisionObject btCollisionObject = this.triggerZoneGhostCollision().getOverlappingPairs().get(i);
            WorldItem worldItem = world.getItemByID(btCollisionObject.getUserIndex2());
            if (worldItem instanceof JBulletEntity) {
                JBulletEntity bullet = (JBulletEntity) worldItem;
                if ((bullet.getBodyIndex().getGroup() & this.getFilter()) != 0) {
                    if (this.getTriggerEntering() != null) {
                        this.onEnter(bullet);
                    }
                    temp.add(bullet);
                }
            }
        }
        if (!this.btEnteredBodies.isEmpty()) {
            for (JBulletEntity bullet : temp) {
                this.btEnteredBodies.remove(bullet);
            }
            Iterator<JBulletEntity> collidableWorldItemIterator = this.btEnteredBodies.iterator();
            while (collidableWorldItemIterator.hasNext()) {
                JBulletEntity bullet = collidableWorldItemIterator.next();
                if (this.getTriggerLeaving() != null) {
                    this.onLeave(bullet);
                }
                collidableWorldItemIterator.remove();
            }
        }
        this.btEnteredBodies.addAll(temp);
    }

    public void destroy(World world) {
        world.removeTriggerZone(this);
    }

    public void onEnter(JBulletEntity entity) {
        this.getTriggerEntering().trigger(entity);
    }

    public void onLeave(JBulletEntity entity) {
        this.getTriggerLeaving().trigger(entity);
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
