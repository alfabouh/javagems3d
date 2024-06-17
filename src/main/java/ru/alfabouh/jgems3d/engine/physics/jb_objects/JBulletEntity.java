package ru.alfabouh.jgems3d.engine.physics.jb_objects;

import org.bytedeco.bullet.BulletCollision.btCollisionObject;
import ru.alfabouh.jgems3d.engine.physics.entities.BodyGroup;
import ru.alfabouh.jgems3d.engine.physics.entities.states.EntityState;

public interface JBulletEntity {
    btCollisionObject getBulletObject();

    BodyGroup getBodyIndex();

    EntityState entityState();

    default boolean isValid() {
        return this.getBulletObject() != null && !this.getBulletObject().isNull();
    }
}