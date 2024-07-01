package ru.alfabouh.jgems3d.engine.physics.triggers;

import org.bytedeco.bullet.BulletCollision.btPairCachingGhostObject;
import ru.alfabouh.jgems3d.engine.physics.objects.base.BodyGroup;
import ru.alfabouh.jgems3d.engine.physics.world.object.IWorldDynamic;
import ru.alfabouh.jgems3d.engine.physics.world.object.IWorldObject;

public interface ITriggerZone extends IWorldDynamic, IWorldObject {
    Zone getZone();

    btPairCachingGhostObject triggerZoneGhostCollision();

    BodyGroup getBodyGroup();
}
