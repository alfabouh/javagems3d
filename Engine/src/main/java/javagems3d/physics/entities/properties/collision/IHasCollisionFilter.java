package javagems3d.physics.entities.properties.collision;

public interface IHasCollisionFilter {
    int getCollisionGroup();

    void setCollisionGroup(CollisionType... collisionTypes);

    int getCollisideWithGroups();

    void setCollideWithGroups(CollisionType... collisionTypes);
}
