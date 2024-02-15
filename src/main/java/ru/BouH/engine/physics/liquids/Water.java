package ru.BouH.engine.physics.liquids;

import org.bytedeco.bullet.BulletCollision.*;
import org.bytedeco.bullet.LinearMath.btTransform;
import org.bytedeco.bullet.LinearMath.btVector3;
import ru.BouH.engine.game.Game;
import ru.BouH.engine.physics.entities.BodyGroup;
import ru.BouH.engine.physics.entities.PhysEntity;
import ru.BouH.engine.physics.triggers.Zone;
import ru.BouH.engine.physics.world.World;
import ru.BouH.engine.physics.world.object.CollidableWorldItem;
import ru.BouH.engine.proxy.IWorld;

public class Water implements ILiquid {
    private final Zone zone;
    private btGhostObject ghostObject;
    private btCollisionShape collisionShape;

    public Water(Zone zone) {
        this.zone = zone;
    }

    private void createGhostZone() {
        this.ghostObject = new btGhostObject();
        double d1_1 = this.getZone().getSize().x / 2.0d - 0.1f;
        double d1_2 = this.getZone().getSize().y / 2.0d - 0.25d;
        double d1_3 = this.getZone().getSize().z / 2.0d - 0.1f;
        this.collisionShape = new btSphereShape(new btBoxShape(new btVector3(d1_1, d1_2, d1_3)));
        this.ghostObject.setCollisionShape(this.collisionShape);
        this.ghostObject.setCollisionFlags(btCollisionObject.CF_NO_CONTACT_RESPONSE | btCollisionObject.CF_STATIC_OBJECT);
        try (btTransform transform = this.ghostObject.getWorldTransform()) {
            transform.setOrigin(new btVector3(this.getZone().getLocation().x, this.getZone().getLocation().y, this.getZone().getLocation().z));
            this.ghostObject.setWorldTransform(transform);
        }
    }

    @Override
    public void onUpdate(IWorld iWorld) {
        World world = (World) iWorld;
        for (CollidableWorldItem collidableWorldItem : world.getAllBulletItems()) {
            btOverlappingPairCache btHashedOverlappingPairCache = world.getDynamicsWorld().getPairCache();
            boolean collided = btHashedOverlappingPairCache.findPair(collidableWorldItem.getRigidBodyObject().getBroadphaseHandle(), this.ghostObject.getBroadphaseHandle()) != null;
            collidableWorldItem.setInWater(collided);
            btHashedOverlappingPairCache.deallocate();
        }
    }

    @Override
    public btGhostObject triggerZoneGhostCollision() {
        return this.ghostObject;
    }

    public BodyGroup getBodyGroup() {
        return BodyGroup.LIQUID;
    }

    @Override
    public Zone getZone() {
        return this.zone;
    }

    @Override
    public void onSpawn(IWorld iWorld) {
        this.createGhostZone();
        Game.getGame().getLogManager().log("Created new Liquid: Location=" + this.getZone().getLocation() + " | Size=" + this.getZone().getSize());
    }

    @Override
    public void onDestroy(IWorld iWorld) {
        this.collisionShape.deallocate();
        Game.getGame().getLogManager().log("Destroyed Liquid: Location=" + this.getZone().getLocation() + " | Size=" + this.getZone().getSize());
    }
}
