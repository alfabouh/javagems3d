package ru.jgems3d.engine.physics.entities.properties.collision;

public interface IHasCollisionFilter {
    int getCollisionGroup();
    int getCollisionFilter();
    void setCollisionFilter(CollisionFilter... collisionFilters);
    void setCollisionGroup(CollisionFilter... collisionFilters);
}
