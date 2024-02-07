package ru.BouH.engine.physics.triggers;

import org.bytedeco.bullet.BulletCollision.btGhostObject;
import org.joml.Vector3d;
import ru.BouH.engine.physics.entities.BodyGroup;
import ru.BouH.engine.physics.world.World;
import ru.BouH.engine.physics.world.object.IWorldDynamic;
import ru.BouH.engine.physics.world.object.IWorldObject;

public interface ITriggerZone extends IWorldDynamic, IWorldObject {
    Zone getZone();
    btGhostObject triggerZoneGhostCollision();
    BodyGroup getBodyGroup();
}
