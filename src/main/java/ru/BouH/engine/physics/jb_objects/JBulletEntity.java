package ru.BouH.engine.physics.jb_objects;

import ru.BouH.engine.physics.entities.BodyGroup;

public interface JBulletEntity {
    RigidBodyObject getRigidBodyObject();

    BodyGroup getBodyIndex();

    default boolean isValid() {
        return this.getRigidBodyObject() != null && !this.getRigidBodyObject().isNull() && this.getRigidBodyObject().isInWorld();
    }
}
