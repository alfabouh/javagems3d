package ru.BouH.engine.physics.jb_objects;

import org.bytedeco.bullet.BulletCollision.btCollisionObject;
import ru.BouH.engine.physics.entities.BodyGroup;
import ru.BouH.engine.physics.entities.states.EntityState;

public interface JBulletEntity {
    btCollisionObject getBulletObject();
    BodyGroup getBodyIndex();
    EntityState entityState();
    default boolean isValid() {
        return this.getBulletObject() != null && !this.getBulletObject().isNull();
    }
}
