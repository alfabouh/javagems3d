package ru.BouH.engine.physics.triggers;

import org.bytedeco.bullet.BulletCollision.btGhostObject;
import org.joml.Vector3d;
import ru.BouH.engine.physics.world.World;

public interface ITriggerZone {
    ITriggerZone.Zone getZone();

    btGhostObject createGhostZone();

    void onUpdate(World world);

    class Zone {
        private final Vector3d location;
        private final Vector3d size;

        public Zone(Vector3d location, Vector3d size) {
            this.location = location;
            this.size = size;
        }

        public Vector3d getLocation() {
            return this.location;
        }

        public Vector3d getSize() {
            return this.size;
        }
    }
}
