package ru.alfabouh.engine.physics.triggers;

import org.bytedeco.bullet.BulletCollision.btPairCachingGhostObject;
import ru.alfabouh.engine.physics.entities.BodyGroup;
import ru.alfabouh.engine.physics.world.object.IWorldDynamic;
import ru.alfabouh.engine.physics.world.object.IWorldObject;

public interface ITriggerZone extends IWorldDynamic, IWorldObject {
    Zone getZone();

    btPairCachingGhostObject triggerZoneGhostCollision();

    BodyGroup getBodyGroup();
}
