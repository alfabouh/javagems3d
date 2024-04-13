package ru.BouH.engine.physics.triggers;

import org.bytedeco.bullet.BulletCollision.btGhostObject;
import org.bytedeco.bullet.BulletCollision.btPairCachingGhostObject;
import ru.BouH.engine.physics.entities.BodyGroup;
import ru.BouH.engine.physics.world.object.IWorldDynamic;
import ru.BouH.engine.physics.world.object.IWorldObject;

public interface ITriggerZone extends IWorldDynamic, IWorldObject {
    Zone getZone();

    btPairCachingGhostObject triggerZoneGhostCollision();

    BodyGroup getBodyGroup();
}
