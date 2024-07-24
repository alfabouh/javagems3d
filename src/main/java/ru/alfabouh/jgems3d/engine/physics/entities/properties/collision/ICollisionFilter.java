package ru.alfabouh.jgems3d.engine.physics.entities.properties.collision;

public interface ICollisionFilter {
    int getCollisionGroup();
    int getCollisionFilter();
    void setCollisionFilter(CollisionFilter... collisionFilters);
    void setCollisionGroup(CollisionFilter... collisionFilters);
}
